package com.example.dao;

import com.example.beans.User;

public interface UserDAO {
    boolean register(User user);
    User findByEmail(String email);
}
