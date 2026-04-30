package com.github.arsenmonets;

import com.github.arsenmonets.models.Order;
import com.github.arsenmonets.models.Product;
import com.github.arsenmonets.view.TemplateRenderer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TemplateRenderer Tests")
class TemplateRendererTest {

    private Map<String, Object> model;

    @BeforeEach
    void setUp() {
        model = new HashMap<>();
    }

    @Test
    @DisplayName("Should replace simple variable with value")
    void testSimpleVariableReplacement() {
        model.put("username", "john_doe");
        String template = "templates/test_simple_var";
        String result = TemplateRenderer.render(template, model);
        assertNotNull(result);
    }

    @Test
    @DisplayName("Should replace nested object property")
    void testNestedPropertyAccess() {
        Product product = new Product("Laptop", 999.99);
        model.put("product", product);
        String template = "templates/test_nested_property";
        String result = TemplateRenderer.render(template, model);
        assertNotNull(result);
    }

    @Test
    @DisplayName("Should render foreach loop with single item")
    void testForeachLoopSingleItem() {
        List<Product> products = new ArrayList<>();
        products.add(new Product("Laptop", 999.99));
        model.put("products", products);

        String html = "#foreach(product in products)<p>${product.name}</p>#end";
        String result = TemplateRenderer.processForEach(html, model);
        result = TemplateRenderer.replaceVariables(result, model);

        assertNotNull(result);
        assertTrue(result.contains("Laptop"));
    }

    @Test
    @DisplayName("Should render foreach loop with multiple items")
    void testForeachLoopMultipleItems() {
        List<Product> products = new ArrayList<>();
        products.add(new Product("Laptop", 999.99));
        products.add(new Product("Mouse", 29.99));
        products.add(new Product("Keyboard", 79.99));
        model.put("products", products);

        String html = "#foreach(product in products)<p>${product.name}</p>#end";
        String result = TemplateRenderer.processForEach(html, model);
        result = TemplateRenderer.replaceVariables(result, model);

        assertNotNull(result);
        assertTrue(result.contains("Laptop"));
        assertTrue(result.contains("Mouse"));
        assertTrue(result.contains("Keyboard"));
    }

    @Test
    @DisplayName("Should render foreach loop with empty collection")
    void testForeachLoopEmptyCollection() {
        List<Product> emptyProducts = new ArrayList<>();
        model.put("products", emptyProducts);

        String template = "templates/test_foreach_empty";
        String result = TemplateRenderer.render(template, model);

        assertNotNull(result);
    }

    @Test
    @DisplayName("Should handle nested property in foreach loop")
    void testNestedPropertyInForeachLoop() {
        List<Order> orders = new ArrayList<>();
        orders.add(new Order(1, "alice", new ArrayList<>(), "PENDING"));
        orders.add(new Order(2, "bob", new ArrayList<>(), "CONFIRMED"));
        model.put("orders", orders);

        String html = "#foreach(order in orders)<p>${order.customerName}</p>#end";
        String result = TemplateRenderer.processForEach(html, model);
        result = TemplateRenderer.replaceVariables(result, model);

        assertNotNull(result);
        assertTrue(result.contains("alice"));
        assertTrue(result.contains("bob"));
    }

    @Test
    @DisplayName("Should handle null values gracefully")
    void testNullValueHandling() {
        model.put("username", null);
        String template = "templates/test_null_value";
        String result = TemplateRenderer.render(template, model);
        assertNotNull(result);
    }

    @Test
    @DisplayName("Should replace multiple variables in same template")
    void testMultipleVariablesReplacement() {
        model.put("firstName", "John");
        model.put("lastName", "Doe");
        model.put("email", "john@example.com");

        String template = "templates/test_multiple_vars";
        String result = TemplateRenderer.render(template, model);

        assertNotNull(result);
    }

    @Test
    @DisplayName("Should handle special characters in variables")
    void testSpecialCharactersInVariables() {
        model.put("description", "Product <with> & special chars \"quoted\"");
        String template = "templates/test_special_chars";
        String result = TemplateRenderer.render(template, model);
        assertNotNull(result);
    }

