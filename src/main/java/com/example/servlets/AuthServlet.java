package com.example.servlets;

import com.example.beans.User;
import com.example.dao.UserDAO;
import com.example.dao.UserDAOImpl;
import com.example.util.PasswordUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/auth/*")
public class AuthServlet extends HttpServlet {
    private UserDAO userDAO = new UserDAOImpl();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getPathInfo();
        if (action == null) action = "";

        switch (action) {
            case "/register":
                register(req, resp);
                break;
            case "/login":
                login(req, resp);
                break;
            default:
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getPathInfo();
        if (action == null) action = "";

        switch (action) {
            case "/logout":
                logout(req, resp);
                break;
            default:
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void register(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String name = req.getParameter("name");
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        // Хэшируем пароль перед сохранением
        String hashedPassword = PasswordUtil.hashPassword(password);

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(hashedPassword);
        user.setRole("USER");

        if (userDAO.register(user)) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
        } else {
            resp.sendRedirect(req.getContextPath() + "/register.jsp?error=1");
        }
    }

    private void login(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        User user = userDAO.findByEmail(email);
        // Проверяем пароль с использованием BCrypt
        if (user != null && PasswordUtil.checkPassword(password, user.getPassword())) {
            req.getSession().setAttribute("user", user);
            resp.sendRedirect(req.getContextPath() + "/products");
        } else {
            resp.sendRedirect(req.getContextPath() + "/login.jsp?error=1");
        }
    }

    private void logout(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.getSession().invalidate();
        resp.sendRedirect(req.getContextPath() + "/login.jsp");
    }
}
