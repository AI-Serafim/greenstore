package com.greenstore.service;

import com.greenstore.dao.CartDAO;
import com.greenstore.model.CartItem;

import java.math.BigDecimal;
import java.util.List;

/**
 * Сервис для работы с корзиной
 */
public class CartService {
    
    private final CartDAO cartDAO = new CartDAO();
    
    public List<CartItem> getCartItems(int userId) {
        return cartDAO.findByUserId(userId);
    }
    
    public boolean addToCart(int userId, int productId, int quantity) {
        CartItem item = new CartItem(userId, productId, quantity);
        return cartDAO.addOrUpdate(item);
    }
    
    public boolean removeFromCart(int userId, int productId) {
        return cartDAO.remove(userId, productId);
    }
    
    public boolean clearCart(int userId) {
        return cartDAO.clear(userId);
    }
    
    public BigDecimal calculateTotal(List<CartItem> items) {
        if (items == null || items.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        return items.stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
