package com.example.servlets;

import com.example.beans.Product;
import com.example.beans.Category;
import com.example.dao.ProductDAO;
import com.example.dao.ProductDAOImpl;
import com.example.dao.CategoryDAO;
import com.example.dao.CategoryDAOImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/products/*")
public class ProductServlet extends HttpServlet {
    private ProductDAO productDAO = new ProductDAOImpl();
    private CategoryDAO categoryDAO = new CategoryDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getPathInfo();
        if (action == null) action = "";

        switch (action) {
            case "":
                listProducts(req, resp);
                break;
            case "/category":
                listProductsByCategory(req, resp);
                break;
            case "/details":
                showProductDetails(req, resp);
                break;
            default:
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void listProducts(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Product> products = productDAO.getAllProducts();
        List<Category> categories = categoryDAO.getAllCategories();
        req.setAttribute("products", products);
        req.setAttribute("categories", categories);
        req.getRequestDispatcher("/products.jsp").forward(req, resp);
    }

    private void listProductsByCategory(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int categoryId = Integer.parseInt(req.getParameter("categoryId"));
        List<Product> products = productDAO.getProductsByCategoryId(categoryId);
        List<Category> categories = categoryDAO.getAllCategories();
        req.setAttribute("products", products);
        req.setAttribute("categories", categories);
        req.getRequestDispatcher("/products.jsp").forward(req, resp);
    }

    private void showProductDetails(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int productId = Integer.parseInt(req.getParameter("id"));
        Product product = productDAO.getProductById(productId);
        req.setAttribute("product", product);
        req.getRequestDispatcher("/product-details.jsp").forward(req, resp);
    }
}
