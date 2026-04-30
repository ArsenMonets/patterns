package com.github.arsenmonets.service;

import com.github.arsenmonets.repository.StoreRepository;
import com.github.arsenmonets.models.Product;
import com.github.arsenmonets.models.Order;
import com.github.arsenmonets.models.User;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

public class StoreService {
    private final StoreRepository storeRepository;
    private final Map<String, List<Product>> userCarts;

    public StoreService(StoreRepository storeRepository) {
        this.storeRepository = storeRepository;
        this.userCarts = new HashMap<>();
    }

    public List<Product> browseProducts() {
        return storeRepository.getProducts();
    }

    public Product findProductByName(String name) {
        List<Product> products = storeRepository.getProducts();
        return products.stream()
                .filter(p -> p.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public Product findProductById(int id) {
        List<Product> products = storeRepository.getProducts();
        return products.stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public void addToCart(User user, String productName) {
        if (user == null) {
            throw new IllegalAccessError("User must be logged in");
        }
        Product product = findProductByName(productName);
        if (product == null) {
            throw new IllegalArgumentException("Product not found: " + productName);
        }
        userCarts.computeIfAbsent(user.getUsername(), k -> new ArrayList<>()).add(product);
    }

    public void addToCartByProductId(User user, int productId) {
        if (user == null) {
            throw new IllegalAccessError("User must be logged in");
        }
        Product product = findProductById(productId);
        if (product == null) {
            throw new IllegalArgumentException("Product not found with id: " + productId);
        }
        userCarts.computeIfAbsent(user.getUsername(), k -> new ArrayList<>()).add(product);
    }

    public List<Product> getCart(User user) {
        if (user == null) {
            throw new IllegalAccessError("User must be logged in");
        }
        return userCarts.getOrDefault(user.getUsername(), new ArrayList<>());
    }

    public double calculateCartTotal(List<Product> cart) {
        if (cart == null) {
            return 0;
        }
        return cart.stream().mapToDouble(Product::getPrice).sum();
    }

    public void clearCart(User user) {
        if (user == null) {
            throw new IllegalAccessError("User must be logged in");
        }
        userCarts.put(user.getUsername(), new ArrayList<>());
    }

    public void customerCheckout(User user, List<Product> cart) {
        if (user == null || !"Customer".equals(user.getRole())) {
            throw new IllegalAccessError("Only customers can checkout");
        }
        if (cart == null || cart.isEmpty()) {
            throw new IllegalArgumentException("Cart cannot be empty");
        }
        Order order = new Order(0, user.getUsername(), new ArrayList<>(cart), "Pending");
        storeRepository.addOrder(order);
        clearCart(user);
    }

    public List<Order> customerViewMyOrders(User user) {
        if (user != null && "Customer".equals(user.getRole())) {
            return storeRepository.getOrdersByCustomer(user.getUsername());
        } else {
            throw new IllegalAccessError("Only customers can view their orders");
        }
    }

    public List<Order> sellerViewAllOrders(User user) {
        if (user != null && "Seller".equals(user.getRole())) {
            return storeRepository.getOrders();
        } else {
            throw new IllegalAccessError("Only sellers can view all orders");
        }
    }

    public void sellerConfirmOrder(User user, int orderId) {
        if (user != null && "Seller".equals(user.getRole())) {
            storeRepository.updateOrderStatus(orderId, "Confirmed");
        } else {
            throw new IllegalAccessError("Only sellers can confirm orders");
        }
    }

    public void sellerAddProduct(User user, String name, double price) {
        if (user != null && "Seller".equals(user.getRole())) {
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("Product name cannot be empty");
            }
            if (price <= 0) {
                throw new IllegalArgumentException("Product price must be positive");
            }
            Product product = new Product(name, price);
            storeRepository.addProduct(product);
        } else {
            throw new IllegalAccessError("Only sellers can add products");
        }
    }

    public void sellerUpdateProduct(User user, Product product) {
        if (user != null && "Seller".equals(user.getRole())) {
            storeRepository.updateProduct(product);
        } else {
            throw new IllegalAccessError("Only sellers can update products");
        }
    }

    public void sellerDeleteProduct(User user, String productName) {
        if (user != null && "Seller".equals(user.getRole())) {
            storeRepository.deleteProduct(productName);
        } else {
            throw new IllegalAccessError("Only sellers can delete products");
        }
    }

    public void sellerManageInventory(User user, List<Product> products) {
        if (user != null && "Seller".equals(user.getRole())) {
            for (Product product : products) {
                storeRepository.updateProduct(product);
            }
        } else {
            throw new IllegalAccessError("Only sellers can manage inventory");
        }
    }

    public String getPostLoginRedirectPath(User user) {
        if (user != null && "Seller".equals(user.getRole())) {
            return "/admin/orders";
        }
        return "/shop";
    }
}
