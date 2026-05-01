package com.greenstore.controller;

import com.greenstore.model.Category;
import com.greenstore.model.Product;
import com.greenstore.service.DataLoader;
import com.greenstore.service.ProductService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Контроллер для работы с товарами и категориями
 */
@WebServlet(name = "ProductServlet", value = {"/products", "/category", "/product"})
public class ProductServlet extends HttpServlet {
    
    private final ProductService productService = new ProductService();
    private static boolean initialized = false;
    
    @Override
    public void init() throws ServletException {
        super.init();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String path = request.getServletPath();
        
        if ("/products".equals(path)) {
            showAllProducts(request, response);
        } else if ("/category".equals(path)) {
            showProductsByCategory(request, response);
        } else if ("/product".equals(path)) {
            showProductDetails(request, response);
        }
    }
    
    private void showAllProducts(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        List<Product> products = productService.getAllProducts();
        List<Category> categories = productService.getAllCategories();
        
        request.setAttribute("products", products);
        request.setAttribute("categories", categories);
        request.getRequestDispatcher("/WEB-INF/views/products.jsp").forward(request, response);
    }
    
    private void showProductsByCategory(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String categoryIdParam = request.getParameter("id");
        
        if (categoryIdParam == null || categoryIdParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/products");
            return;
        }
        
        try {
            int categoryId = Integer.parseInt(categoryIdParam);
            List<Product> products = productService.getProductsByCategory(categoryId);
            List<Category> categories = productService.getAllCategories();
            Category currentCategory = productService.getCategoryById(categoryId).orElse(null);
            
            request.setAttribute("products", products);
            request.setAttribute("categories", categories);
            request.setAttribute("currentCategory", currentCategory);
            request.getRequestDispatcher("/WEB-INF/views/products.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/products");
        }
    }
    
    private void showProductDetails(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String productIdParam = request.getParameter("id");
        
        if (productIdParam == null || productIdParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/products");
            return;
        }
        
        try {
            int productId = Integer.parseInt(productIdParam);
            Product product = productService.getProductById(productId).orElse(null);
            
            if (product != null) {
                List<Category> categories = productService.getAllCategories();
                request.setAttribute("product", product);
                request.setAttribute("categories", categories);
                request.getRequestDispatcher("/WEB-INF/views/product-details.jsp").forward(request, response);
            } else {
                response.sendRedirect(request.getContextPath() + "/products");
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/products");
        }
    }
}
