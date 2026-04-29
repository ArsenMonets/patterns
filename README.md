
# Store Management System

This project is a simple console-based store management system written in Java. It allows customers to browse products, add them to a cart, and place orders, while sellers (admins) can manage inventory and confirm orders. All data is saved to a text file.

## Features
- User registration and login (Customer or Seller)
- Product catalog browsing
- Shopping cart and order placement for customers
- Inventory management and order confirmation for sellers
- Data persistence to a text file

## Architecture
- **StoreManager**: Repository layer - handles all data access operations through DAOs
- **StoreService**: Service layer with business logic - enforces role-based access control and application workflows
- **AuthService**: Authentication service - handles user login and registration with password hashing
- **StoreController**: Web controller layer (Javalin) - handles HTTP requests/responses and MVC routing
- **DAOs**: Data Access Objects - provide interfaces for database operations
- **DatabaseConnection**: Singleton pattern - manages single database connection instance
- **Views**: HTML templates rendered server-side with variables

## MVC Architecture
The application follows the Model-View-Controller (MVC) pattern:
- **Model**: User, Product, Order classes managed through DAOs
- **View**: HTML templates served by Javalin with variable substitution
- **Controller**: StoreController handles routing and business logic dispatch to StoreService

## Security Features
- Password hashing using BCrypt (12 rounds) for secure user authentication
- Role-based access control (Customer/Seller) enforced at service layer
- Session management for user authentication
- Form-based login/registration with validation

## How to Run
1. Make sure you have Java (version 21 or higher) and Maven installed.
2. Open a terminal in the project directory.
3. Build the project:
    ```bash
    mvn clean package
    ```
4. Run the application:
    ```bash
    java -cp target/umlproject-1.0-SNAPSHOT.jar com.github.arsenmonets.Application
    ```
5. Open your browser and navigate to `http://localhost:8080`

**UML Diagrams**

### Use Case Diagram

**Actors:**

- **Customer**: A user who can browse products, add them to the cart, place orders, and register/login to the system.
- **Seller (Admin)**: A user with administrative rights who can manage the product inventory, confirm orders, and also register/login.

```mermaid
graph LR
    subgraph Actors
        Customer["Customer"]
        Seller["Seller (Admin)"]
    end

    subgraph System
        Login((Login))
        Register((Register))
        
        Browse((Browse Products))
        AddToCart((Add to Cart))
        Checkout((Checkout))
        
        ManageInv((Manage Inventory))
        ConfirmOrder((Confirm Order))
    end

    %% Customer Actions
    Customer --> Browse
    Customer --> AddToCart
    Customer --> Checkout
    Customer --> Login
    Customer --> Register
    
    %% Seller Actions
    Seller --> ManageInv
    Seller --> ConfirmOrder
    Seller --> Login
    Seller --> Register
    
    Checkout -. include .-> Login
    ManageInv -. include .-> Login
    ConfirmOrder -. include .-> Login
    
    Login -. extend .-> Register
    
```


