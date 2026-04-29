package com.github.arsenmonets.service;

import com.github.arsenmonets.dao.UserDAO;
import com.github.arsenmonets.models.User;
import com.github.arsenmonets.exception.AuthenticationException;
import com.github.arsenmonets.util.PasswordUtil;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class AuthServiceTest {

    @Mock
    private UserDAO userDAO;

    private AuthService authService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        authService = new AuthService(userDAO);
    }

    @Test
    public void testLoginWithNullUsername() {
        assertThrows(AuthenticationException.class, () -> authService.login(null, "password123"));
    }

    @Test
    public void testLoginWithEmptyUsername() {
        assertThrows(AuthenticationException.class, () -> authService.login("", "password123"));
    }

    @Test
    public void testLoginWithWhitespaceUsername() {
        assertThrows(AuthenticationException.class, () -> authService.login("   ", "password123"));
    }

    @Test
    public void testLoginWithNullPassword() {
        assertThrows(AuthenticationException.class, () -> authService.login("testuser", null));
    }

    @Test
    public void testLoginWithEmptyPassword() {
        assertThrows(AuthenticationException.class, () -> authService.login("testuser", ""));
    }

    @Test
    public void testLoginWithNonExistentUser() {
        when(userDAO.findByUsername("nonexistent")).thenReturn(null);
        assertThrows(AuthenticationException.class, () -> authService.login("nonexistent", "password123"));
    }

    @Test
    public void testLoginWithWrongPassword() {
        String hashedPassword = PasswordUtil.hashPassword("correctPassword");
        User user = new User("testuser", hashedPassword, "Customer");

        when(userDAO.findByUsername("testuser")).thenReturn(user);
        assertThrows(AuthenticationException.class, () -> authService.login("testuser", "wrongPassword"));
    }

    @Test
    public void testLoginWithNullBothUsernameAndPassword() {
        assertThrows(AuthenticationException.class, () -> authService.login(null, null));
    }

    @Test
    public void testRegisterWithNullUsername() {
        assertThrows(IllegalArgumentException.class, () -> authService.register(null, "password123", "Customer"));
    }

    @Test
    public void testRegisterWithEmptyUsername() {
        assertThrows(IllegalArgumentException.class, () -> authService.register("", "password123", "Customer"));
    }

    @Test
    public void testRegisterWithWhitespaceUsername() {
        assertThrows(IllegalArgumentException.class, () -> authService.register("   ", "password123", "Customer"));
    }

    @Test
    public void testRegisterWithNullPassword() {
        assertThrows(IllegalArgumentException.class, () -> authService.register("newuser", null, "Customer"));
    }

    @Test
    public void testRegisterWithEmptyPassword() {
        assertThrows(IllegalArgumentException.class, () -> authService.register("newuser", "", "Customer"));
    }

    @Test
    public void testRegisterWithNullRole() {
        assertThrows(IllegalArgumentException.class, () -> authService.register("newuser", "password123", null));
    }

    @Test
    public void testRegisterWithInvalidRole() {
        assertThrows(IllegalArgumentException.class, () -> authService.register("newuser", "password123", "Admin"));
    }

    @Test
    public void testRegisterWithInvalidRoleLowercase() {
        assertThrows(IllegalArgumentException.class, () -> authService.register("newuser", "password123", "customer"));
    }

    @Test
    public void testRegisterWithDuplicateUsername() {
        User existingUser = new User("existinguser", "hashedpass", "Customer");
        when(userDAO.findByUsername("existinguser")).thenReturn(existingUser);

        assertThrows(IllegalArgumentException.class,
                () -> authService.register("existinguser", "newpassword", "Customer"));
    }

    @Test
    public void testValidatePasswordMatchWithNullPassword() {
        assertThrows(IllegalArgumentException.class, () -> authService.validatePasswordMatch(null, "password123"));
    }

    @Test
    public void testValidatePasswordMatchWithNullConfirmPassword() {
        assertThrows(IllegalArgumentException.class, () -> authService.validatePasswordMatch("password123", null));
    }

    @Test
    public void testValidatePasswordMatchWithBothNull() {
        assertThrows(IllegalArgumentException.class, () -> authService.validatePasswordMatch(null, null));
    }

    @Test
    public void testValidatePasswordMatchWithMismatch() {
        assertThrows(IllegalArgumentException.class,
                () -> authService.validatePasswordMatch("password123", "password456"));
    }

    @Test
    public void testValidatePasswordMatchWithPartialMatch() {
        assertThrows(IllegalArgumentException.class,
                () -> authService.validatePasswordMatch("password123", "password12"));
    }

}
