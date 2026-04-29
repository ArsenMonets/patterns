package com.github.arsenmonets.dao.impl;

import com.github.arsenmonets.dao.OrderDAO;
import com.github.arsenmonets.database.DatabaseConnection;
import com.github.arsenmonets.models.Order;
import com.github.arsenmonets.models.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAOImpl implements OrderDAO {
    private final Connection connection;

    public OrderDAOImpl() {
        this.connection = DatabaseConnection.getInstance().getConnection();
        initializeTables();
    }

    private void initializeTables() {
        String ordersSql = "CREATE TABLE IF NOT EXISTS orders (" +
                "id INT PRIMARY KEY, " +
                "customer_name VARCHAR(100) NOT NULL, " +
                "status VARCHAR(50) NOT NULL)";

        String orderItemsSql = "CREATE TABLE IF NOT EXISTS order_items (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "order_id INT NOT NULL, " +
                "product_name VARCHAR(100) NOT NULL, " +
                "price DOUBLE NOT NULL, " +
                "FOREIGN KEY (order_id) REFERENCES orders(id))";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(ordersSql);
            stmt.execute(orderItemsSql);
        } catch (SQLException e) {
            System.err.println("Error initializing orders tables: " + e.getMessage());
        }
    }

    @Override
    public boolean save(Order order) {
        String orderSql = "INSERT INTO orders (id, customer_name, status) VALUES (?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(orderSql)) {
            pstmt.setInt(1, order.getId());
            pstmt.setString(2, order.getCustomerName());
            pstmt.setString(3, order.getStatus());
            pstmt.executeUpdate();

            saveOrderItems(order);
            return true;
        } catch (SQLException e) {
            System.err.println("Error saving order: " + e.getMessage());
            return false;
        }
    }

    private void saveOrderItems(Order order) {
        String sql = "INSERT INTO order_items (order_id, product_name, price) VALUES (?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            for (Product product : order.getProducts()) {
                pstmt.setInt(1, order.getId());
                pstmt.setString(2, product.getName());
                pstmt.setDouble(3, product.getPrice());
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Error saving order items: " + e.getMessage());
        }
    }

    @Override
    public Order findById(int id) {
        String sql = "SELECT * FROM orders WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                List<Product> products = getOrderProducts(id);
                return new Order(
                        rs.getInt("id"),
                        rs.getString("customer_name"),
                        products,
                        rs.getString("status"));
            }
        } catch (SQLException e) {
            System.err.println("Error finding order: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Order> findAll() {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders";

        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int orderId = rs.getInt("id");
                List<Product> products = getOrderProducts(orderId);
                orders.add(new Order(
                        orderId,
                        rs.getString("customer_name"),
                        products,
                        rs.getString("status")));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving orders: " + e.getMessage());
        }
        return orders;
    }

    @Override
    public List<Order> findByCustomer(String customerName) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE customer_name = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, customerName);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int orderId = rs.getInt("id");
                List<Product> products = getOrderProducts(orderId);
                orders.add(new Order(
                        orderId,
                        rs.getString("customer_name"),
                        products,
                        rs.getString("status")));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving orders by customer: " + e.getMessage());
        }
        return orders;
    }

    @Override
    public boolean update(Order order) {
        String sql = "UPDATE orders SET customer_name = ?, status = ? WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, order.getCustomerName());
            pstmt.setString(2, order.getStatus());
            pstmt.setInt(3, order.getId());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error updating order: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(int id) {
        String deleteItemsSql = "DELETE FROM order_items WHERE order_id = ?";
        String deleteOrderSql = "DELETE FROM orders WHERE id = ?";

        try (PreparedStatement pstmt1 = connection.prepareStatement(deleteItemsSql);
                PreparedStatement pstmt2 = connection.prepareStatement(deleteOrderSql)) {

            pstmt1.setInt(1, id);
            pstmt1.executeUpdate();

            pstmt2.setInt(1, id);
            pstmt2.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error deleting order: " + e.getMessage());
            return false;
        }
    }

    private List<Product> getOrderProducts(int orderId) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT product_name, price FROM order_items WHERE order_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, orderId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                products.add(new Product(
                        rs.getString("product_name"),
                        rs.getDouble("price")));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving order items: " + e.getMessage());
        }
        return products;
    }
}
