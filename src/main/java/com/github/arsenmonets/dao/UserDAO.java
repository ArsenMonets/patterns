package com.github.arsenmonets.dao;

import com.github.arsenmonets.models.User;
import java.util.List;

public interface UserDAO {
    boolean save(User user);

    User findByUsername(String username);

    List<User> findAll();

    boolean update(User user);

    boolean delete(String username);
}
