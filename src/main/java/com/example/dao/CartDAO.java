package com.example.dao;

import com.example.beans.CartItem;

import java.util.List;

public interface CartDAO {
    boolean addToCart(CartItem item);
    boolean updateCartItem(int userId, int productId, int quantity);
    boolean removeFromCart(int userId, int productId);
    List<CartItem> getCartItems(int userId);
    boolean clearCart(int userId);
}
