package com.github.arsenmonets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class StoreManagerTest {
    private StoreManager manager;
    private AuthService auth;

    @Before
    public void setUp() {
        DataStorage storage = new DataStorage("test.txt");
        manager = new StoreManager(storage);
        auth = new AuthService(manager.getUsers());
    }

    @Test
    public void testRegisterAndLogin() {
        assertTrue(auth.register("user1", "pass1", "Customer"));
        User u = auth.login("user1", "pass1");
        assertNotNull(u);
        assertEquals("user1", u.username);
        assertEquals("Customer", u.role);
    }

    @Test
    public void testRegisterDuplicate() {
        assertTrue(auth.register("user2", "pass2", "Customer"));
        assertFalse(auth.register("user2", "pass2", "Customer"));
    }

    @Test
    public void testAddProduct() {
        int size = manager.getProducts().size();
        manager.addProduct(new Product("Milk", 20.0));
        assertEquals(size + 1, manager.getProducts().size());
    }

    @Test
    public void testAddOrder() {
        User u = new User("c1", "p1", "Customer");
        Product p = new Product("Bread", 10.0);
        manager.addProduct(p);
        List<Product> cart = new ArrayList<>();
        cart.add(p);
        Order o = new Order(1, u.username, cart, "Pending");
        int size = manager.getOrders().size();
        manager.addOrder(o);
        assertEquals(size + 1, manager.getOrders().size());
    }

    @Test
    public void testUpdateOrderStatus() {
        Product p = new Product("Eggs", 15.0);
        manager.addProduct(p);
        List<Product> cart = new ArrayList<>();
        cart.add(p);
        Order o = new Order(2, "c2", cart, "Pending");
        manager.addOrder(o);
        manager.updateOrderStatus(2, "Paid");
        boolean found = false;
        for (Order ord : manager.getOrders()) {
            if (ord.id == 2 && ord.status.equals("Paid")) found = true;
        }
        assertTrue(found);
    }

    @Test
    public void testGetUsers() {
        int size = manager.getUsers().size();
        manager.getUsers().add(new User("test", "t", "Customer"));
        assertEquals(size + 1, manager.getUsers().size());
    }

    @Test
    public void testProductToString() {
        Product p = new Product("Juice", 30.5);
        assertTrue(p.toString().contains("Juice"));
    }

    @Test
    public void testOrderToString() {
        Product p = new Product("Water", 5.0);
        List<Product> cart = new ArrayList<>();
        cart.add(p);
        Order o = new Order(3, "c3", cart, "Pending");
        assertTrue(o.toString().contains("c3"));
    }

    @Test
    public void testUserToString() {
        User u = new User("alex", "123", "Seller");
        assertTrue(u.toString().contains("alex"));
    }

    @Test
    public void testLoginFail() {
        assertNull(auth.login("nouser", "nopass"));
    }
}
