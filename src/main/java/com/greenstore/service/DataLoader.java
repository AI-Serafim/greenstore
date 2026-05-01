package com.greenstore.service;

import com.greenstore.dao.UserDAO;
import com.greenstore.model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.SQLException;
import com.greenstore.util.DatabaseConnection;

/**
 * Сервис для инициализации тестовых данных
 */
public class DataLoader {
    
    private final UserDAO userDAO = new UserDAO();
    
    public void init() {
        // Очищаем таблицу пользователей перед созданием тестовых данных
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn != null) {
                // Проверяем существование таблицы
                java.sql.DatabaseMetaData meta = conn.getMetaData();
                java.sql.ResultSet tables = meta.getTables(null, null, "users", null);
                
                if (tables.next()) {
                    // Таблица существует, очищаем её
                    conn.createStatement().executeUpdate("DELETE FROM users");
                    conn.commit(); // Явный коммит
                    System.out.println("Users table cleared successfully");
                } else {
                    System.out.println("Users table does not exist yet, will be created by JPA/Hibernate or init.sql");
                }
                tables.close();
                conn.close();
            }
        } catch (SQLException e) {
            System.err.println("Error clearing users table: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Небольшая задержка перед созданием пользователей
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Создаем тестового пользователя
        String hashedPassword = BCrypt.hashpw("password123", BCrypt.gensalt(10));
        User user = new User("test@example.com", hashedPassword, "Иван", "Петров");
        user.setPhone("+7 (999) 123-45-67");
        user.setAddress("г. Москва, ул. Примерная, д. 1");
        user.setRole(User.Role.USER);
        boolean userCreated = userDAO.create(user);
        if (userCreated) {
            System.out.println("Test user created successfully: test@example.com / password123");
        } else {
            System.err.println("Failed to create test user");
        }
        
        // Создаем администратора
        hashedPassword = BCrypt.hashpw("password123", BCrypt.gensalt(10));
        User admin = new User("admin@greenstore.com", hashedPassword, "Админ", "Админов");
        admin.setPhone("+7 (999) 000-00-00");
        admin.setAddress("г. Москва");
        admin.setRole(User.Role.ADMIN);
        boolean adminCreated = userDAO.create(admin);
        if (adminCreated) {
            System.out.println("Admin user created successfully: admin@greenstore.com / password123");
        } else {
            System.err.println("Failed to create admin user");
        }
    }
}
