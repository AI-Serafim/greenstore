package com.greenstore.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

public class DatabaseConnection {
    
    private static final Logger LOGGER = Logger.getLogger(DatabaseConnection.class.getName());
    
    private static final String URL = String.format(
        "jdbc:mysql://%s:%s/%s?useSSL=false&allowPublicKeyRetrieval=true" +
        "&serverTimezone=UTC&characterEncoding=UTF-8&useUnicode=true&connectionCollation=utf8mb4_unicode_ci",
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
        Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        // Устанавливаем кодировку для каждого нового соединения
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("SET NAMES utf8mb4");
            stmt.execute("SET CHARACTER SET utf8mb4");
        } catch (SQLException e) {
            LOGGER.warning("Failed to set charset: " + e.getMessage());
        }
        return connection;
    }
    
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                LOGGER.warning("Failed to close connection: " + e.getMessage());
            }
        }
    }
}