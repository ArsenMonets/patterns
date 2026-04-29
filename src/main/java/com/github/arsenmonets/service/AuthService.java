package com.github.arsenmonets.service;

import com.github.arsenmonets.dao.UserDAO;
import com.github.arsenmonets.models.User;
import com.github.arsenmonets.util.PasswordUtil;
import com.github.arsenmonets.exception.AuthenticationException;

public class AuthService {
    private final UserDAO userDAO;

    public AuthService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public User login(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            throw new AuthenticationException("Username cannot be empty");
        }
        if (password == null || password.isEmpty()) {
            throw new AuthenticationException("Password cannot be empty");
        }
        User user = userDAO.findByUsername(username);
        if (user != null && PasswordUtil.checkPassword(password, user.getPassword())) {
            return user;
        }
        throw new AuthenticationException("Invalid username or password");
    }

    public boolean register(String username, String password, String role) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        if (role == null || (!role.equals("Customer") && !role.equals("Seller"))) {
            throw new IllegalArgumentException("Invalid role. Must be Customer or Seller");
        }
        if (userDAO.findByUsername(username) != null) {
            throw new IllegalArgumentException("Username already exists");
        }
        String hashedPassword = PasswordUtil.hashPassword(password);
        User newUser = new User(username, hashedPassword, role);
        return userDAO.save(newUser);
    }

    public void validatePasswordMatch(String password, String confirmPassword) {
        if (password == null || confirmPassword == null || !password.equals(confirmPassword)) {
            throw new IllegalArgumentException("Passwords do not match");
        }
    }
}
