package com.greenstore.dao;

import com.greenstore.model.Product;
import com.greenstore.util.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO для работы с товарами
 */
public class ProductDAO {

    public List<Product> findAll() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT p.*, c.name as category_name FROM products p " +
                     "JOIN categories c ON p.category_id = c.id " +
                     "WHERE p.is_available = TRUE ORDER BY p.name";
        
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            
            while (resultSet.next()) {
                products.add(mapResultSetToProduct(resultSet));
            }
        } catch (SQLException e) {
            // Обработка ошибки
        }
        return products;
    }

    public List<Product> findByCategory(int categoryId) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT p.*, c.name as category_name FROM products p " +
                     "JOIN categories c ON p.category_id = c.id " +
                     "WHERE p.category_id = ? AND p.is_available = TRUE";
        
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, categoryId);
            ResultSet resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                products.add(mapResultSetToProduct(resultSet));
            }
        } catch (SQLException e) {
            // Обработка ошибки
        }
        return products;
    }

    public Optional<Product> findById(int id) {
        String sql = "SELECT p.*, c.name as category_name FROM products p " +
                     "JOIN categories c ON p.category_id = c.id " +
                     "WHERE p.id = ?";
        
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                return Optional.of(mapResultSetToProduct(resultSet));
            }
        } catch (SQLException e) {
            // Обработка ошибки
        }
        return Optional.empty();
    }

    public boolean updateStock(int productId, int quantity) {
        String sql = "UPDATE products SET stock_quantity = stock_quantity - ?, " +
                     "updated_at = CURRENT_TIMESTAMP WHERE id = ? AND stock_quantity >= ?";
        
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, quantity);
            statement.setInt(2, productId);
            statement.setInt(3, quantity);
            
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            // Обработка ошибки
        }
        return false;
    }

    private Product mapResultSetToProduct(ResultSet resultSet) throws SQLException {
        Product product = new Product();
        product.setId(resultSet.getInt("id"));
        product.setCategoryId(resultSet.getInt("category_id"));
        product.setCategoryName(resultSet.getString("category_name"));
        product.setName(resultSet.getString("name"));
        product.setDescription(resultSet.getString("description"));
        product.setPrice(resultSet.getBigDecimal("price"));
        product.setStockQuantity(resultSet.getInt("stock_quantity"));
        product.setImageUrl(resultSet.getString("image_url"));
        product.setAvailable(resultSet.getBoolean("is_available"));
        product.setCreatedAt(resultSet.getTimestamp("created_at"));
        product.setUpdatedAt(resultSet.getTimestamp("updated_at"));
        return product;
    }
}
