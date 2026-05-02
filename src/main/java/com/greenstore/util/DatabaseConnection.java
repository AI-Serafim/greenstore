package com.greenstore.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Утилита для получения соединений с базой данных
 */
public class DatabaseConnection {
    
    private static final String URL = String.format(
        "jdbc:mysql://%s:%s/%?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC" +
"&characterEncoding=UTF-8&useUnicode=true&connectionCollation=utf8mb4_unicode_ci",
        System.getenv("DB_HOST"),
        System.getenv("DB_PORT"),
        System.getenv("DB_NAME")
    );
    
    private static final String USERNAME = System.getenv("DB_USER");
    private static final String PASSWORD = System.getenv("DB_PASSWORD");
    
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Не удалось загрузить драйвер MySQL", e);
        }
    }
    
    private DatabaseConnection() {}
    
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }
    
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                // Логирование ошибки закрытия соединения
            }
        }
    }
}
