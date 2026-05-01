package com.greenstore.service;  

import com.greenstore.dao.UserDAO;
import com.greenstore.model.User;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.*;

public class DataLoader { 
    
    // Публичный метод для инициализации данных
    public void init() {
        String url = System.getenv("DB_URL");
        String user = System.getenv("DB_USER");
        String password = System.getenv("DB_PASSWORD");

        if (url == null) url = "jdbc:mysql://db:3306/greenstore_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
        if (user == null) user = "greenstore_user";
        if (password == null) password = "greenstore_password";

        try {
            Thread.sleep(5000); // Ждём БД
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            try (Connection conn = DriverManager.getConnection(url, user, password)) {
                // Очищаем таблицу
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute("DELETE FROM users");
                }
                
                // Создаём тестовых пользователей
                String adminEmail = "admin@greenstore.com";
                String adminPass = BCrypt.hashpw("password123", BCrypt.gensalt());
                String testEmail = "test@example.com";
                String testPass = BCrypt.hashpw("password123", BCrypt.gensalt());
                
                insertUser(conn, adminEmail, adminPass, "Admin", "Adminov", true);
                insertUser(conn, testEmail, testPass, "Test", "Testov", false);
                
                System.out.println("✓ Тестовые пользователи созданы");
            }
        } catch (Exception e) {
            System.err.println("Ошибка инициализации: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void insertUser(Connection conn, String email, String passwordHash, 
                           String firstName, String lastName, boolean isAdmin) throws SQLException {
        String sql = "INSERT INTO users (email, password, first_name, last_name, is_admin, created_at) VALUES (?, ?, ?, ?, ?, NOW())";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.setString(2, passwordHash);
            pstmt.setString(3, firstName);
            pstmt.setString(4, lastName);
            pstmt.setBoolean(5, isAdmin);
            pstmt.executeUpdate();
        }
    }
}