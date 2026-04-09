
# Store Management System

This project is a simple console-based store management system written in Java. It allows customers to browse products, add them to a cart, and place orders, while sellers (admins) can manage inventory and confirm orders. All data is saved to a text file.

## Features
- User registration and login (Customer or Seller)
- Product catalog browsing
- Shopping cart and order placement for customers
- Inventory management and order confirmation for sellers
- Data persistence to a text file

## How to Run
1. Make sure you have Java (version 21 or higher) and Maven installed.
2. Open a terminal in the project directory.
3. Build the project:
    ```bash
    mvn clean package
    ```
4. Run the application:
    ```bash
    java -cp target/umlproject-1.0-SNAPSHOT.jar com.github.arsenmonets.ConsoleUI
    ```

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
        +String username
        +String password
        +String role
        +User(String, String, String)
        +toString()
    }

    class Product {
        +String name
        +double price
        +Product(String, double)
        +toString()
    }

    class Order {
        +int id
        +String customerName
        +String status
        +List~Product~ products
        +Order(int, String, List~Product~, String)
        +toString()
    }

    class DataStorage {
        -String filePath
        +DataStorage(String)
        +void saveAll(List~User~, List~Product~, List~Order~)
        +Map~String, List<?>~ loadAll()
    }

    class StoreManager {
        -List~User~ users
        -List~Product~ catalog
        -List~Order~ orders
        -DataStorage storage
        -int orderCounter
        +StoreManager(DataStorage)
        +List~Product~ getProducts()
        +List~Order~ getOrders()
        +void addOrder(Order)
        +void updateOrderStatus(int, String)
        +List~User~ getUsers()
        +void addProduct(Product)
    }

    class AuthService {
        -List~User~ users
        +AuthService(List~User~)
        +User login(String, String)
        +boolean register(String, String, String)
    }

    class ConsoleUI {
        -StoreManager manager
        -AuthService auth
        -Scanner sc
        -User currentUser
        +ConsoleUI(StoreManager, AuthService)
        +void mainLoop()
        +void customerMenu()
        +void sellerMenu()
    }

    StoreManager o-- User
    StoreManager *-- Product
    StoreManager *-- Order
    StoreManager <-- DataStorage

    Order o-- Product

    AuthService o-- User

    ConsoleUI <-- StoreManager
    ConsoleUI <-- AuthService
```


### Sequence Diagram

**Scenario Descriptions:**

- **Customer scenario:**
    1. Customer selects "Buy Items" in the UI.
    2. The UI creates a new order and sends it to the StoreManager.
    3. StoreManager saves all data via DataStorage.
    4. DataStorage confirms saving to the UI.
    5. UI notifies the customer that the order is pending confirmation.

- **Seller scenario:**
    1. Seller opens the "Admin Panel" in the UI.
    2. UI requests the list of orders from StoreManager.
    3. StoreManager returns the list to the UI.
    4. Seller selects an order to confirm.
    5. UI updates the order status via StoreManager.
    6. StoreManager saves all data via DataStorage.
    7. DataStorage confirms update to the UI.
    8. UI notifies the seller that the order is confirmed.
```mermaid
%% Updated Sequence Diagram
sequenceDiagram
    autonumber
    actor Customer
    participant UI as ConsoleUI
    participant SM as StoreManager
    participant DS as DataStorage
    actor Seller

    Note over Customer, DS: Customer Action
    Customer->>UI: Select "Buy Items"
    UI->>SM: addOrder(newOrder)
    SM->>DS: saveAll()
    DS-->>UI: Saved to text file
    UI-->>Customer: "Order pending confirmation"

    Note over Seller, DS: Seller Action
    Seller->>UI: Open "Admin Panel"
    UI->>SM: getOrders()
    SM-->>UI: List of Orders
    Seller->>UI: Confirm Order #ID
    UI->>SM: updateOrderStatus(ID, "Paid")
    SM->>DS: saveAll()
    DS-->>UI: Data Updated
    UI-->>Seller: "Order confirmed!"
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