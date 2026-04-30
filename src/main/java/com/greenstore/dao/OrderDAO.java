package com.greenstore.dao;

import com.greenstore.model.Order;
import com.greenstore.model.OrderItem;
import com.greenstore.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO для работы с заказами
 */
public class OrderDAO {

    public boolean create(Order order) {
        Connection connection = null;
        try {
            connection = DatabaseConnection.getConnection();
            connection.setAutoCommit(false);
            
            String orderSql = "INSERT INTO orders (user_id, order_number, status, total_amount, " +
                              "delivery_address, contact_phone, notes) VALUES (?, ?, ?, ?, ?, ?, ?)";
            
            try (PreparedStatement orderStmt = connection.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS)) {
                orderStmt.setInt(1, order.getUserId());
                orderStmt.setString(2, order.getOrderNumber());
                orderStmt.setString(3, order.getStatus().name());
                orderStmt.setBigDecimal(4, order.getTotalAmount());
                orderStmt.setString(5, order.getDeliveryAddress());
                orderStmt.setString(6, order.getContactPhone());
                orderStmt.setString(7, order.getNotes());
                
                int affectedRows = orderStmt.executeUpdate();
                
                if (affectedRows > 0) {
                    ResultSet generatedKeys = orderStmt.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        order.setId(generatedKeys.getInt(1));
                    }
                    
                    // Вставляем элементы заказа
                    String itemSql = "INSERT INTO order_items (order_id, product_id, product_name, " +
                                     "quantity, unit_price, subtotal) VALUES (?, ?, ?, ?, ?, ?)";
                    
                    try (PreparedStatement itemStmt = connection.prepareStatement(itemSql)) {
                        for (OrderItem item : order.getItems()) {
                            itemStmt.setInt(1, order.getId());
                            itemStmt.setInt(2, item.getProductId());
                            itemStmt.setString(3, item.getProductName());
                            itemStmt.setInt(4, item.getQuantity());
                            itemStmt.setBigDecimal(5, item.getUnitPrice());
                            itemStmt.setBigDecimal(6, item.getSubtotal());
                            itemStmt.addBatch();
                        }
                        itemStmt.executeBatch();
                    }
                    
                    connection.commit();
                    return true;
                }
            }
            connection.rollback();
        } catch (SQLException e) {
            try {
                if (connection != null) connection.rollback();
            } catch (SQLException ex) {
                // Игнорируем ошибку отката
            }
        } finally {
            try {
                if (connection != null) {
                    connection.setAutoCommit(true);
                    connection.close();
                }
            } catch (SQLException e) {
                // Игнорируем
            }
        }
        return false;
    }

    public List<Order> findByUserId(int userId) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE user_id = ? ORDER BY created_at DESC";
        
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                orders.add(mapResultSetToOrder(resultSet));
            }
        } catch (SQLException e) {
            // Обработка ошибки
        }
        return orders;
    }

    public Optional<Order> findById(int id) {
        String sql = "SELECT * FROM orders WHERE id = ?";
        
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                Order order = mapResultSetToOrder(resultSet);
                // Загружаем элементы заказа
                order.setItems(findItemsByOrderId(id));
                return Optional.of(order);
            }
        } catch (SQLException e) {
            // Обработка ошибки
        }
        return Optional.empty();
    }

    private List<OrderItem> findItemsByOrderId(int orderId) {
        List<OrderItem> items = new ArrayList<>();
        String sql = "SELECT * FROM order_items WHERE order_id = ?";
        
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, orderId);
            ResultSet resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                items.add(mapResultSetToOrderItem(resultSet));
            }
        } catch (SQLException e) {
            // Обработка ошибки
        }
        return items;
    }

    private Order mapResultSetToOrder(ResultSet resultSet) throws SQLException {
        Order order = new Order();
        order.setId(resultSet.getInt("id"));
        order.setUserId(resultSet.getInt("user_id"));
        order.setOrderNumber(resultSet.getString("order_number"));
        order.setStatus(Order.Status.valueOf(resultSet.getString("status")));
        order.setTotalAmount(resultSet.getBigDecimal("total_amount"));
        order.setDeliveryAddress(resultSet.getString("delivery_address"));
        order.setContactPhone(resultSet.getString("contact_phone"));
        order.setNotes(resultSet.getString("notes"));
        order.setCreatedAt(resultSet.getTimestamp("created_at"));
        order.setUpdatedAt(resultSet.getTimestamp("updated_at"));
        return order;
    }

    private OrderItem mapResultSetToOrderItem(ResultSet resultSet) throws SQLException {
        OrderItem item = new OrderItem();
        item.setId(resultSet.getInt("id"));
        item.setOrderId(resultSet.getInt("order_id"));
        item.setProductId(resultSet.getInt("product_id"));
        item.setProductName(resultSet.getString("product_name"));
        item.setQuantity(resultSet.getInt("quantity"));
        item.setUnitPrice(resultSet.getBigDecimal("unit_price"));
        item.setSubtotal(resultSet.getBigDecimal("subtotal"));
        return item;
    }
}
