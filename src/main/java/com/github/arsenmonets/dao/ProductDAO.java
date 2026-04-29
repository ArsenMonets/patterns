package com.github.arsenmonets.dao;

import com.github.arsenmonets.models.Product;
import java.util.List;

public interface ProductDAO {
    boolean save(Product product);

    Product findByName(String name);

    List<Product> findAll();

    boolean update(Product product);

    boolean delete(String name);
}
