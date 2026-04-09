package com.github.arsenmonets;

import java.util.List;

public class AuthService {
    private List<User> users;

    public AuthService(List<User> users) {
        this.users = users;
    }

    public User login(String u, String p) {
        for (User user : users) {
            if (user.username.equals(u) && user.password.equals(p)) return user;
        }
        return null;
    }

    public boolean register(String u, String p, String r) {
        for (User user : users) {
            if (user.username.equals(u)) return false;
        }
        if (!r.equals("Customer") && !r.equals("Seller")) return false;
        users.add(new User(u, p, r));
        return true;
    }
}