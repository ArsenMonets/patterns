package com.github.arsenmonets.service;

import com.github.arsenmonets.dao.UserDAO;
import com.github.arsenmonets.dao.ProductDAO;
import com.github.arsenmonets.dao.OrderDAO;
import com.github.arsenmonets.models.Product;
import com.github.arsenmonets.models.Order;

import java.util.List;

public class StoreManager {
    private final UserDAO userDAO;
    private final ProductDAO productDAO;
    private final OrderDAO orderDAO;
    private int orderCounter;

    public StoreManager(UserDAO userDAO, ProductDAO productDAO, OrderDAO orderDAO) {
        this.userDAO = userDAO;
        this.productDAO = productDAO;
        this.orderDAO = orderDAO;
        this.orderCounter = 0;
    }

    public List<Product> getProducts() {
        return productDAO.findAll();
    }

    public List<Order> getOrders() {
        return orderDAO.findAll();
    }

    public List<Order> getOrdersByCustomer(String customerName) {
        return orderDAO.findByCustomer(customerName);
    }

    public void addOrder(Order order) {
        order.setId(++orderCounter);
        orderDAO.save(order);
    }

    public void updateOrderStatus(int orderId, String status) {
        Order order = orderDAO.findById(orderId);
        if (order != null) {
            order.setStatus(status);
            orderDAO.update(order);
        }
    }

    public List<?> getUsers() {
        return userDAO.findAll();
    }

    public void addProduct(Product product) {
        productDAO.save(product);
    }

    public void updateProduct(Product product) {
        productDAO.update(product);
    }

    public void deleteProduct(String productName) {
        productDAO.delete(productName);
    }

    public Order getOrderById(int orderId) {
        return orderDAO.findById(orderId);
    }
}
