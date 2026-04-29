package com.example.servlets;

import com.example.beans.User;
import com.example.beans.CartItem;
import com.example.dao.CartDAO;
import com.example.dao.CartDAOImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/cart/*")
public class CartServlet extends HttpServlet {
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
            case "/add":
                addToCart(req, resp, user);
                break;
            case "/remove":
                removeFromCart(req, resp, user);
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
                viewCart(req, resp, user);
                break;
            default:
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void addToCart(HttpServletRequest req, HttpServletResponse resp, User user) throws IOException {
        int productId = Integer.parseInt(req.getParameter("productId"));
        int quantity = Integer.parseInt(req.getParameter("quantity"));

        CartItem item = new CartItem();
        item.setUserId(user.getId());
        item.setProductId(productId);
        item.setQuantity(quantity);

        if (cartDAO.addToCart(item)) {
            resp.sendRedirect(req.getContextPath() + "/cart");
        } else {
            resp.sendRedirect(req.getContextPath() + "/products?error=1");
        }
    }

    private void removeFromCart(HttpServletRequest req, HttpServletResponse resp, User user) throws IOException {
        int productId = Integer.parseInt(req.getParameter("productId"));

        if (cartDAO.removeFromCart(user.getId(), productId)) {
            resp.sendRedirect(req.getContextPath() + "/cart");
        } else {
            resp.sendRedirect(req.getContextPath() + "/cart?error=1");
        }
    }

    private void viewCart(HttpServletRequest req, HttpServletResponse resp, User user) throws ServletException, IOException {
        List<CartItem> cartItems = cartDAO.getCartItems(user.getId());
        double total = 0;
        for (CartItem item : cartItems) {
            total += item.getProduct().getPrice() * item.getQuantity();
        }
        req.setAttribute("cartItems", cartItems);
        req.setAttribute("total", total);
        req.getRequestDispatcher("/cart.jsp").forward(req, resp);
    }
}
