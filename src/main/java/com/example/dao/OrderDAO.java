package com.example.dao;

import com.example.beans.Order;
import com.example.beans.OrderItem;
import com.example.beans.CartItem;

import java.util.List;

public interface OrderDAO {
    boolean createOrder(Order order, List<CartItem> cartItems);
    List<Order> getOrderHistory(int userId);
    Order getOrderById(int orderId);
    List<OrderItem> getOrderItems(int orderId);
}
