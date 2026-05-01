package com.greenstore.listener;

import com.greenstore.service.DataLoader;  // ← Импорт из service
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class AppContextListener implements ServletContextListener {
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("=== GreenStore Starting ===");
        try {
            new DataLoader().init();  // ← Вызываем сервис
            System.out.println("=== Ready ===");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("=== Shutting Down ===");
    }
}