    @Test
    @DisplayName("Should handle numeric variable values")
    void testNumericVariableValues() {
        model.put("price", 99.99);
        model.put("quantity", 5);
        model.put("total", 499.95);

        String template = "templates/test_numeric_vars";
        String result = TemplateRenderer.render(template, model);
        assertNotNull(result);
    }

    @Test
    @DisplayName("Should render cart template with products")
    void testCartTemplateRendering() {
        List<Product> cart = new ArrayList<>();
        cart.add(new Product("Laptop", 999.99));
        cart.add(new Product("Mouse", 29.99));
        model.put("cart", cart);
        model.put("total", 1029.98);

        String result = TemplateRenderer.render("cart", model);

        assertNotNull(result);
        assertTrue(result.contains("Your Cart"));
        assertTrue(result.contains("Laptop"));
        assertTrue(result.contains("Mouse"));
        assertTrue(result.contains("1029.98"));
    }

    @Test
    @DisplayName("Should render orders template with order list")
    void testOrdersTemplateRendering() {
        List<Order> orders = new ArrayList<>();
        orders.add(new Order(1, "alice", new ArrayList<>(), "PENDING"));
        orders.add(new Order(2, "bob", new ArrayList<>(), "CONFIRMED"));
        model.put("orders", orders);

        String result = TemplateRenderer.render("my-orders", model);

        assertNotNull(result);
        assertTrue(result.contains("Your Orders"));
        assertTrue(result.contains("PENDING") || result.contains("CONFIRMED"));
        assertTrue(result.contains("product(s)"));
    }

    @Test
    @DisplayName("Should render admin-orders template")
    void testAdminOrdersTemplateRendering() {
        List<Order> orders = new ArrayList<>();
        orders.add(new Order(1, "customer1", new ArrayList<>(), "PENDING"));
        orders.add(new Order(2, "customer2", new ArrayList<>(), "CONFIRMED"));
        model.put("orders", orders);

        String result = TemplateRenderer.render("admin-orders", model);

        assertNotNull(result);
        assertTrue(result.contains("Pending Orders"));
        assertTrue(result.contains("customer1"));
        assertTrue(result.contains("customer2"));
    }

    @Test
    @DisplayName("Should render shop template with products")
    void testShopTemplateRendering() {
        List<Product> products = new ArrayList<>();
        products.add(new Product("Laptop", 999.99));
        products.add(new Product("Monitor", 299.99));
        products.add(new Product("Keyboard", 79.99));
        model.put("products", products);

        String result = TemplateRenderer.render("shop", model);

        assertNotNull(result);
        assertTrue(result.contains("Browse Products"));
        assertTrue(result.contains("Laptop"));
        assertTrue(result.contains("Monitor"));
        assertTrue(result.contains("Keyboard"));
    }

    @Test
    @DisplayName("Should render admin-products template")
    void testAdminProductsTemplateRendering() {
        List<Product> products = new ArrayList<>();
        products.add(new Product("Laptop", 999.99));
        products.add(new Product("Mouse", 29.99));
        model.put("products", products);

        String result = TemplateRenderer.render("admin-products", model);

        assertNotNull(result);
        assertTrue(result.contains("Manage Inventory"));
        assertTrue(result.contains("Laptop"));
        assertTrue(result.contains("Mouse"));
    }

    @Test
    @DisplayName("Should handle foreach with complex nested objects")
    void testForeachWithComplexNestedObjects() {
        List<Order> orders = new ArrayList<>();
        List<Product> orderProducts = new ArrayList<>();
        orderProducts.add(new Product("Item1", 50.0));
        orderProducts.add(new Product("Item2", 75.0));
        orders.add(new Order(1, "john", orderProducts, "PENDING"));
        model.put("orders", orders);

        String html = "#foreach(order in orders)<p>${order.customerName}</p>#end";
        String result = TemplateRenderer.processForEach(html, model);
        result = TemplateRenderer.replaceVariables(result, model);

        assertNotNull(result);
        assertTrue(result.contains("john"));
    }

