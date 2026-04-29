package com.example.servlets;

import com.example.beans.User;
import com.example.beans.Order;
import com.example.beans.CartItem;
import com.example.dao.OrderDAO;
import com.example.dao.OrderDAOImpl;
import com.example.dao.CartDAO;
import com.example.dao.CartDAOImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/order/*")
public class OrderServlet extends HttpServlet {
    private OrderDAO orderDAO = new OrderDAOImpl();
    private CartDAO cartDAO = new CartDAOImpl();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getPathInfo();
        if (action == null) action = "";

        User user = (User) req.getSession().getAttribute("user");
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

        switch (action) {
            case "/create":
                createOrder(req, resp, user);
                break;
            default:
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getPathInfo();
        if (action == null) action = "";

        User user = (User) req.getSession().getAttribute("user");
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

        switch (action) {
            case "":
                viewOrderHistory(req, resp, user);
                break;
            default:
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void createOrder(HttpServletRequest req, HttpServletResponse resp, User user) throws IOException {
        List<CartItem> cartItems = cartDAO.getCartItems(user.getId());
        
        if (cartItems.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/cart?error=empty");
            return;
        }

        Order order = new Order();
        order.setUserId(user.getId());
        order.setStatus("PENDING");

        if (orderDAO.createOrder(order, cartItems)) {
            cartDAO.clearCart(user.getId());
            resp.sendRedirect(req.getContextPath() + "/order");
        } else {
            resp.sendRedirect(req.getContextPath() + "/cart?error=1");
        }
    }

    private void viewOrderHistory(HttpServletRequest req, HttpServletResponse resp, User user) throws ServletException, IOException {
        List<Order> orders = orderDAO.getOrderHistory(user.getId());
        req.setAttribute("orders", orders);
        req.getRequestDispatcher("/orders.jsp").forward(req, resp);
    }
}
