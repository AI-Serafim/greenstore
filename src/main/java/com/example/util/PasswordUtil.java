package com.example.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Утилита для безопасного хэширования и проверки паролей
 */
public class PasswordUtil {
    
    /**
     * Хэширует пароль с использованием BCrypt
     * @param plainPassword исходный пароль
     * @return хэшированный пароль
     */
    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }
    
    /**
     * Проверяет соответствие пароля хэшу
     * @param plainPassword исходный пароль
     * @param hashedPassword хэшированный пароль из БД
     * @return true если пароль совпадает, false иначе
     */
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (Exception e) {
            return false;
        }
    }
}
