package com.greenstore.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;

/**
 * DataLoader - проверяет подключение к БД.
 * Данные (товары, категории, пользователи) загружаются через database/init.sql при первом старте БД.
 * Этот класс НЕ создает пользователей - только проверяет соединение.
 */
public class DataLoader {
    
    public static void loadData(ProductService productService) {
        String url = System.getenv("DB_URL");
        String user = System.getenv("DB_USER");
        String password = System.getenv("DB_PASSWORD");

        if (url == null) {
            // Добавляем явное указание кодировки UTF-8 для JDBC соединения
            url = "jdbc:mysql://db:3306/greenstore?useSSL=false&allowPublicKeyRetrieval=true" +
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
                    System.out.println("Database connection successful");
                    
                    // Устанавливаем кодировку для соединения
                    try (Statement stmt = conn.createStatement()) {
                        stmt.execute("SET NAMES utf8mb4");
                        stmt.execute("SET CHARACTER SET utf8mb4");
                    }
                    
                    // Проверяем наличие данных (для информации)
                    try (Statement stmt = conn.createStatement();
                         ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM products")) {
                        if (rs.next() && rs.getInt(1) > 0) {
                            System.out.println("Products found in database (loaded via init.sql)");
                        } else {
                            System.out.println("Warning: No products found. Check database/init.sql");
                        }
                    }
                    
                    // Пользователи создаются через database/init.sql с правильным BCrypt хешем
                    System.out.println("Users loaded from database/init.sql");
                    
                    System.out.println("=== GreenStore Ready ===");
                    return;
                    
                }
            } catch (Exception e) {
                System.err.println("Attempt " + attempt + "/" + maxRetries + ": " + e.getMessage());
                if (attempt == maxRetries) {
                    System.err.println("Failed to connect to database after " + maxRetries + " attempts");
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
}