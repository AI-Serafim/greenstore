package com.greenstore.listener;

import com.greenstore.dao.UserDAO;
import com.greenstore.model.User;
import org.mindrot.jbcrypt.BCrypt;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

@WebListener
public class DataLoader implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // Получаем параметры подключения из контекста (или задаем явно, если нужно)
        String url = System.getenv("DB_URL");
        String user = System.getenv("DB_USER");
        String password = System.getenv("DB_PASSWORD");

        // Если переменные окружения не заданы (локальный запуск без docker env), используем дефолтные для docker-compose
        if (url == null) url = "jdbc:mysql://db:3306/greenstore_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
        if (user == null) user = "greenstore_user";
        if (password == null) password = "greenstore_password";

        try {
            // Ждем немного, пока БД точно поднимется, если это первый старт
            Thread.sleep(5000); 
            
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(url, user, password)) {
                
                // 1. Очищаем таблицу пользователей, чтобы избежать конфликтов при повторных запусках
                // Это критически важно, если том не удалился полностью
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute("DELETE FROM users");
                    System.out.println("Таблица users очищена перед инициализацией.");
                }

                // 2. Создаем тестовых пользователей
                UserDAO userDAO = new UserDAO(); // Убедитесь, что конструктор DAO использует переданное соединение или настроен правильно
                
                // ВАЖНО: В вашем текущем коде UserDAO, скорее всего, создает свое соединение.
                // Для надежности инициализации лучше выполнять SQL напрямую здесь, 
                // чтобы не зависеть от логики DAO, которая может ловить исключения.
                
                String adminEmail = "admin@greenstore.com";
                String adminPass = BCrypt.hashpw("password123", BCrypt.gensalt());
                
                String testEmail = "test@example.com";
                String testPass = BCrypt.hashpw("password123", BCrypt.gensalt());

                // Вставляем админа
                insertUser(conn, adminEmail, adminPass, "Admin", "Adminov", true);
                // Вставляем обычного пользователя
                insertUser(conn, testEmail, testPass, "Test", "Testov", false);

                System.out.println("Тестовые пользователи успешно созданы:");
                System.out.println("Admin: " + adminEmail + " / password123");
                System.out.println("User: " + testEmail + " / password123");

            }
        } catch (Exception e) {
            System.err.println("Ошибка при инициализации данных: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void insertUser(Connection conn, String email, String passwordHash, String firstName, String lastName, boolean isAdmin) throws Exception {
        String sql = "INSERT INTO users (email, password, first_name, last_name, is_admin, created_at) VALUES (?, ?, ?, ?, ?, NOW())";
        try (java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.setString(2, passwordHash);
            pstmt.setString(3, firstName);
            pstmt.setString(4, lastName);
            pstmt.setBoolean(5, isAdmin);
            pstmt.executeUpdate();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Nothing to do
    }
}