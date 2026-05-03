package com.greenstore.service;

import java.sql.*;

/**
 * DataLoader - проверяет подключение к БД и логирует успешный старт.
 * Все данные (товары, пользователи) загружаются через database/init.sql при первом старте БД.
 */
public class DataLoader {
    
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
                    
                    // Проверяем наличие данных (для информации)
                    try (Statement stmt = conn.createStatement();
                         ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM products")) {
                        if (rs.next() && rs.getInt(1) > 0) {
                            System.out.println("✓ Товары найдены в БД (загружены через init.sql)");
                        } else {
                            System.out.println("⚠ Товары не найдены. Проверьте database/init.sql");
                        }
                    }
                    
                    try (Statement stmt = conn.createStatement();
                         ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users WHERE email IN ('admin@greenstore.com', 'test@example.com')")) {
                        if (rs.next() && rs.getInt(1) >= 2) {
                            System.out.println("✓ Тестовые пользователи найдены (пароль: password123)");
                        } else {
                            System.out.println("⚠ Тестовые пользователи не найдены. Проверьте database/init.sql");
                        }
                    }
                    
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
}