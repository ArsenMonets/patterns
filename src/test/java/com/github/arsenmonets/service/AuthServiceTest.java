package com.github.arsenmonets.service;

import com.github.arsenmonets.dao.UserDAO;
import com.github.arsenmonets.models.User;
import com.github.arsenmonets.exception.AuthenticationException;
import com.github.arsenmonets.util.PasswordUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.mockito.Mockito.*;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@RunWith(JUnit4.class)
public class AuthServiceTest {

    @Mock
    private UserDAO userDAO;

    private AuthService authService;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        authService = new AuthService(userDAO);
    }

    @Test(expected = AuthenticationException.class)
    public void testLoginWithNullUsername() {
        authService.login(null, "password123");
    }

    @Test(expected = AuthenticationException.class)
    public void testLoginWithEmptyUsername() {
        authService.login("", "password123");
    }

    @Test(expected = AuthenticationException.class)
    public void testLoginWithWhitespaceUsername() {
        authService.login("   ", "password123");
    }

    @Test(expected = AuthenticationException.class)
    public void testLoginWithNullPassword() {
        authService.login("testuser", null);
    }

    @Test(expected = AuthenticationException.class)
    public void testLoginWithEmptyPassword() {
        authService.login("testuser", "");
    }

    @Test(expected = AuthenticationException.class)
    public void testLoginWithNonExistentUser() {
        when(userDAO.findByUsername("nonexistent")).thenReturn(null);
        authService.login("nonexistent", "password123");
    }

    @Test(expected = AuthenticationException.class)
    public void testLoginWithWrongPassword() {
        String hashedPassword = PasswordUtil.hashPassword("correctPassword");
        User user = new User("testuser", hashedPassword, "Customer");

        when(userDAO.findByUsername("testuser")).thenReturn(user);
        authService.login("testuser", "wrongPassword");
    }

    @Test(expected = AuthenticationException.class)
    public void testLoginWithNullBothUsernameAndPassword() {
        authService.login(null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRegisterWithNullUsername() {
        authService.register(null, "password123", "Customer");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRegisterWithEmptyUsername() {
        authService.register("", "password123", "Customer");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRegisterWithWhitespaceUsername() {
        authService.register("   ", "password123", "Customer");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRegisterWithNullPassword() {
        authService.register("newuser", null, "Customer");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRegisterWithEmptyPassword() {
        authService.register("newuser", "", "Customer");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRegisterWithNullRole() {
        authService.register("newuser", "password123", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRegisterWithInvalidRole() {
        authService.register("newuser", "password123", "Admin");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRegisterWithInvalidRoleLowercase() {
        authService.register("newuser", "password123", "customer");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRegisterWithDuplicateUsername() {
        User existingUser = new User("existinguser", "hashedpass", "Customer");
        when(userDAO.findByUsername("existinguser")).thenReturn(existingUser);

        authService.register("existinguser", "newpassword", "Customer");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidatePasswordMatchWithNullPassword() {
        authService.validatePasswordMatch(null, "password123");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidatePasswordMatchWithNullConfirmPassword() {
        authService.validatePasswordMatch("password123", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidatePasswordMatchWithBothNull() {
        authService.validatePasswordMatch(null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidatePasswordMatchWithMismatch() {
        authService.validatePasswordMatch("password123", "password456");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidatePasswordMatchWithPartialMatch() {
        authService.validatePasswordMatch("password123", "password12");
    }

    @Test
    public void testLoginVerifiesUserDAOCalled() {
        authService.login("testuser", "password123");
        verify(userDAO, times(1)).findByUsername("testuser");
    }

    @Test
    public void testRegisterVerifiesUserDAOSaveCalled() {
        when(userDAO.findByUsername("newuser")).thenReturn(null);
        when(userDAO.save(any(User.class))).thenReturn(true);

        authService.register("newuser", "password123", "Customer");
        verify(userDAO, times(1)).save(any(User.class));
    }
}
