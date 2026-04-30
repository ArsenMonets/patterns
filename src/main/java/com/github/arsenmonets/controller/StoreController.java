package com.github.arsenmonets.controller;

import io.javalin.Javalin;
import io.javalin.http.Context;
import com.github.arsenmonets.service.AuthService;
import com.github.arsenmonets.service.StoreService;
import com.github.arsenmonets.models.User;
import com.github.arsenmonets.models.Product;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StoreController {
    private final AuthService authService;
    private final StoreService storeService;
    private final Javalin app;
    private final RouteHandler routeHandler;

    public StoreController(AuthService authService, StoreService storeService) {
        this.authService = authService;
        this.storeService = storeService;
        this.app = Javalin.create();
        this.routeHandler = new RouteHandler();
        setupRoutes();
    }

    private void setupRoutes() {
        app.get("/", ctx -> routeHandler.handle(ctx, this::home));

        app.get("/register", ctx -> routeHandler.handle(ctx, this::registerPage));
        app.post("/register", ctx -> routeHandler.handle(ctx, this::register));

        app.get("/login", ctx -> routeHandler.handle(ctx, this::loginPage));
        app.post("/login", ctx -> routeHandler.handle(ctx, this::login));

        app.get("/logout", ctx -> routeHandler.handle(ctx, this::logout));

        app.get("/shop", ctx -> routeHandler.handle(ctx, this::shop));
        app.post("/add-to-cart", ctx -> routeHandler.handleAuthenticated(ctx, this::addToCart));
        app.get("/cart", ctx -> routeHandler.handleAuthenticated(ctx, this::viewCart));
        app.post("/checkout", ctx -> routeHandler.handleAuthenticated(ctx, this::checkout));

        app.get("/my-orders", ctx -> routeHandler.handleAuthenticated(ctx, this::myOrders));

        app.get("/admin/orders", ctx -> routeHandler.handleAuthenticated(ctx, this::adminOrders));
        app.post("/admin/confirm-order", ctx -> routeHandler.handleAuthenticated(ctx, this::confirmOrder));
        app.get("/admin/products", ctx -> routeHandler.handleAuthenticated(ctx, this::manageProducts));
        app.post("/admin/add-product", ctx -> routeHandler.handleAuthenticated(ctx, this::addProduct));
        app.post("/admin/delete-product", ctx -> routeHandler.handleAuthenticated(ctx, this::deleteProduct));
    }

    private void home(Context ctx) {
        User user = routeHandler.getSessionUser(ctx);
        Map<String, Object> model = new HashMap<>();
        model.put("user", user);
        routeHandler.renderTemplate(ctx, "home", model);
    }

    private void registerPage(Context ctx) {
        Map<String, Object> model = new HashMap<>();
        routeHandler.renderTemplate(ctx, "register", model);
    }

    private void register(Context ctx) {
        String username = ctx.formParam("username");
        String password = ctx.formParam("password");
        String confirmPassword = ctx.formParam("confirm_password");
        String role = ctx.formParam("role");

        authService.validatePasswordMatch(password, confirmPassword);
        authService.register(username, password, role);
        ctx.redirect("/login?registered=true");
    }

    private void loginPage(Context ctx) {
        Map<String, Object> model = new HashMap<>();
        model.put("registered", ctx.queryParam("registered") != null);
        routeHandler.renderTemplate(ctx, "login", model);
    }

    private void login(Context ctx) {
        String username = ctx.formParam("username");
        String password = ctx.formParam("password");

        User user = authService.login(username, password);
        ctx.sessionAttribute("user", user);
        ctx.redirect(storeService.getPostLoginRedirectPath(user));
    }

    private void logout(Context ctx) {
        ctx.sessionAttribute("user", null);
        ctx.redirect("/");
    }

    private void shop(Context ctx) {
        List<Product> products = storeService.browseProducts();
        Map<String, Object> model = new HashMap<>();
        model.put("products", products);
        routeHandler.renderWithUser(ctx, "shop", model);
    }

    private void addToCart(Context ctx, User user) {
        String productIdStr = ctx.formParam("product_id");
        int productId = Integer.parseInt(productIdStr);
        storeService.addToCartByProductId(user, productId);
        ctx.redirect("/cart");
    }

    private void viewCart(Context ctx, User user) {
        List<Product> cart = storeService.getCart(user);
        double total = storeService.calculateCartTotal(cart);
        Map<String, Object> model = new HashMap<>();
        model.put("cart", cart);
        model.put("total", total);
        routeHandler.renderWithUser(ctx, "cart", model);
    }

    private void checkout(Context ctx, User user) {
        List<Product> cart = storeService.getCart(user);
        storeService.customerCheckout(user, cart);
        ctx.redirect("/my-orders?success=true");
    }

    private void myOrders(Context ctx, User user) {
        List<com.github.arsenmonets.models.Order> orders = storeService.customerViewMyOrders(user);
        Map<String, Object> model = new HashMap<>();
        model.put("orders", orders);
        routeHandler.renderWithUser(ctx, "my-orders", model);
    }

    private void adminOrders(Context ctx, User user) {
        List<com.github.arsenmonets.models.Order> orders = storeService.sellerViewAllOrders(user);
        Map<String, Object> model = new HashMap<>();
        model.put("orders", orders);
        routeHandler.renderWithUser(ctx, "admin-orders", model);
    }

    private void confirmOrder(Context ctx, User user) {
        String orderIdStr = ctx.formParam("order_id");
        int orderId = Integer.parseInt(orderIdStr);
        storeService.sellerConfirmOrder(user, orderId);
        ctx.redirect("/admin/orders?success=true");
    }

    private void manageProducts(Context ctx, User user) {
        List<Product> products = storeService.browseProducts();
        Map<String, Object> model = new HashMap<>();
        model.put("products", products);
        routeHandler.renderWithUser(ctx, "admin-products", model);
    }

    private void addProduct(Context ctx, User user) {
        String name = ctx.formParam("name");
        String priceStr = ctx.formParam("price");
        double price = Double.parseDouble(priceStr);
        storeService.sellerAddProduct(user, name, price);
        ctx.redirect("/admin/products?success=true");
    }

    private void deleteProduct(Context ctx, User user) {
        int productId = Integer.parseInt(ctx.formParam("product_id"));
        storeService.sellerDeleteProduct(user, productId);
        ctx.redirect("/admin/products?success=true");
    }

    public void start(int port) {
        app.start(port);
    }

    public void stop() {
        app.stop();
    }

    public Javalin getApp() {
        return app;
    }
}
