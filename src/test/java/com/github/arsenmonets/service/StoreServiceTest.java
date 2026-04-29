package com.github.arsenmonets.service;

import com.github.arsenmonets.repository.StoreRepository;
import com.github.arsenmonets.models.Product;
import com.github.arsenmonets.models.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@RunWith(JUnit4.class)
public class StoreServiceTest {

    @Mock
    private StoreRepository storeRepository;

    private StoreService storeService;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        storeService = new StoreService(storeRepository);
    }

    @Test(expected = IllegalAccessError.class)
    public void testAddToCartWithNullUser() {
        storeService.addToCart(null, "Laptop");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddToCartWithNonExistentProduct() {
        User user = new User("testuser", "hashedpass", "Customer");
        when(storeRepository.getProducts()).thenReturn(new ArrayList<>());
        storeService.addToCart(user, "NonExistentProduct");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddToCartWithNullProductName() {
        User user = new User("testuser", "hashedpass", "Customer");
        storeService.addToCart(user, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddToCartWithEmptyProductName() {
        User user = new User("testuser", "hashedpass", "Customer");
        storeService.addToCart(user, "");
    }

    @Test(expected = IllegalAccessError.class)
    public void testGetCartWithNullUser() {
        storeService.getCart(null);
    }

    @Test
    public void testGetCartEmptyForNewUser() {
        User user = new User("newuser", "hashedpass", "Customer");
        List<Product> cart = storeService.getCart(user);
        assertNotNull(cart);
        assertTrue(cart.isEmpty());
    }

    @Test(expected = IllegalAccessError.class)
    public void testClearCartWithNullUser() {
        storeService.clearCart(null);
    }

    @Test
    public void testCalculateCartTotalWithEmptyCart() {
        List<Product> cart = new ArrayList<>();
        double total = storeService.calculateCartTotal(cart);
        assertEquals(0.0, total, 0.01);
    }

    @Test
    public void testCalculateCartTotalWithNullCart() {
        double total = storeService.calculateCartTotal(null);
        assertEquals(0.0, total, 0.01);
    }

    @Test(expected = IllegalAccessError.class)
    public void testCustomerCheckoutWithNullUser() {
        List<Product> cart = new ArrayList<>();
        storeService.customerCheckout(null, cart);
    }

    @Test(expected = IllegalAccessError.class)
    public void testCustomerCheckoutWithSellerUser() {
        User seller = new User("seller", "hashedpass", "Seller");
        List<Product> cart = new ArrayList<>();
        cart.add(new Product("Laptop", 999.99));
        storeService.customerCheckout(seller, cart);
    }

    @Test(expected = IllegalAccessError.class)
    public void testCustomerCheckoutWithInvalidRole() {
        User user = new User("testuser", "hashedpass", "InvalidRole");
        List<Product> cart = new ArrayList<>();
        cart.add(new Product("Laptop", 999.99));
        storeService.customerCheckout(user, cart);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCustomerCheckoutWithNullCart() {
        User customer = new User("customer", "hashedpass", "Customer");
        storeService.customerCheckout(customer, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCustomerCheckoutWithEmptyCart() {
        User customer = new User("customer", "hashedpass", "Customer");
        List<Product> cart = new ArrayList<>();
        storeService.customerCheckout(customer, cart);
    }

    @Test(expected = IllegalAccessError.class)
    public void testCustomerViewMyOrdersWithNullUser() {
        storeService.customerViewMyOrders(null);
    }

    @Test(expected = IllegalAccessError.class)
    public void testCustomerViewMyOrdersWithSellerUser() {
        User seller = new User("seller", "hashedpass", "Seller");
        storeService.customerViewMyOrders(seller);
    }

    @Test(expected = IllegalAccessError.class)
    public void testCustomerViewMyOrdersWithInvalidRole() {
        User user = new User("testuser", "hashedpass", "InvalidRole");
        storeService.customerViewMyOrders(user);
    }

    @Test(expected = IllegalAccessError.class)
    public void testSellerViewAllOrdersWithNullUser() {
        storeService.sellerViewAllOrders(null);
    }

    @Test(expected = IllegalAccessError.class)
    public void testSellerViewAllOrdersWithCustomerUser() {
        User customer = new User("customer", "hashedpass", "Customer");
        storeService.sellerViewAllOrders(customer);
    }

    @Test(expected = IllegalAccessError.class)
    public void testSellerViewAllOrdersWithInvalidRole() {
        User user = new User("testuser", "hashedpass", "InvalidRole");
        storeService.sellerViewAllOrders(user);
    }

    @Test(expected = IllegalAccessError.class)
    public void testSellerConfirmOrderWithNullUser() {
        storeService.sellerConfirmOrder(null, 1);
    }

    @Test(expected = IllegalAccessError.class)
    public void testSellerConfirmOrderWithCustomerUser() {
        User customer = new User("customer", "hashedpass", "Customer");
        storeService.sellerConfirmOrder(customer, 1);
    }

    @Test(expected = IllegalAccessError.class)
    public void testSellerConfirmOrderWithInvalidRole() {
        User user = new User("testuser", "hashedpass", "InvalidRole");
        storeService.sellerConfirmOrder(user, 1);
    }

    @Test(expected = IllegalAccessError.class)
    public void testSellerAddProductWithNullUser() {
        storeService.sellerAddProduct(null, "Laptop", 999.99);
    }

    @Test(expected = IllegalAccessError.class)
    public void testSellerAddProductWithCustomerUser() {
        User customer = new User("customer", "hashedpass", "Customer");
        storeService.sellerAddProduct(customer, "Laptop", 999.99);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSellerAddProductWithNullName() {
        User seller = new User("seller", "hashedpass", "Seller");
        storeService.sellerAddProduct(seller, null, 999.99);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSellerAddProductWithEmptyName() {
        User seller = new User("seller", "hashedpass", "Seller");
        storeService.sellerAddProduct(seller, "", 999.99);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSellerAddProductWithWhitespaceName() {
        User seller = new User("seller", "hashedpass", "Seller");
        storeService.sellerAddProduct(seller, "   ", 999.99);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSellerAddProductWithZeroPrice() {
        User seller = new User("seller", "hashedpass", "Seller");
        storeService.sellerAddProduct(seller, "Laptop", 0.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSellerAddProductWithNegativePrice() {
        User seller = new User("seller", "hashedpass", "Seller");
        storeService.sellerAddProduct(seller, "Laptop", -99.99);
    }

    @Test(expected = IllegalAccessError.class)
    public void testSellerUpdateProductWithNullUser() {
        Product product = new Product("Laptop", 999.99);
        storeService.sellerUpdateProduct(null, product);
    }

    @Test(expected = IllegalAccessError.class)
    public void testSellerUpdateProductWithCustomerUser() {
        User customer = new User("customer", "hashedpass", "Customer");
        Product product = new Product("Laptop", 999.99);
        storeService.sellerUpdateProduct(customer, product);
    }

    @Test(expected = IllegalAccessError.class)
    public void testSellerDeleteProductWithNullUser() {
        storeService.sellerDeleteProduct(null, "Laptop");
    }

    @Test(expected = IllegalAccessError.class)
    public void testSellerDeleteProductWithCustomerUser() {
        User customer = new User("customer", "hashedpass", "Customer");
        storeService.sellerDeleteProduct(customer, "Laptop");
    }

    @Test(expected = IllegalAccessError.class)
    public void testSellerManageInventoryWithNullUser() {
        List<Product> products = new ArrayList<>();
        storeService.sellerManageInventory(null, products);
    }

    @Test(expected = IllegalAccessError.class)
    public void testSellerManageInventoryWithCustomerUser() {
        User customer = new User("customer", "hashedpass", "Customer");
        List<Product> products = new ArrayList<>();
        storeService.sellerManageInventory(customer, products);
    }

    @Test
    public void testGetPostLoginRedirectPathForCustomer() {
        User customer = new User("customer", "hashedpass", "Customer");
        String path = storeService.getPostLoginRedirectPath(customer);
        assertEquals("/shop", path);
    }

    @Test
    public void testGetPostLoginRedirectPathForSeller() {
        User seller = new User("seller", "hashedpass", "Seller");
        String path = storeService.getPostLoginRedirectPath(seller);
        assertEquals("/admin/orders", path);
    }

    @Test
    public void testGetPostLoginRedirectPathForNullUser() {
        String path = storeService.getPostLoginRedirectPath(null);
        assertEquals("/shop", path);
    }
}
