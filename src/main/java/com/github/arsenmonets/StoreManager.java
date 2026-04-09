package com.github.arsenmonets;

import java.util.*;

public class StoreManager {
    private List<User> users;
    private List<Product> catalog;
    private List<Order> orders;
    private DataStorage storage;
    private int orderCounter = 1;

    @SuppressWarnings("unchecked")
    public StoreManager(DataStorage storage) {
        this.storage = storage;
        Map<String, List<?>> data = storage.loadAll();
        this.users = (List<User>) data.get("users");
        this.catalog = (List<Product>) data.get("products");
        this.orders = (List<Order>) data.get("orders");
        if (users.isEmpty())
            users.add(new User("admin", "admin", "Seller"));
    }

    public List<Product> getProducts() {
        return catalog;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void addOrder(Order o) {
        orders.add(o);
        this.orderCounter++;
        storage.saveAll(users, catalog, orders);
    }

    public void updateOrderStatus(int id, String status) {
        for (Order o : orders)
            if (o.id == id)
                o.status = status;
        storage.saveAll(users, catalog, orders);
    }

    public List<User> getUsers() {
        return users;
    }

    public void addProduct(Product p) {
        catalog.add(p);
        storage.saveAll(users, catalog, orders);
    }
}