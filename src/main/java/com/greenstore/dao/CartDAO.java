package com.greenstore.dao;

import com.greenstore.model.CartItem;
import com.greenstore.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO для работы с корзиной
 */
public class CartDAO {

    public List<CartItem> findByUserId(int userId) {
        List<CartItem> items = new ArrayList<>();
        String sql = "SELECT c.id, c.user_id, c.product_id, c.quantity, p.name as product_name, " +
                     "p.price, p.image_url FROM cart c " +
                     "JOIN products p ON c.product_id = p.id " +
                     "WHERE c.user_id = ? ORDER BY c.added_at DESC";
        
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                items.add(mapResultSetToCartItem(resultSet));
            }
        } catch (SQLException e) {
            // Обработка ошибки
        }
        return items;
    }

    public boolean addOrUpdate(CartItem item) {
        String checkSql = "SELECT id, quantity FROM cart WHERE user_id = ? AND product_id = ?";
        String updateSql = "UPDATE cart SET quantity = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        String insertSql = "INSERT INTO cart (user_id, product_id, quantity) VALUES (?, ?, ?)";
        
        try (Connection connection = DatabaseConnection.getConnection()) {
            // Проверяем существует ли запись
            try (PreparedStatement checkStmt = connection.prepareStatement(checkSql)) {
                checkStmt.setInt(1, item.getUserId());
                checkStmt.setInt(2, item.getProductId());
                ResultSet rs = checkStmt.executeQuery();
                
                if (rs.next()) {
                    // Обновляем количество
                    int currentQty = rs.getInt("quantity");
                    int cartId = rs.getInt("id");
                    try (PreparedStatement updateStmt = connection.prepareStatement(updateSql)) {
                        updateStmt.setInt(1, currentQty + item.getQuantity());
                        updateStmt.setInt(2, cartId);
                        return updateStmt.executeUpdate() > 0;
                    }
                } else {
                    // Создаем новую запись
                    try (PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {
                        insertStmt.setInt(1, item.getUserId());
                        insertStmt.setInt(2, item.getProductId());
                        insertStmt.setInt(3, item.getQuantity());
                        return insertStmt.executeUpdate() > 0;
                    }
                }
            }
        } catch (SQLException e) {
            // Обработка ошибки
        }
        return false;
    }

    public boolean remove(int userId, int productId) {
        String sql = "DELETE FROM cart WHERE user_id = ? AND product_id = ?";
        
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, userId);
            statement.setInt(2, productId);
            
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            // Обработка ошибки
        }
        return false;
    }

    public boolean clear(int userId) {
        String sql = "DELETE FROM cart WHERE user_id = ?";
        
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, userId);
            
            return statement.executeUpdate() >= 0;
        } catch (SQLException e) {
            // Обработка ошибки
        }
        return false;
    }

    private CartItem mapResultSetToCartItem(ResultSet resultSet) throws SQLException {
        CartItem item = new CartItem();
        item.setId(resultSet.getInt("id"));
        item.setUserId(resultSet.getInt("user_id"));
        item.setProductId(resultSet.getInt("product_id"));
        item.setProductName(resultSet.getString("product_name"));
        item.setPrice(resultSet.getBigDecimal("price"));
        item.setImageUrl(resultSet.getString("image_url"));
        item.setQuantity(resultSet.getInt("quantity"));
        return item;
    }
}
