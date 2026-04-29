package com.github.arsenmonets.service;

import com.github.arsenmonets.dao.impl.UserDAOImpl;
import com.github.arsenmonets.dao.impl.ProductDAOImpl;
import com.github.arsenmonets.dao.impl.OrderDAOImpl;
import com.github.arsenmonets.repository.StoreRepository;
import com.github.arsenmonets.models.User;
import com.github.arsenmonets.models.Product;
import com.github.arsenmonets.models.Order;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class StoreServiceIntegrationTest {

    private StoreService storeService;
    private StoreRepository storeRepository;
    private ProductDAOImpl productDAO;
    private OrderDAOImpl orderDAO;
    private UserDAOImpl userDAO;

    @Before
    public void setUp() {
        productDAO = new ProductDAOImpl();
        orderDAO = new OrderDAOImpl();
        userDAO = new UserDAOImpl();
        storeRepository = new StoreRepository(userDAO, productDAO, orderDAO);
        storeService = new StoreService(storeRepository);

        productDAO.save(new Product("Laptop", 999.99));
        productDAO.save(new Product("Mouse", 29.99));
        productDAO.save(new Product("Keyboard", 79.99));
        productDAO.save(new Product("Monitor", 299.99));
    }

    @Test
    public void testBrowseProductsReturnsAllProducts() {
        List<Product> products = storeService.browseProducts();
        assertNotNull(products);
        assertTrue(products.size() >= 4);
    }

    @Test
    public void testBrowseProductsContainsExpectedProducts() {
        List<Product> products = storeService.browseProducts();

        boolean hasLaptop = products.stream()
                .anyMatch(p -> p.getName().equals("Laptop") && p.getPrice() == 999.99);
        boolean hasMouse = products.stream()
                .anyMatch(p -> p.getName().equals("Mouse") && p.getPrice() == 29.99);

        assertTrue(hasLaptop);
        assertTrue(hasMouse);
    }

    @Test
    public void testFindProductByName() {
        Product product = storeService.findProductByName("Laptop");

        assertNotNull(product);
        assertEquals("Laptop", product.getName());
        assertEquals(999.99, product.getPrice(), 0.01);
    }

    @Test
    public void testFindProductByNameCaseInsensitive() {
        Product product = storeService.findProductByName("laptop");

        assertNotNull(product);
        assertEquals("Laptop", product.getName());
    }

    @Test
    public void testAddSingleProductToCart() {
        User customer = new User("customer1", "hashedpass", "Customer");

        storeService.addToCart(customer, "Laptop");
        List<Product> cart = storeService.getCart(customer);

        assertEquals(1, cart.size());
        assertEquals("Laptop", cart.get(0).getName());
    }

    @Test
    public void testAddMultipleProductsToCart() {
        User customer = new User("customer1", "hashedpass", "Customer");

        storeService.addToCart(customer, "Laptop");
        storeService.addToCart(customer, "Mouse");
        storeService.addToCart(customer, "Keyboard");

        List<Product> cart = storeService.getCart(customer);
        assertEquals(3, cart.size());
    }

    @Test
    public void testAddDuplicateProductToCart() {
        User customer = new User("customer1", "hashedpass", "Customer");

        storeService.addToCart(customer, "Laptop");
        storeService.addToCart(customer, "Laptop");

        List<Product> cart = storeService.getCart(customer);
        assertEquals(2, cart.size());
        assertEquals("Laptop", cart.get(0).getName());
        assertEquals("Laptop", cart.get(1).getName());
    }

    @Test
    public void testCalculateSingleProductCartTotal() {
        List<Product> cart = new ArrayList<>();
        cart.add(new Product("Laptop", 999.99));

        double total = storeService.calculateCartTotal(cart);
        assertEquals(999.99, total, 0.01);
    }

    @Test
    public void testCalculateMultipleProductsCartTotal() {
        List<Product> cart = new ArrayList<>();
        cart.add(new Product("Laptop", 999.99));
        cart.add(new Product("Mouse", 29.99));
        cart.add(new Product("Keyboard", 79.99));

        double total = storeService.calculateCartTotal(cart);
        assertEquals(1109.97, total, 0.01);
    }

    @Test
    public void testCalculateDuplicateProductsCartTotal() {
        List<Product> cart = new ArrayList<>();
        cart.add(new Product("Laptop", 999.99));
        cart.add(new Product("Laptop", 999.99));

        double total = storeService.calculateCartTotal(cart);
        assertEquals(1999.98, total, 0.01);
    }

    @Test
    public void testClearCartRemovesAllProducts() {
        User customer = new User("customer1", "hashedpass", "Customer");

        storeService.addToCart(customer, "Laptop");
        storeService.addToCart(customer, "Mouse");

        storeService.clearCart(customer);
        List<Product> cart = storeService.getCart(customer);

        assertTrue(cart.isEmpty());
    }

    @Test
    public void testCustomerCheckoutWithSingleProduct() {
        User customer = new User("john_doe", "hashedpass", "Customer");

        storeService.addToCart(customer, "Laptop");
        List<Product> cart = storeService.getCart(customer);

        storeService.customerCheckout(customer, cart);

        List<Product> emptyCart = storeService.getCart(customer);
        assertTrue(emptyCart.isEmpty());
    }

    @Test
    public void testCustomerCheckoutWithMultipleProducts() {
        User customer = new User("jane_doe", "hashedpass", "Customer");

        storeService.addToCart(customer, "Laptop");
        storeService.addToCart(customer, "Mouse");
        storeService.addToCart(customer, "Monitor");

        List<Product> cart = storeService.getCart(customer);
        assertEquals(3, cart.size());

        storeService.customerCheckout(customer, cart);

        List<Order> orders = storeService.customerViewMyOrders(customer);
        assertEquals(1, orders.size());
        assertEquals("Pending", orders.get(0).getStatus());
    }

    @Test
    public void testCustomerCheckoutCreatesOrder() {
        User customer = new User("customer1", "hashedpass", "Customer");

        storeService.addToCart(customer, "Laptop");
        List<Product> cart = storeService.getCart(customer);

        storeService.customerCheckout(customer, cart);

        List<Order> orders = storeService.customerViewMyOrders(customer);
        assertNotNull(orders);
        assertEquals(1, orders.size());

        Order order = orders.get(0);
        assertEquals("customer1", order.getCustomerName());
        assertEquals("Pending", order.getStatus());
        assertEquals(1, order.getProducts().size());
    }

    @Test
    public void testCustomerViewMyOrdersReturnsEmptyListInitially() {
        User customer = new User("new_customer", "hashedpass", "Customer");
        List<Order> orders = storeService.customerViewMyOrders(customer);

        assertNotNull(orders);
        assertTrue(orders.isEmpty());
    }

    @Test
    public void testCustomerViewMyOrdersReturnsOnlyOwnOrders() {
        User customer1 = new User("customer1", "hashedpass", "Customer");
        User customer2 = new User("customer2", "hashedpass", "Customer");

        storeService.addToCart(customer1, "Laptop");
        storeService.customerCheckout(customer1, storeService.getCart(customer1));

        storeService.addToCart(customer2, "Mouse");
        storeService.customerCheckout(customer2, storeService.getCart(customer2));

        List<Order> customer1Orders = storeService.customerViewMyOrders(customer1);
        assertEquals(1, customer1Orders.size());
        assertEquals("customer1", customer1Orders.get(0).getCustomerName());

        List<Order> customer2Orders = storeService.customerViewMyOrders(customer2);
        assertEquals(1, customer2Orders.size());
        assertEquals("customer2", customer2Orders.get(0).getCustomerName());
    }

    @Test
    public void testSellerViewAllOrdersReturnsAllOrders() {
        User seller = new User("seller1", "hashedpass", "Seller");
        User customer1 = new User("customer1", "hashedpass", "Customer");
        User customer2 = new User("customer2", "hashedpass", "Customer");

        storeService.addToCart(customer1, "Laptop");
        storeService.customerCheckout(customer1, storeService.getCart(customer1));

        storeService.addToCart(customer2, "Monitor");
        storeService.customerCheckout(customer2, storeService.getCart(customer2));

        List<Order> allOrders = storeService.sellerViewAllOrders(seller);
        assertEquals(2, allOrders.size());
    }

    @Test
    public void testSellerConfirmOrderUpdatesStatus() {
        User seller = new User("seller1", "hashedpass", "Seller");
        User customer = new User("customer1", "hashedpass", "Customer");

        storeService.addToCart(customer, "Laptop");
        storeService.customerCheckout(customer, storeService.getCart(customer));

        List<Order> orders = storeService.customerViewMyOrders(customer);
        int orderId = orders.get(0).getId();

        storeService.sellerConfirmOrder(seller, orderId);

        List<Order> updatedOrders = storeService.customerViewMyOrders(customer);
        assertEquals("Confirmed", updatedOrders.get(0).getStatus());
    }

    @Test
    public void testSellerAddNewProduct() {
        User seller = new User("seller1", "hashedpass", "Seller");

        storeService.sellerAddProduct(seller, "Headphones", 149.99);

        List<Product> products = storeService.browseProducts();
        boolean found = products.stream()
                .anyMatch(p -> p.getName().equals("Headphones") && p.getPrice() == 149.99);

        assertTrue(found);
    }

    @Test
    public void testSellerAddMultipleProducts() {
        User seller = new User("seller1", "hashedpass", "Seller");

        storeService.sellerAddProduct(seller, "USB Cable", 9.99);
        storeService.sellerAddProduct(seller, "Webcam", 89.99);
        storeService.sellerAddProduct(seller, "SSD", 149.99);

        List<Product> products = storeService.browseProducts();
        assertTrue(products.size() >= 7);
    }

    @Test
    public void testSellerAddProductWithDecimalPrice() {
        User seller = new User("seller1", "hashedpass", "Seller");

        storeService.sellerAddProduct(seller, "Charger", 19.99);

        Product product = storeService.findProductByName("Charger");
        assertEquals(19.99, product.getPrice(), 0.01);
    }

    @Test
    public void testSellerUpdateProductPrice() {
        User seller = new User("seller1", "hashedpass", "Seller");

        Product laptop = storeService.findProductByName("Laptop");
        laptop.setPrice(899.99);

        storeService.sellerUpdateProduct(seller, laptop);

        Product updated = storeService.findProductByName("Laptop");
        assertEquals(899.99, updated.getPrice(), 0.01);
    }

    @Test
    public void testSellerDeleteProduct() {
        User seller = new User("seller1", "hashedpass", "Seller");

        storeService.sellerDeleteProduct(seller, "Mouse");

        Product product = storeService.findProductByName("Mouse");
        assertNull(product);
    }

    @Test
    public void testSellerDeleteProductReducesInventory() {
        User seller = new User("seller1", "hashedpass", "Seller");
        List<Product> beforeDelete = storeService.browseProducts();

        storeService.sellerDeleteProduct(seller, "Keyboard");

        List<Product> afterDelete = storeService.browseProducts();
        assertEquals(beforeDelete.size() - 1, afterDelete.size());
    }

    @Test
    public void testSellerManageInventoryUpdatesMultipleProducts() {
        User seller = new User("seller1", "hashedpass", "Seller");

        List<Product> productsToUpdate = new ArrayList<>();
        Product laptop = storeService.findProductByName("Laptop");
        laptop.setPrice(1099.99);
        productsToUpdate.add(laptop);

        Product monitor = storeService.findProductByName("Monitor");
        monitor.setPrice(399.99);
        productsToUpdate.add(monitor);

        storeService.sellerManageInventory(seller, productsToUpdate);

        Product updatedLaptop = storeService.findProductByName("Laptop");
        Product updatedMonitor = storeService.findProductByName("Monitor");

        assertEquals(1099.99, updatedLaptop.getPrice(), 0.01);
        assertEquals(399.99, updatedMonitor.getPrice(), 0.01);
    }

    @Test
    public void testPostLoginRedirectPathForCustomer() {
        User customer = new User("customer", "hashedpass", "Customer");
        String path = storeService.getPostLoginRedirectPath(customer);

        assertEquals("/shop", path);
    }

    @Test
    public void testPostLoginRedirectPathForSeller() {
        User seller = new User("seller", "hashedpass", "Seller");
        String path = storeService.getPostLoginRedirectPath(seller);

        assertEquals("/admin/orders", path);
    }

}
