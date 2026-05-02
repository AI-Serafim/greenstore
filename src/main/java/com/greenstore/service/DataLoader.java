package com.greenstore.service;

import com.greenstore.dao.UserDAO;
import com.greenstore.model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;

public class DataLoader {
    
    public static void loadData(ProductService productService) {
        String url = System.getenv("DB_URL");
        String user = System.getenv("DB_USER");
        String password = System.getenv("DB_PASSWORD");

        if (url == null) {
            url = "jdbc:mysql://db:3306/greenstore_db?useSSL=false&allowPublicKeyRetrieval=true" +
                  "&serverTimezone=UTC&characterEncoding=UTF-8&useUnicode=true";
        }
        if (user == null) user = "greenstore_user";
        if (password == null) password = "greenstore_password";

        int maxRetries = 15;
        int retryDelayMs = 2000;
        
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                
                try (Connection conn = DriverManager.getConnection(url, user, password)) {
                    // Проверяем, есть ли уже товары
                    if (!productService.getAllProducts().isEmpty()) {
                        System.out.println("✓ Данные уже загружены");
                        return;
                    }
                    
                    // Загружаем категории
                    loadCategories(conn);
                    
                    // Загружаем товары
                    loadProducts(conn, productService);
                    
                    // Очищаем таблицу пользователей для тестов и создаём тестовых пользователей
                    try (Statement stmt = conn.createStatement()) {
                        stmt.execute("DELETE FROM users");
                    }
                    
                    String adminPass = BCrypt.hashpw("password123", BCrypt.gensalt());
                    String testPass = BCrypt.hashpw("password123", BCrypt.gensalt());
                    
                    insertUser(conn, "admin@greenstore.com", adminPass, "Admin", "Adminov", true);
                    insertUser(conn, "test@example.com", testPass, "Test", "Testov", false);
                    
                    System.out.println("✓ Тестовые данные и пользователи созданы");
                    return;
                }
            } catch (Exception e) {
                System.err.println("⏳ Попытка " + attempt + "/" + maxRetries + ": " + e.getMessage());
                if (attempt == maxRetries) {
                    System.err.println("❌ Не удалось подключиться к БД после " + maxRetries + " попыток");
                    e.printStackTrace();
                    return;
                }
                try {
                    Thread.sleep(retryDelayMs);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
    }
    
    private static void loadCategories(Connection conn) throws SQLException {
        String[] categories = {
            "Бытовая химия",
            "Косметика и уход",
            "Товары для дома",
            "Одежда и текстиль",
            "Посуда и кухня"
        };
        
        String sql = "INSERT INTO categories (name) VALUES (?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (String categoryName : categories) {
                pstmt.setString(1, categoryName);
                pstmt.executeUpdate();
            }
        }
        System.out.println("✓ Категории загружены");
    }
    
    private static void loadProducts(Connection conn, ProductService productService) throws SQLException {
        String[][] products = {
            {"Эко-шампунь 'Лаванда'", "Натуральный шампунь без сульфатов", "29.99", "1"},
            {"Биоразлагаемый стиральный порошок", "Экологичный порошок для стирки", "45.00", "1"},
            {"Бамбуковая зубная щетка", "Зубная щетка из бамбука", "15.50", "2"},
            {"Органическое мыло 'Мята'", "Мыло ручной работы", "12.00", "2"},
            {"Многоразовая сумка-шоппер", "Сумка из органического хлопка", "25.00", "3"},
            {"Набор бамбуковых полотенец", "Набор из 3 полотенец", "89.99", "4"},
            {"Стеклянная бутылка для воды", "Бутылка с силиконовым чехлом", "35.00", "3"},
            {"Набор контейнеров для хранения", "Контейнеры из стекла", "55.00", "5"},
            {"Восковые салфетки для еды", "Многоразовые салфетки", "22.00", "3"},
            {"Щетка для посуды из кокоса", "Натуральная щетка", "18.00", "5"}
        };
        
        String sql = "INSERT INTO products (name, description, price, category_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (String[] product : products) {
                pstmt.setString(1, product[0]);
                pstmt.setString(2, product[1]);
                pstmt.setDouble(3, Double.parseDouble(product[2]));
                pstmt.setInt(4, Integer.parseInt(product[3]));
                pstmt.executeUpdate();
            }
        }
        System.out.println("✓ Товары загружены");
    }
    
    private static void insertUser(Connection conn, String email, String passwordHash, 
                           String firstName, String lastName, boolean isAdmin) throws SQLException {
        String sql = "INSERT INTO users (email, password_hash, first_name, last_name, role) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.setString(2, passwordHash);
            pstmt.setString(3, firstName);
            pstmt.setString(4, lastName);
            pstmt.setString(5, isAdmin ? "ADMIN" : "USER");
            pstmt.executeUpdate();
        }
    }
}