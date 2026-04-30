package com.github.arsenmonets.dao.impl;

import com.github.arsenmonets.dao.ProductDAO;
import com.github.arsenmonets.database.DatabaseConnection;
import com.github.arsenmonets.models.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAOImpl implements ProductDAO {
    private final Connection connection;

    public ProductDAOImpl() {
        this.connection = DatabaseConnection.getInstance().getConnection();
        initializeTable();
    }

    private void initializeTable() {
        String sql = "CREATE TABLE IF NOT EXISTS products (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "name VARCHAR(100) UNIQUE NOT NULL, " +
                "price DOUBLE NOT NULL)";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("Error initializing products table: " + e.getMessage());
        }
    }

    @Override
    public boolean save(Product product) {
        String sql = "INSERT INTO products (name, price) VALUES (?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, product.getName());
            pstmt.setDouble(2, product.getPrice());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error saving product: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Product findByName(String name) {
        String sql = "SELECT * FROM products WHERE name = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Product(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDouble("price"));
            }
        } catch (SQLException e) {
            System.err.println("Error finding product: " + e.getMessage());
        }
        return null;
    }

    public Product findById(int id) {
        String sql = "SELECT * FROM products WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Product(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDouble("price"));
            }
        } catch (SQLException e) {
            System.err.println("Error finding product by id: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Product> findAll() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products";

        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                products.add(new Product(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDouble("price")));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving products: " + e.getMessage());
        }
        return products;
    }

    @Override
    public boolean update(Product product) {
        String sql = "UPDATE products SET price = ? WHERE name = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setDouble(1, product.getPrice());
            pstmt.setString(2, product.getName());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error updating product: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(String name) {
        String sql = "DELETE FROM products WHERE name = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error deleting product: " + e.getMessage());
            return false;
        }
    }
}