### Class Diagram
```mermaid
classDiagram
    class User {
        -String username
        -String password
        -String role
        +User(String, String, String)
        +getUsername() String
        +setUsername(String) void
        +getPassword() String
        +setPassword(String) void
        +getRole() String
        +setRole(String) void
        +toString() String
    }

    class Product {
        -String name
        -double price
        +Product(String, double)
        +getName() String
        +setName(String) void
        +getPrice() double
        +setPrice(double) void
        +toString() String
    }

    class Order {
        -int id
        -String customerName
        -String status
        -List~Product~ products
        +Order(int, String, List~Product~, String)
        +getId() int
        +setId(int) void
        +getCustomerName() String
        +setCustomerName(String) void
        +getStatus() String
        +setStatus(String) void
        +getProducts() List
        +setProducts(List) void
        +toString() String
    }

    class DatabaseConnection {
        -static DatabaseConnection instance
        -Connection connection
        -DatabaseConnection()
        +static getInstance() DatabaseConnection
        +getConnection() Connection
        +closeConnection() void
    }

    class UserDAO {
        <<interface>>
        +save(User) boolean
        +findByUsername(String) User
        +findAll() List
        +update(User) boolean
        +delete(String) boolean
    }

    class ProductDAO {
        <<interface>>
        +save(Product) boolean
        +findByName(String) Product
        +findAll() List
        +update(Product) boolean
        +delete(String) boolean
    }

    class OrderDAO {
        <<interface>>
        +save(Order) boolean
        +findById(int) Order
        +findAll() List
        +findByCustomer(String) List
        +update(Order) boolean
        +delete(int) boolean
    }

    class UserDAOImpl {
        -Connection connection
        +UserDAOImpl()
        +save(User) boolean
        +findByUsername(String) User
        +findAll() List
        +update(User) boolean
        +delete(String) boolean
    }

    class ProductDAOImpl {
        -Connection connection
        +ProductDAOImpl()
        +save(Product) boolean
        +findByName(String) Product
        +findAll() List
        +update(Product) boolean
        +delete(String) boolean
    }

    class OrderDAOImpl {
        -Connection connection
        +OrderDAOImpl()
        +save(Order) boolean
        +findById(int) Order
        +findAll() List
        +findByCustomer(String) List
        +update(Order) boolean
        +delete(int) boolean
    }

    class StoreManager {
        -UserDAO userDAO
        -ProductDAO productDAO
        -OrderDAO orderDAO
        -int orderCounter
        +StoreManager(UserDAO, ProductDAO, OrderDAO)
        +getProducts() List
        +getOrders() List
        +getOrdersByCustomer(String) List
        +addOrder(Order) void
        +updateOrderStatus(int, String) void
        +getUsers() List
        +addProduct(Product) void
        +updateProduct(Product) void
        +deleteProduct(String) void
        +getOrderById(int) Order
    }

    class StoreService {
        -StoreManager storeManager
        +StoreService(StoreManager)
        +browseProducts() List
        +findProductByName(String) Product
        +customerCheckout(User, Order) void
        +customerViewMyOrders(User) List
        +sellerViewAllOrders(User) List
        +sellerConfirmOrder(User, int) void
        +sellerAddProduct(User, Product) void
        +sellerUpdateProduct(User, Product) void
        +sellerDeleteProduct(User, String) void
        +sellerManageInventory(User, List) void
    }

    class AuthService {
        -UserDAO userDAO
        +AuthService(UserDAO)
        +login(String, String) User
        +register(String, String, String) boolean
    }

    DatabaseConnection "1" -- "1" UserDAOImpl : provides connection
    DatabaseConnection "1" -- "1" ProductDAOImpl : provides connection
    DatabaseConnection "1" -- "1" OrderDAOImpl : provides connection

    UserDAO <|.. UserDAOImpl
    ProductDAO <|.. ProductDAOImpl
    OrderDAO <|.. OrderDAOImpl

    UserDAO --> User
    ProductDAO --> Product
    OrderDAO --> Order

    StoreManager o-- UserDAO
    StoreManager o-- ProductDAO
    StoreManager o-- OrderDAO

    StoreService --> StoreManager
    StoreService --> User

    Order o-- Product

    AuthService o-- UserDAO
```


### Sequence Diagram

**Scenario Descriptions:**

- **Customer scenario:**
    1. Customer initiates a purchase via the application.
    2. The application creates a new order and sends it to the StoreManager.
    3. StoreManager saves the order via OrderDAOImpl.
    4. OrderDAOImpl persists data to the H2 database.
    5. Application notifies the customer that the order is pending confirmation.

- **Seller scenario:**
    1. Seller initiates order confirmation via the application.
    2. Application requests the list of orders from StoreManager.
    3. StoreManager retrieves orders via OrderDAOImpl.
    4. OrderDAOImpl fetches data from the H2 database.
    5. Seller selects an order to confirm.
    6. Application updates the order status via StoreManager.
    7. StoreManager updates the order via OrderDAOImpl.
    8. OrderDAOImpl persists the update to the H2 database.
    9. Application notifies the seller that the order is confirmed.

```mermaid
sequenceDiagram
    autonumber
    actor Customer
    participant SM as StoreManager
    participant DAO as OrderDAOImpl
    participant DB as H2 Database
    actor Seller

    Note over Customer, DB: Customer Action
    Customer->>SM: addOrder(newOrder)
    SM->>DAO: save(order)
    DAO->>DB: INSERT order data
    DB-->>DAO: Order saved
    DAO-->>SM: true
    SM-->>Customer: Order pending confirmation

    Note over Seller, DB: Seller Action
    Seller->>SM: getOrders()
    SM->>DAO: findAll()
    DAO->>DB: SELECT all orders
    DB-->>DAO: Order list
    DAO-->>SM: List of Orders
    SM-->>Seller: Display orders
    Seller->>SM: updateOrderStatus(ID, "Paid")
    SM->>DAO: update(order)
    DAO->>DB: UPDATE order status
    DB-->>DAO: Status updated
    DAO-->>SM: true
    SM-->>Seller: Order confirmed!
```

## StoreManagerTest: Test Descriptions

1. **testRegisterAndLogin** — checks that a user can register and log in, and that the user's data is stored correctly.
2. **testRegisterDuplicate** — checks that it is not possible to register two users with the same username.
3. **testAddProduct** — checks that after adding a product, the number of products in the store increases by 1.
4. **testAddOrder** — checks that after adding an order, the number of orders in the store increases by 1.
5. **testUpdateOrderStatus** — checks that the status of an order can be changed (e.g., from "Pending" to "Paid").
6. **testGetUsers** — checks that it is possible to get the list of users and add a new user to this list.
7. **testProductToString** — checks that the toString() method in Product returns a string containing the product name.
8. **testOrderToString** — checks that the toString() method in Order returns a string containing the customer's name.
9. **testUserToString** — checks that the toString() method in User returns a string containing the username.
10. **testLoginFail** — checks that logging in with a non-existent user returns null (failure).