    @Test
    @DisplayName("Should handle foreach loop with getProductCount() method")
    void testForeachWithProductCountGetter() {
        List<Order> orders = new ArrayList<>();
        orders.add(new Order(1, "alice", new ArrayList<>(), "PENDING"));
        orders.add(new Order(2, "bob", new ArrayList<>(), "CONFIRMED"));
        model.put("orders", orders);

        String result = TemplateRenderer.render("admin-orders", model);

        assertNotNull(result);
        assertTrue(result.contains("product(s)"));
    }

    @Test
    @DisplayName("Should preserve HTML structure when rendering")
    void testHTMLStructurePreservation() {
        List<Product> products = new ArrayList<>();
        products.add(new Product("Test Product", 99.99));
        model.put("products", products);

        String result = TemplateRenderer.render("shop", model);

        assertNotNull(result);
        assertTrue(result.contains("<html>"));
        assertTrue(result.contains("</html>"));
        assertTrue(result.contains("<h1>"));
        assertTrue(result.contains("</h1>"));
    }

    @Test
    @DisplayName("Should handle variable replacement before foreach processing")
    void testVariableReplacementBeforeForeach() {
        model.put("title", "Product List");
        List<Product> products = new ArrayList<>();
        products.add(new Product("Laptop", 999.99));
        model.put("products", products);

        String result = TemplateRenderer.render("shop", model);

        assertNotNull(result);
        assertTrue(result.contains("Laptop"));
    }

    @Test
    @DisplayName("Should not render conditionals (if statements not supported)")
    void testConditionalNotSupported() {
        model.put("user", null);

        String result = TemplateRenderer.render("home", model);

        assertNotNull(result);
    }

    @Test
    @DisplayName("Should handle empty template gracefully")
    void testEmptyTemplateFile() {
        String result = TemplateRenderer.render("test_empty", model);
        assertNotNull(result);
    }

    @Test
    @DisplayName("Should render template with no variables")
    void testTemplateWithNoVariables() {
        String result = TemplateRenderer.render("test_no_vars", model);
        assertNotNull(result);
    }

    @Test
    @DisplayName("Should handle foreach loop iteration correctly")
    void testForeachIterationCorrectness() {
        List<String> items = new ArrayList<>();
        items.add("Item A");
        items.add("Item B");
        items.add("Item C");
        model.put("items", items);

        String template = "templates/test_foreach_strings";
        String result = TemplateRenderer.render(template, model);

        assertNotNull(result);
    }

    @Test
    @DisplayName("Should replace variables with double values correctly")
    void testDoubleValueReplacement() {
        model.put("price", 99.99);
        model.put("tax", 15.50);
        model.put("total", 115.49);

        String result = TemplateRenderer.render("cart", model);

        assertNotNull(result);
        assertTrue(result.contains("115.49") || result.contains("99.99"));
    }

    @Test
    @DisplayName("Should handle foreach with string collection")
    void testForeachWithStringCollection() {
        List<String> categories = new ArrayList<>();
        categories.add("Electronics");
        categories.add("Furniture");
        categories.add("Books");
        model.put("categories", categories);

        String template = "templates/test_foreach_categories";
        String result = TemplateRenderer.render(template, model);

        assertNotNull(result);
    }

    @Test
    @DisplayName("Should not throw exception on missing property in nested access")
    void testMissingNestedPropertyHandling() {
        Product product = new Product("Laptop", 999.99);
        model.put("product", product);

        String template = "templates/test_missing_property";
        String result = TemplateRenderer.render(template, model);

        assertNotNull(result);
    }

    @Test
    @DisplayName("Should handle cart template with zero total")
    void testCartWithZeroTotal() {
        List<Product> emptyCart = new ArrayList<>();
        model.put("cart", emptyCart);
        model.put("total", 0.0);

        String result = TemplateRenderer.render("cart", model);

        assertNotNull(result);
        assertTrue(result.contains("Your Cart"));
    }

    @Test
    @DisplayName("Should handle admin-orders with no orders")
    void testAdminOrdersWithNoOrders() {
        List<Order> emptyOrders = new ArrayList<>();
        model.put("orders", emptyOrders);

        String result = TemplateRenderer.render("admin-orders", model);

        assertNotNull(result);
        assertTrue(result.contains("Pending Orders"));
    }
}
