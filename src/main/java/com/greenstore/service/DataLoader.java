package com.greenstore.service;

import com.greenstore.dao.UserDAO;
import com.greenstore.model.User;
import org.mindrot.jbcrypt.BCrypt;

/**
 * Сервис для инициализации тестовых данных
 */
public class DataLoader {
    
    private final UserDAO userDAO = new UserDAO();
    
    public void init() {
        // Создаем тестового пользователя, если он не существует
        if (!userDAO.findByEmail("test@example.com").isPresent()) {
            String hashedPassword = BCrypt.hashpw("password123", BCrypt.gensalt(10));
            User user = new User("test@example.com", hashedPassword, "Иван", "Петров");
            user.setPhone("+7 (999) 123-45-67");
            user.setAddress("г. Москва, ул. Примерная, д. 1");
            user.setRole(User.Role.USER);
            userDAO.create(user);
            System.out.println("Test user created: test@example.com / password123");
        }
        
        // Создаем администратора, если он не существует
        if (!userDAO.findByEmail("admin@greenstore.com").isPresent()) {
            String hashedPassword = BCrypt.hashpw("password123", BCrypt.gensalt(10));
            User admin = new User("admin@greenstore.com", hashedPassword, "Админ", "Админов");
            admin.setPhone("+7 (999) 000-00-00");
            admin.setAddress("г. Москва");
            admin.setRole(User.Role.ADMIN);
            userDAO.create(admin);
            System.out.println("Admin user created: admin@greenstore.com / password123");
        }
    }
}
