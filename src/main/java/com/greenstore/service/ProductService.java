package com.greenstore.service;

import com.greenstore.dao.CategoryDAO;
import com.greenstore.dao.ProductDAO;
import com.greenstore.model.Category;
import com.greenstore.model.Product;

import java.util.List;
import java.util.Optional;

/**
 * Сервис для работы с товарами и категориями
 */
public class ProductService {
    
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final ProductDAO productDAO = new ProductDAO();
    
    public List<Category> getAllCategories() {
        return categoryDAO.findAll();
    }
    
    public Optional<Category> getCategoryById(int id) {
        return categoryDAO.findById(id);
    }
    
    public List<Product> getAllProducts() {
        return productDAO.findAll();
    }
    
    public List<Product> getProductsByCategory(int categoryId) {
        return productDAO.findByCategory(categoryId);
    }
    
    public Optional<Product> getProductById(int id) {
        return productDAO.findById(id);
    }
}
