package com.greenstore.controller;

import com.greenstore.model.CartItem;
import com.greenstore.model.User;
import com.greenstore.service.CartService;
import com.greenstore.service.ProductService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

/**
 * Контроллер для работы с корзиной
 */
@WebServlet(name = "CartServlet", value = {"/cart", "/cart/add", "/cart/remove"})
public class CartServlet extends HttpServlet {
    
    private final CartService cartService = new CartService();
    private final ProductService productService = new ProductService();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("text/html; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        request.setCharacterEncoding("UTF-8");
                        
        String path = request.getServletPath();
        
        if ("/cart".equals(path)) {
            showCart(request, response);
        } else if ("/cart/add".equals(path)) {
            addToCart(request, response);
        } else if ("/cart/remove".equals(path)) {
            removeFromCart(request, response);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        response.setContentType("text/html; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        request.setCharacterEncoding("UTF-8");
        
        String path = request.getServletPath();
        
        if ("/cart/add".equals(path)) {
            addToCart(request, response);
        } else if ("/cart/remove".equals(path)) {
            removeFromCart(request, response);
        }
    }
    
    private void showCart(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        User user = (User) session.getAttribute("user");
        List<CartItem> items = cartService.getCartItems(user.getId());
        BigDecimal total = cartService.calculateTotal(items);
        
        request.setAttribute("cartItems", items);
        request.setAttribute("total", total);
        request.getRequestDispatcher("/WEB-INF/views/cart.jsp").forward(request, response);
    }
    
    private void addToCart(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        String productIdParam = request.getParameter("productId");
        String quantityParam = request.getParameter("quantity");
        
        if (productIdParam == null || productIdParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/products");
            return;
        }
        
        try {
            int productId = Integer.parseInt(productIdParam);
            int quantity = 1;
            
            if (quantityParam != null && !quantityParam.trim().isEmpty()) {
                quantity = Integer.parseInt(quantityParam);
                if (quantity < 1) quantity = 1;
            }
            
            User user = (User) session.getAttribute("user");
            boolean success = cartService.addToCart(user.getId(), productId, quantity);
            
            // Возвращаемся на предыдущую страницу
            String referer = request.getHeader("Referer");
            if (referer != null) {
                response.sendRedirect(referer);
            } else {
                response.sendRedirect(request.getContextPath() + "/products");
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/products");
        }
    }
    
    private void removeFromCart(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        String productIdParam = request.getParameter("productId");
        
        if (productIdParam != null && !productIdParam.trim().isEmpty()) {
            try {
                int productId = Integer.parseInt(productIdParam);
                User user = (User) session.getAttribute("user");
                cartService.removeFromCart(user.getId(), productId);
            } catch (NumberFormatException e) {
                // Игнорируем
            }
        }
        
        response.sendRedirect(request.getContextPath() + "/cart");
    }
}
