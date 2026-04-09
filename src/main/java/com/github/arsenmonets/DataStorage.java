package com.github.arsenmonets;

import java.io.*;
import java.util.*;

public class DataStorage {
    private String filePath;

    public DataStorage(String filePath) {
        this.filePath = filePath;
    }

    public void saveAll(List<User> users, List<Product> products, List<Order> orders) {
        try (PrintWriter out = new PrintWriter(new FileWriter(filePath))) {
            out.println("[USERS]");
            for (User u : users) out.println(u.username + "," + u.password + "," + u.role);
            out.println("[PRODUCTS]");
            for (Product p : products) out.println(p.name + "," + p.price);
            out.println("[ORDERS]");
            for (Order o : orders) {
                out.print(o.id + "," + o.customerName + "," + o.status + ",");
                for (Product p : o.products) out.print(p.name + "|");
                out.println();
            }
        } catch (IOException e) {
            System.out.println("Error saving data: " + e.getMessage());
        }
    }

    public Map<String, List<?>> loadAll() {
        // Dummy loader for brevity
        Map<String, List<?>> map = new HashMap<>();
        map.put("users", new ArrayList<User>());
        map.put("products", new ArrayList<Product>());
        map.put("orders", new ArrayList<Order>());
        return map;
    }
}