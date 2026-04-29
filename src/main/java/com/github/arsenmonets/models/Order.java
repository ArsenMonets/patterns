package com.github.arsenmonets.models;

import java.util.List;

public class Order {
    private int id;
    private String customerName;
    private String status;
    private List<Product> products;

    public Order(int id, String customerName, List<Product> products, String status) {
        this.id = id;
        this.customerName = customerName;
        this.products = products;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", customerName='" + customerName + '\'' +
                ", status='" + status + '\'' +
                ", products=" + products +
                '}';
    }
}
