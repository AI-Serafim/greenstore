package com.greenstore.listener;

import com.greenstore.service.DataLoader;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Слушатель контекста для инициализации тестовых данных при старте приложения
 */
@WebListener
public class AppContextListener implements ServletContextListener {
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("=== GreenStore Application Starting ===");
        
        // Инициализация тестовых данных
        try {
            DataLoader dataLoader = new DataLoader();
            dataLoader.init();
            System.out.println("=== Test data initialized successfully ===");
        } catch (Exception e) {
            System.err.println("Error initializing test data: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("=== GreenStore Application Ready ===");
    }
    
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("=== GreenStore Application Shutting Down ===");
    }
}
