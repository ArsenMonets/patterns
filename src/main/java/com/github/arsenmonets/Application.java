package com.github.arsenmonets;

import com.github.arsenmonets.controller.StoreController;
import com.github.arsenmonets.dao.UserDAO;
import com.github.arsenmonets.dao.ProductDAO;
import com.github.arsenmonets.dao.OrderDAO;
import com.github.arsenmonets.dao.impl.UserDAOImpl;
import com.github.arsenmonets.dao.impl.ProductDAOImpl;
import com.github.arsenmonets.dao.impl.OrderDAOImpl;
import com.github.arsenmonets.service.AuthService;
import com.github.arsenmonets.service.StoreService;
import com.github.arsenmonets.repository.StoreRepository;

public class Application {
    public static void main(String[] args) {
        UserDAO userDAO = new UserDAOImpl();
        ProductDAO productDAO = new ProductDAOImpl();
        OrderDAO orderDAO = new OrderDAOImpl();

        StoreRepository storeRepository = new StoreRepository(userDAO, productDAO, orderDAO);
        AuthService authService = new AuthService(userDAO);
        StoreService storeService = new StoreService(storeRepository);

        StoreController controller = new StoreController(authService, storeService);
        controller.start(8080);

        System.out.println("Server started on http://localhost:8080");
    }
}
