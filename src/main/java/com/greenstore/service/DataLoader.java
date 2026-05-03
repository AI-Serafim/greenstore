package com.greenstore.service;

import org.mindrot.jbcrypt.BCrypt;
import java.sql.*;

/**
 * DataLoader - проверяет подключение к БД, создает тестовых пользователей с правильным BCrypt хешем.
 * Данные (товары, категории) загружаются через database/init.sql при первом старте БД.
 */
public class DataLoader {
    
    // Правильный BCrypt хеш для пароля "password123"
    private static final String PASSWORD_HASH = "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy";
    
    public static void loadData(ProductService productService) {
        String url = System.getenv("DB_URL");
        String user = System.getenv("DB_USER");
        String password = System.getenv("DB_PASSWORD");

        if (url == null) {
            // Добавляем явное указание кодировки UTF-8 для JDBC соединения
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
                    System.out.println("=== GreenStore Starting ===");
                    System.out.println("✓ Подключение к БД успешно");
                    
                    // Устанавливаем кодировку для соединения
                    try (Statement stmt = conn.createStatement()) {
                        stmt.execute("SET NAMES utf8mb4");
                        stmt.execute("SET CHARACTER SET utf8mb4");
                    }
                    
                    // Проверяем наличие данных (для информации)
                    try (Statement stmt = conn.createStatement();
                         ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM products")) {
                        if (rs.next() && rs.getInt(1) > 0) {
                            System.out.println("✓ Товары найдены в БД (загружены через init.sql)");
                        } else {
                            System.out.println("⚠ Товары не найдены. Проверьте database/init.sql");
                        }
                    }
                    
                    // Создаем тестовых пользователей с гарантированно рабочим хешем
                    createTestUsers(conn);
                    
                    System.out.println("=== Ready ===");
                    return;
                    
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
    
    /**
     * Создает тестовых пользователей с правильным BCrypt хешем пароля
     */
    private static void createTestUsers(Connection conn) throws SQLException {
        String checkSql = "SELECT COUNT(*) FROM users WHERE email = ?";
        String insertSql = "INSERT INTO users (email, password_hash, first_name, last_name, phone, address, role) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        // Admin пользователь
        ensureUserExists(conn, checkSql, insertSql, 
            "admin@greenstore.com", PASSWORD_HASH, "Админ", "Системный", 
            "+7 (999) 000-00-00", "г. Москва", "ADMIN");
        
        // Test пользователь
        ensureUserExists(conn, checkSql, insertSql,
            "test@example.com", PASSWORD_HASH, "Иван", "Петров",
            "+7 (999) 123-45-67", "г. Москва, ул. Тестовая, д. 1", "USER");
        
        System.out.println("✓ Тестовые пользователи созданы/проверены (пароль: password123)");
    }
    
    private static void ensureUserExists(Connection conn, String checkSql, String insertSql,
                                         String email, String passwordHash, String firstName, 
                                         String lastName, String phone, String address, String role) throws SQLException {
        try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setString(1, email);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    // Пользователь существует - обновляем хеш пароля на правильный
                    String updateSql = "UPDATE users SET password_hash = ? WHERE email = ?";
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                        updateStmt.setString(1, passwordHash);
                        updateStmt.setString(2, email);
                        updateStmt.executeUpdate();
                    }
                    System.out.println("  → Обновлен хеш пароля для: " + email);
                } else {
                    // Пользователя нет - создаем
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                        insertStmt.setString(1, email);
                        insertStmt.setString(2, passwordHash);
                        insertStmt.setString(3, firstName);
                        insertStmt.setString(4, lastName);
                        insertStmt.setString(5, phone);
                        insertStmt.setString(6, address);
                        insertStmt.setString(7, role);
                        insertStmt.executeUpdate();
                    }
                    System.out.println("  → Создан пользователь: " + email);
                }
            }
        }
    }
}