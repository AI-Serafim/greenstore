package com.greenstore.controller;

import com.greenstore.model.CartItem;
import com.greenstore.model.Order;
import com.greenstore.model.User;
import com.greenstore.service.CartService;
import com.greenstore.service.OrderService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/**
 * Контроллер для работы с заказами
 */
@WebServlet(name = "OrderServlet", value = {"/orders", "/checkout"})
public class OrderServlet extends HttpServlet {
    
    private final OrderService orderService = new OrderService();
    private final CartService cartService = new CartService();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String path = request.getServletPath();
        
        if ("/orders".equals(path)) {
            showOrders(request, response);
        } else if ("/checkout".equals(path)) {
            showCheckout(request, response);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String path = request.getServletPath();
        
        if ("/checkout".equals(path)) {
            processCheckout(request, response);
        }
    }
    
    private void showOrders(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        User user = (User) session.getAttribute("user");
        List<Order> orders = orderService.getUserOrders(user.getId());
        
        request.setAttribute("orders", orders);
        request.getRequestDispatcher("/WEB-INF/views/orders.jsp").forward(request, response);
    }
    
    private void showCheckout(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        User user = (User) session.getAttribute("user");
        List<CartItem> items = cartService.getCartItems(user.getId());
        
        if (items == null || items.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }
        
        request.setAttribute("cartItems", items);
        request.setAttribute("total", cartService.calculateTotal(items));
        request.getRequestDispatcher("/WEB-INF/views/checkout.jsp").forward(request, response);
    }
    
    private void processCheckout(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        String deliveryAddress = request.getParameter("deliveryAddress");
        String contactPhone = request.getParameter("contactPhone");
        String notes = request.getParameter("notes");
        
        // Валидация
        if (deliveryAddress == null || deliveryAddress.trim().isEmpty() ||
            contactPhone == null || contactPhone.trim().isEmpty()) {
            request.setAttribute("error", "Адрес доставки и телефон обязательны");
            showCheckout(request, response);
            return;
        }
        
        User user = (User) session.getAttribute("user");
        List<CartItem> items = cartService.getCartItems(user.getId());
        
        if (items == null || items.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }
        
        boolean success = orderService.createOrder(
            user.getId(), 
            items, 
            deliveryAddress.trim(), 
            contactPhone.trim(), 
            notes != null ? notes.trim() : ""
        );
        
        if (success) {
            // Очищаем корзину
            cartService.clearCart(user.getId());
            // Перенаправляем на страницу заказов
            response.sendRedirect(request.getContextPath() + "/orders?success=true");
        } else {
            request.setAttribute("error", "Ошибка при оформлении заказа. Попробуйте позже.");
            showCheckout(request, response);
        }
    }
}
