package com.greenstore.service;

import com.greenstore.dao.OrderDAO;
import com.greenstore.dao.ProductDAO;
import com.greenstore.model.CartItem;
import com.greenstore.model.Order;
import com.greenstore.model.OrderItem;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Сервис для работы с заказами
 */
public class OrderService {
    
    private final OrderDAO orderDAO = new OrderDAO();
    private final ProductDAO productDAO = new ProductDAO();
    
    public boolean createOrder(int userId, List<CartItem> cartItems, 
                               String deliveryAddress, String contactPhone, String notes) {
        if (cartItems == null || cartItems.isEmpty()) {
            return false;
        }
        
        Order order = new Order();
        order.setUserId(userId);
        order.setOrderNumber(generateOrderNumber());
        order.setStatus(Order.Status.PENDING);
        order.setDeliveryAddress(deliveryAddress);
        order.setContactPhone(contactPhone);
        order.setNotes(notes);
        
        BigDecimal total = BigDecimal.ZERO;
        
        for (CartItem cartItem : cartItems) {
            OrderItem item = new OrderItem(
                cartItem.getProductId(),
                cartItem.getProductName(),
                cartItem.getQuantity(),
                cartItem.getPrice()
            );
            order.addItem(item);
            total = total.add(item.getSubtotal());
            
            // Обновляем остатки на складе
            productDAO.updateStock(cartItem.getProductId(), cartItem.getQuantity());
        }
        
        order.setTotalAmount(total);
        
        return orderDAO.create(order);
    }
    
    public List<Order> getUserOrders(int userId) {
        return orderDAO.findByUserId(userId);
    }
    
    public Order getOrderById(int id) {
        return orderDAO.findById(id).orElse(null);
    }
    
    private String generateOrderNumber() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return "ORD-" + timestamp + "-" + (int)(Math.random() * 1000);
    }
}
