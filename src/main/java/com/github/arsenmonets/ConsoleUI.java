package com.github.arsenmonets;

import java.util.*;

public class ConsoleUI {
    public static void main(String[] args) {
        DataStorage storage = new DataStorage("data.txt");
        StoreManager manager = new StoreManager(storage);
        AuthService auth = new AuthService(manager.getUsers());
        ConsoleUI ui = new ConsoleUI(manager, auth);
        ui.mainLoop();
    }
    private StoreManager manager;
    private AuthService auth;
    private Scanner sc = new Scanner(System.in);
    private User currentUser;

    public ConsoleUI(StoreManager manager, AuthService auth) {
        this.manager = manager;
        this.auth = auth;
    }

    public void mainLoop() {
        while (true) {
            System.out.println("1. Login\n2. Register\n0. Exit");
            String c = sc.nextLine();
            if (c.equals("1")) login();
            else if (c.equals("2")) register();
            else break;
        }
    }

    private void login() {
        System.out.print("Username: "); String u = sc.nextLine();
        System.out.print("Password: "); String p = sc.nextLine();
        currentUser = auth.login(u, p);
        if (currentUser == null) System.out.println("Login failed");
        else if (currentUser.role.equals("Customer")) customerMenu();
        else sellerMenu();
    }

    private void register() {
        System.out.print("Username: "); String u = sc.nextLine();
        System.out.print("Password: "); String p = sc.nextLine();
        System.out.print("Role (Customer/Seller): "); String r = sc.nextLine();
        if (auth.register(u, p, r)) System.out.println("Registered!");
        else System.out.println("User exists!");
    }

    public void customerMenu() {
        while (true) {
            System.out.println("1. Browse Products\n2. Add to Cart\n3. Checkout\n0. Logout");
            String c = sc.nextLine();
            if (c.equals("1")) browse();
            else if (c.equals("2")) addToCart();
            else if (c.equals("3")) checkout();
            else break;
        }
    }

    private List<Product> cart = new ArrayList<>();
    private void browse() {
        for (Product p : manager.getProducts()) System.out.println(p.name + " - " + p.price);
    }
    private void addToCart() {
        System.out.print("Product name: ");
        String name = sc.nextLine();
        for (Product p : manager.getProducts()) {
            if (p.name.equals(name)) cart.add(p);
        }
    }
    private void checkout() {
        if (cart.isEmpty()) { System.out.println("Cart is empty"); return; }
        manager.addOrder(new Order(new Random().nextInt(10000), currentUser.username, new ArrayList<>(cart), "Pending"));
        cart.clear();
        System.out.println("Order pending confirmation");
    }

    public void sellerMenu() {
        while (true) {
            System.out.println("1. Manage Inventory\n2. Confirm Order\n0. Logout");
            String c = sc.nextLine();
            if (c.equals("1")) manageInventory();
            else if (c.equals("2")) confirmOrder();
            else break;
        }
    }
    private void manageInventory() {
        System.out.println("Products:");
        for (Product p : manager.getProducts()) System.out.println(p.name + " - " + p.price);
        System.out.print("Add product? (y/n): ");
        if (sc.nextLine().equalsIgnoreCase("y")) {
            System.out.print("Name: "); String n = sc.nextLine();
            System.out.print("Price: "); double pr = Double.parseDouble(sc.nextLine());
            manager.addProduct(new Product(n, pr));
        }
    }
    private void confirmOrder() {
        for (Order o : manager.getOrders()) System.out.println(o);
        System.out.print("Order ID to confirm: ");
        int id = Integer.parseInt(sc.nextLine());
        manager.updateOrderStatus(id, "Paid");
        System.out.println("Order confirmed!");
    }
}
