package com.github.arsenmonets.service;

import com.github.arsenmonets.repository.StoreRepository;
import com.github.arsenmonets.models.Product;
import com.github.arsenmonets.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class StoreServiceTest {

    @Mock
    private StoreRepository storeRepository;

    private StoreService storeService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        storeService = new StoreService(storeRepository);
    }

    @Test
    public void testAddToCartWithNullUser() {
        assertThrows(IllegalAccessError.class, () -> storeService.addToCart(null, "Laptop"));
    }

    @Test
    public void testAddToCartWithNonExistentProduct() {
        User user = new User("testuser", "hashedpass", "Customer");
        when(storeRepository.getProducts()).thenReturn(new ArrayList<>());
        assertThrows(IllegalArgumentException.class, () -> storeService.addToCart(user, "NonExistentProduct"));
    }

    @Test
    public void testAddToCartWithNullProductName() {
        User user = new User("testuser", "hashedpass", "Customer");
        assertThrows(IllegalArgumentException.class, () -> storeService.addToCart(user, null));
    }

    @Test
    public void testAddToCartWithEmptyProductName() {
        User user = new User("testuser", "hashedpass", "Customer");
        assertThrows(IllegalArgumentException.class, () -> storeService.addToCart(user, ""));
    }

    @Test
    public void testGetCartWithNullUser() {
        assertThrows(IllegalAccessError.class, () -> storeService.getCart(null));
    }

    @Test
    public void testGetCartEmptyForNewUser() {
        User user = new User("newuser", "hashedpass", "Customer");
        List<Product> cart = storeService.getCart(user);
        assertNotNull(cart);
        assertTrue(cart.isEmpty());
    }

    @Test
    public void testClearCartWithNullUser() {
        assertThrows(IllegalAccessError.class, () -> storeService.clearCart(null));
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

    @Test
    public void testCustomerCheckoutWithNullUser() {
        List<Product> cart = new ArrayList<>();
        assertThrows(IllegalAccessError.class, () -> storeService.customerCheckout(null, cart));
    }

    @Test
    public void testCustomerCheckoutWithSellerUser() {
        User seller = new User("seller", "hashedpass", "Seller");
        List<Product> cart = new ArrayList<>();
        cart.add(new Product("Laptop", 999.99));
        assertThrows(IllegalAccessError.class, () -> storeService.customerCheckout(seller, cart));
    }

    @Test
    public void testCustomerCheckoutWithInvalidRole() {
        User user = new User("testuser", "hashedpass", "InvalidRole");
        List<Product> cart = new ArrayList<>();
        cart.add(new Product("Laptop", 999.99));
        assertThrows(IllegalAccessError.class, () -> storeService.customerCheckout(user, cart));
    }

    @Test
    public void testCustomerCheckoutWithNullCart() {
        User customer = new User("customer", "hashedpass", "Customer");
        assertThrows(IllegalArgumentException.class, () -> storeService.customerCheckout(customer, null));
    }

    @Test
    public void testCustomerCheckoutWithEmptyCart() {
        User customer = new User("customer", "hashedpass", "Customer");
        List<Product> cart = new ArrayList<>();
        assertThrows(IllegalArgumentException.class, () -> storeService.customerCheckout(customer, cart));
    }

    @Test
    public void testCustomerViewMyOrdersWithNullUser() {
        assertThrows(IllegalAccessError.class, () -> storeService.customerViewMyOrders(null));
    }

    @Test
    public void testCustomerViewMyOrdersWithSellerUser() {
        User seller = new User("seller", "hashedpass", "Seller");
        assertThrows(IllegalAccessError.class, () -> storeService.customerViewMyOrders(seller));
    }

    @Test
    public void testCustomerViewMyOrdersWithInvalidRole() {
        User user = new User("testuser", "hashedpass", "InvalidRole");
        assertThrows(IllegalAccessError.class, () -> storeService.customerViewMyOrders(user));
    }

    @Test
    public void testSellerViewAllOrdersWithNullUser() {
        assertThrows(IllegalAccessError.class, () -> storeService.sellerViewAllOrders(null));
    }

    @Test
    public void testSellerViewAllOrdersWithCustomerUser() {
        User customer = new User("customer", "hashedpass", "Customer");
        assertThrows(IllegalAccessError.class, () -> storeService.sellerViewAllOrders(customer));
    }

    @Test
    public void testSellerViewAllOrdersWithInvalidRole() {
        User user = new User("testuser", "hashedpass", "InvalidRole");
        assertThrows(IllegalAccessError.class, () -> storeService.sellerViewAllOrders(user));
    }

    @Test
    public void testSellerConfirmOrderWithNullUser() {
        assertThrows(IllegalAccessError.class, () -> storeService.sellerConfirmOrder(null, 1));
    }

    @Test
    public void testSellerConfirmOrderWithCustomerUser() {
        User customer = new User("customer", "hashedpass", "Customer");
        assertThrows(IllegalAccessError.class, () -> storeService.sellerConfirmOrder(customer, 1));
    }

    @Test
    public void testSellerConfirmOrderWithInvalidRole() {
        User user = new User("testuser", "hashedpass", "InvalidRole");
        assertThrows(IllegalAccessError.class, () -> storeService.sellerConfirmOrder(user, 1));
    }

    @Test
    public void testSellerAddProductWithNullUser() {
        assertThrows(IllegalAccessError.class, () -> storeService.sellerAddProduct(null, "Laptop", 999.99));
    }

    @Test
    public void testSellerAddProductWithCustomerUser() {
        User customer = new User("customer", "hashedpass", "Customer");
        assertThrows(IllegalAccessError.class, () -> storeService.sellerAddProduct(customer, "Laptop", 999.99));
    }

    @Test
    public void testSellerAddProductWithNullName() {
        User seller = new User("seller", "hashedpass", "Seller");
        assertThrows(IllegalArgumentException.class, () -> storeService.sellerAddProduct(seller, null, 999.99));
    }

    @Test
    public void testSellerAddProductWithEmptyName() {
        User seller = new User("seller", "hashedpass", "Seller");
        assertThrows(IllegalArgumentException.class, () -> storeService.sellerAddProduct(seller, "", 999.99));
    }

    @Test
    public void testSellerAddProductWithWhitespaceName() {
        User seller = new User("seller", "hashedpass", "Seller");
        assertThrows(IllegalArgumentException.class, () -> storeService.sellerAddProduct(seller, "   ", 999.99));
    }

    @Test
    public void testSellerAddProductWithZeroPrice() {
        User seller = new User("seller", "hashedpass", "Seller");
        assertThrows(IllegalArgumentException.class, () -> storeService.sellerAddProduct(seller, "Laptop", 0.0));
    }

    @Test
    public void testSellerAddProductWithNegativePrice() {
        User seller = new User("seller", "hashedpass", "Seller");
        assertThrows(IllegalArgumentException.class, () -> storeService.sellerAddProduct(seller, "Laptop", -99.99));
    }

    @Test
    public void testSellerUpdateProductWithNullUser() {
        Product product = new Product("Laptop", 999.99);
        assertThrows(IllegalAccessError.class, () -> storeService.sellerUpdateProduct(null, product));
    }

    @Test
    public void testSellerUpdateProductWithCustomerUser() {
        User customer = new User("customer", "hashedpass", "Customer");
        Product product = new Product("Laptop", 999.99);
        assertThrows(IllegalAccessError.class, () -> storeService.sellerUpdateProduct(customer, product));
    }

    @Test
    public void testSellerDeleteProductWithNullUser() {
        assertThrows(IllegalAccessError.class, () -> storeService.sellerDeleteProduct(null, 1));
    }

    @Test
    public void testSellerDeleteProductWithCustomerUser() {
        User customer = new User("customer", "hashedpass", "Customer");
        assertThrows(IllegalAccessError.class, () -> storeService.sellerDeleteProduct(customer, 1));
    }

    @Test
    public void testSellerManageInventoryWithNullUser() {
        List<Product> products = new ArrayList<>();
        assertThrows(IllegalAccessError.class, () -> storeService.sellerManageInventory(null, products));
    }

    @Test
    public void testSellerManageInventoryWithCustomerUser() {
        User customer = new User("customer", "hashedpass", "Customer");
        List<Product> products = new ArrayList<>();
        assertThrows(IllegalAccessError.class, () -> storeService.sellerManageInventory(customer, products));
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
