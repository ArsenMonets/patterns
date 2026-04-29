package com.github.arsenmonets.dao;

import com.github.arsenmonets.models.Order;
import java.util.List;

public interface OrderDAO {
    boolean save(Order order);

    Order findById(int id);

    List<Order> findAll();

    List<Order> findByCustomer(String customerName);

    boolean update(Order order);

    boolean delete(int id);
}
