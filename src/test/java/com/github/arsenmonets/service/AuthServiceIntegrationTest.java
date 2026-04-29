package com.github.arsenmonets.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.arsenmonets.dao.impl.UserDAOImpl;
import com.github.arsenmonets.models.User;

public class AuthServiceIntegrationTest {

    private AuthService authService;
    private UserDAOImpl userDAO;

    @BeforeEach
    public void setUp() {
        userDAO = new UserDAOImpl();
        authService = new AuthService(userDAO);
    }

    @Test
    public void testSuccessfulLoginWithValidCredentials() {
        authService.register("john_doe", "secure_password", "Customer");
        User user = authService.login("john_doe", "secure_password");

        assertNotNull(user);
        assertEquals("john_doe", user.getUsername());
        assertEquals("Customer", user.getRole());
    }

    @Test
    public void testLoginAfterRegistration() {
        String username = "alice";
        String password = "alice_pwd_123";

        boolean registered = authService.register(username, password, "Seller");
        assertTrue(registered);

        User user = authService.login(username, password);
        assertNotNull(user);
        assertEquals(username, user.getUsername());
        assertEquals("Seller", user.getRole());
    }

    @Test
    public void testLoginWithCaseSensitiveUsername() {
        authService.register("TestUser", "password", "Customer");
        User user = authService.login("TestUser", "password");
        assertNotNull(user);
    }

    @Test
    public void testSuccessfulRegistrationWithValidData() {
        boolean result = authService.register("newuser", "password123", "Customer");
        assertTrue(result);

        User user = authService.login("newuser", "password123");
        assertNotNull(user);
    }

    @Test
    public void testSuccessfulRegistrationAsCustomer() {
        boolean result = authService.register("customer_user", "pass", "Customer");
        assertTrue(result);

        User user = authService.login("customer_user", "pass");
        assertEquals("Customer", user.getRole());
    }

    @Test
    public void testSuccessfulRegistrationAsSeller() {
        boolean result = authService.register("seller_user", "pass", "Seller");
        assertTrue(result);

        User user = authService.login("seller_user", "pass");
        assertEquals("Seller", user.getRole());
    }

    @Test
    public void testMultipleUsersCanRegisterAndLogin() {
        authService.register("user1", "pass1", "Customer");
        User user1 = authService.login("user1", "pass1");
        assertEquals("user1", user1.getUsername());

        authService.register("user2", "pass2", "Seller");
        User user2 = authService.login("user2", "pass2");
        assertEquals("user2", user2.getUsername());

        User user1Again = authService.login("user1", "pass1");
        User user2Again = authService.login("user2", "pass2");

        assertNotNull(user1Again);
        assertNotNull(user2Again);
    }

    @Test
    public void testValidatePasswordMatchWithMatchingPasswords() {
        authService.validatePasswordMatch("password123", "password123");
    }

    @Test
    public void testValidatePasswordMatchWithSpecialCharacters() {
        String password = "p@ssw0rd!#$%";
        authService.validatePasswordMatch(password, password);
    }

    @Test
    public void testValidatePasswordMatchWithLongPassword() {
        String password = "this_is_a_very_long_password_with_many_characters_123456789";
        authService.validatePasswordMatch(password, password);
    }
}
