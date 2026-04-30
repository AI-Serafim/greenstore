package com.greenstore.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Утилита для получения соединений с базой данных
 * Поддерживает конфигурацию через переменные окружения для Docker
 */
public class DatabaseConnection {
    
    // Читаем настройки из переменных окружения (для Docker) или используем значения по умолчанию (для локальной разработки)
    private static final String DB_HOST = System.getenv("DB_HOST") != null ? System.getenv("DB_HOST") : "localhost";
    private static final String DB_PORT = System.getenv("DB_PORT") != null ? System.getenv("DB_PORT") : "3306";
    private static final String DB_NAME = System.getenv("DB_NAME") != null ? System.getenv("DB_NAME") : "greenstore_db";
    private static final String DB_USER = System.getenv("DB_USER") != null ? System.getenv("DB_USER") : "greenstore_user";
    private static final String DB_PASSWORD = System.getenv("DB_PASSWORD") != null ? System.getenv("DB_PASSWORD") : "greenstore_password";
    
    private static final String URL = "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME + "?useSSL=false&serverTimezone=UTC&characterEncoding=UTF-8&allowPublicKeyRetrieval=true";
    private static final String USERNAME = DB_USER;
    private static final String PASSWORD = DB_PASSWORD;
    
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
