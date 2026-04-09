package com.github.arsenmonets;

import java.util.List;

public class Order {
    public int id;
    public String customerName;
    public String status;
    public List<Product> products;

    public Order(int id, String customerName, List<Product> products, String status) {
        this.id = id;
        this.customerName = customerName;
        this.products = products;
        this.status = status;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", customer='" + customerName + '\'' +
                ", status='" + status + '\'' +
                ", products=" + products +
                '}';
    }
}