package com.github.arsenmonets.repository;

import com.github.arsenmonets.dao.UserDAO;
import com.github.arsenmonets.dao.ProductDAO;
import com.github.arsenmonets.dao.OrderDAO;
import com.github.arsenmonets.models.Product;
import com.github.arsenmonets.models.Order;

import java.util.List;

public class StoreRepository {
    private final UserDAO userDAO;
    private final ProductDAO productDAO;
    private final OrderDAO orderDAO;
    private int orderCounter;
    private int productCounter;

    public StoreRepository(UserDAO userDAO, ProductDAO productDAO, OrderDAO orderDAO) {
        this.userDAO = userDAO;
        this.productDAO = productDAO;
        this.orderDAO = orderDAO;
        this.orderCounter = 0;
        this.productCounter = 0;
    }

    public List<Product> getProducts() {
        return productDAO.findAll();
    }

    public Product getProductById(int id) {
        return productDAO.findById(id);
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
        product.setId(++productCounter);
        productDAO.save(product);
    }

    public void updateProduct(Product product) {
        productDAO.update(product);
    }

    public void deleteProduct(int productId) {
        productDAO.delete(productId);
    }

    public Order getOrderById(int orderId) {
        return orderDAO.findById(orderId);
    }
}
