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
            // Добавляем явное указание collation для гарантии UTF-8
            url = "jdbc:mysql://db:3306/greenstore_db?useSSL=false&allowPublicKeyRetrieval=true" +
                  "&serverTimezone=UTC&characterEncoding=UTF-8&useUnicode=true&connectionCollation=utf8mb4_unicode_ci";
        }
        if (user == null) user = "greenstore_user";
        if (password == null) password = "greenstore_password";

        int maxRetries = 15;
        int retryDelayMs = 2000;

        // Фиксированный BCrypt хеш для пароля "password123"
        // Генерируется один раз и всегда одинаков для этой соли
        String fixedPasswordHash = "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy";
        
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");

                try (Connection conn = DriverManager.getConnection(url, user, password)) {
                    System.out.println("=== GreenStore Starting ===");
                    
                    // 1. Проверяем наличие товаров (для информации)
                    boolean dataExists = !productService.getAllProducts().isEmpty();
                    if (dataExists) {
                        System.out.println("✓ Данные найдены в БД (загружены через init.sql)");
                    } else {
                        System.out.println("⚠ Товары не найдены. Проверьте init.sql");
                        // Можно попробовать загрузить их здесь, если init.sql не сработал
                        loadCategories(conn);
                        loadProducts(conn, productService);
                    }
                    
                    // 2. ВАЖНО: Всегда пересоздаем тестовых пользователей, чтобы сбросить пароль
                    System.out.println("🔄 Обновление тестовых пользователей...");
                    try (Statement stmt = conn.createStatement()) {
                        // Удаляем старых тестовых юзеров
                        stmt.execute("DELETE FROM users WHERE email IN ('admin@greenstore.com', 'test@example.com')");
                    }
                    
                    // Создаем новых с гарантированным паролем
                    insertUser(conn, "admin@greenstore.com", fixedPasswordHash, "Админ", "Системный", "+7 (999) 000-00-00", "г. Москва", true);
                    insertUser(conn, "test@example.com", fixedPasswordHash, "Иван", "Петров", "+7 (999) 123-45-67", "г. Москва, ул. Тестовая, д. 1", false);
                    
                    System.out.println("✓ Тестовые пользователи созданы (пароль: password123)");
                    System.out.println("=== Ready ===");
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
        // Проверка на пустоту перед вставкой, чтобы не дублировать при ошибке логики
        String checkSql = "SELECT COUNT(*) FROM categories";
        try (PreparedStatement checkStmt = conn.prepareStatement(checkSql);
             ResultSet rs = checkStmt.executeQuery()) {
            if (rs.next() && rs.getInt(1) > 0) {
                return; // Категории уже есть
            }
        }

        String[] categories = {
            "Эко-продукты питания",
            "Бытовая химия",
            "Косметика и уход",
            "Товары для дома",
            "Одежда и текстиль"
        };
        
        String sql = "INSERT INTO categories (name) VALUES (?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (String categoryName : categories) {
                pstmt.setString(1, categoryName);
                pstmt.executeUpdate();
            }
        }
        System.out.println("✓ Категории загружены через Java");
    }
    
    private static void loadProducts(Connection conn, ProductService productService) throws SQLException {
        if (!productService.getAllProducts().isEmpty()) {
            return;
        }

        String[][] products = {
            {"Органический мёд", "Натуральный горный мёд", "450.00", "1"},
            {"Чиа семена", "Органические семена чиа", "320.00", "1"},
            {"Зелёный чай матча", "Японский чай матча", "890.00", "1"},
            {"Эко-средство для посуды", "Без фосфатов и аллергенов", "280.00", "2"},
            {"Универсальный очиститель", "На основе цитрусовых", "350.00", "2"},
            {"Шампунь без сульфатов", "Для чувствительной кожи", "520.00", "3"},
            {"Крем для лица", "Увлажняющий органический", "780.00", "3"},
            {"Бамбуковые зубные щётки", "Набор из 4 штук", "290.00", "4"},
            {"Многоразовые сумки", "Набор шопперов", "450.00", "4"},
            {"Хлопковая футболка", "Из органического хлопка", "1200.00", "5"}
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
        System.out.println("✓ Товары загружены через Java");
    }
    
    private static void insertUser(Connection conn, String email, String passwordHash, 
                           String firstName, String lastName, String phone, String address, boolean isAdmin) throws SQLException {
        String sql = "INSERT INTO users (email, password_hash, first_name, last_name, phone, address, role) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.setString(2, passwordHash);
            pstmt.setString(3, firstName);
            pstmt.setString(4, lastName);
            pstmt.setString(5, phone);
            pstmt.setString(6, address);
            pstmt.setString(7, isAdmin ? "ADMIN" : "USER");
            pstmt.executeUpdate();
        }
    }
}