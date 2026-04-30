package com.greenstore.service;

import com.greenstore.dao.UserDAO;
import com.greenstore.model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;

/**
 * Сервис для работы с аутентификацией
 */
public class AuthService {
    
    private final UserDAO userDAO = new UserDAO();
    
    public boolean register(String email, String password, String firstName, String lastName) {
        // Проверяем существование пользователя
        if (userDAO.findByEmail(email).isPresent()) {
            return false;
        }
        
        // Хэшируем пароль
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(10));
        
        User user = new User(email, hashedPassword, firstName, lastName);
        return userDAO.create(user);
    }
    
    public Optional<User> login(String email, String password) {
        Optional<User> userOpt = userDAO.findByEmail(email);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (BCrypt.checkpw(password, user.getPasswordHash())) {
                return userOpt;
            }
        }
        
        return Optional.empty();
    }
    
    public Optional<User> getUserById(int id) {
        return userDAO.findById(id);
    }
}
