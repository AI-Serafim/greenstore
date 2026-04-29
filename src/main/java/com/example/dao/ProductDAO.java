package com.example.dao;

import com.example.beans.Product;

import java.util.List;

public interface ProductDAO {
    List<Product> getAllProducts();
    List<Product> getProductsByCategoryId(int categoryId);
    Product getProductById(int productId);
}
