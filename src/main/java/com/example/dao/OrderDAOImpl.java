package com.example.dao;

import com.example.beans.Order;
import com.example.beans.OrderItem;
import com.example.beans.CartItem;
import com.example.beans.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderDAOImpl implements OrderDAO {
    @Override
    public boolean createOrder(Order order, List<CartItem> cartItems) {
        String sql = "INSERT INTO orders (user_id, status) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, order.getUserId());
            pstmt.setString(2, order.getStatus());
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                int orderId = rs.getInt(1);
                for (CartItem item : cartItems) {
                    String sqlItem = "INSERT INTO order_items (order_id, product_id, quantity, price) VALUES (?, ?, ?, ?)";
                    try (PreparedStatement pstmtItem = conn.prepareStatement(sqlItem)) {
                        pstmtItem.setInt(1, orderId);
                        pstmtItem.setInt(2, item.getProductId());
                        pstmtItem.setInt(3, item.getQuantity());
                        pstmtItem.setDouble(4, item.getProduct().getPrice());
                        pstmtItem.executeUpdate();
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<Order> getOrderHistory(int userId) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT o.*, SUM(oi.quantity * oi.price) as total " +
                     "FROM orders o LEFT JOIN order_items oi ON o.id = oi.order_id " +
                     "WHERE o.user_id = ? GROUP BY o.id ORDER BY o.order_date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Order order = new Order();
                order.setId(rs.getInt("id"));
                order.setUserId(rs.getInt("user_id"));
                order.setOrderDate(rs.getTimestamp("order_date"));
                order.setStatus(rs.getString("status"));
                order.setTotal(rs.getDouble("total"));
                orders.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    @Override
    public Order getOrderById(int orderId) {
        String sql = "SELECT o.*, SUM(oi.quantity * oi.price) as total " +
                     "FROM orders o LEFT JOIN order_items oi ON o.id = oi.order_id " +
                     "WHERE o.id = ? GROUP BY o.id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, orderId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Order order = new Order();
                order.setId(rs.getInt("id"));
                order.setUserId(rs.getInt("user_id"));
                order.setOrderDate(rs.getTimestamp("order_date"));
                order.setStatus(rs.getString("status"));
                order.setTotal(rs.getDouble("total"));
                return order;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<OrderItem> getOrderItems(int orderId) {
        List<OrderItem> orderItems = new ArrayList<>();
        String sql = "SELECT oi.*, p.name, p.description, p.category_id " +
                     "FROM order_items oi JOIN products p ON oi.product_id = p.id WHERE oi.order_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, orderId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                OrderItem item = new OrderItem();
                item.setOrderId(rs.getInt("order_id"));
                item.setProductId(rs.getInt("product_id"));
                item.setQuantity(rs.getInt("quantity"));
                item.setPrice(rs.getDouble("price"));
                
                Product product = new Product();
                product.setId(rs.getInt("product_id"));
                product.setName(rs.getString("name"));
                product.setDescription(rs.getString("description"));
                product.setCategoryId(rs.getInt("category_id"));
                item.setProduct(product);
                
                orderItems.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orderItems;
    }
}
