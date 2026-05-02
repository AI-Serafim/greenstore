package com.greenstore.service;

import com.greenstore.dao.UserDAO;
import com.greenstore.model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;

public class DataLoader {
    
    public void init() {
        String url = System.getenv("DB_URL");
        String user = System.getenv("DB_USER");
        String password = System.getenv("DB_PASSWORD");

        if (url == null) {
            url = "jdbc:mysql://db:3306/greenstore_db?useSSL=false&allowPublicKeyRetrieval=true" +
                  "&serverTimezone=UTC&characterEncoding=UTF-8&useUnicode=true";
        }
        if (user == null) user = "greenstore_user";
        if (password == null) password = "greenstore_password";

        int maxRetries = 15;
        int retryDelayMs = 2000;
        
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                
                try (Connection conn = DriverManager.getConnection(url, user, password)) {
                    // Очищаем таблицу пользователей для тестов
                    try (Statement stmt = conn.createStatement()) {
                        stmt.execute("DELETE FROM users");
                    }
                    
                    // Создаём тестовых пользователей
                    String adminPass = BCrypt.hashpw("password123", BCrypt.gensalt());
                    String testPass = BCrypt.hashpw("password123", BCrypt.gensalt());
                    
                    insertUser(conn, "admin@greenstore.com", adminPass, "Admin", "Adminov", true);
                    insertUser(conn, "test@example.com", testPass, "Test", "Testov", false);
                    
                    System.out.println("✓ Тестовые пользователи созданы");
                    return; // ✅ Успех
                }
            } catch (Exception e) {
                System.err.println("⏳ Попытка " + attempt + "/" + maxRetries + ": " + e.getMessage());
                if (attempt == maxRetries) {
                    System.err.println("❌ Не удалось подключиться к БД после " + maxRetries + " попыток");
                    e.printStackTrace();
                    return;
                }
                try {
                    Thread.sleep(retryDelayMs);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
    }
    
    private void insertUser(Connection conn, String email, String passwordHash, 
                           String firstName, String lastName, boolean isAdmin) throws SQLException {
        String sql = "INSERT INTO users (email, password_hash, first_name, last_name, role) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.setString(2, passwordHash);
            pstmt.setString(3, firstName);
            pstmt.setString(4, lastName);
            pstmt.setString(5, isAdmin ? "ADMIN" : "USER");
            pstmt.executeUpdate();
        }
    }
}