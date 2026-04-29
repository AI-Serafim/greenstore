package com.example.dao;

import com.example.beans.CartItem;
import com.example.beans.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CartDAOImpl implements CartDAO {
    @Override
    public boolean addToCart(CartItem item) {
        String checkSql = "SELECT quantity FROM cart WHERE user_id = ? AND product_id = ?";
        String insertSql = "INSERT INTO cart (user_id, product_id, quantity) VALUES (?, ?, ?)";
        String updateSql = "UPDATE cart SET quantity = quantity + ? WHERE user_id = ? AND product_id = ?";
        
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setInt(1, item.getUserId());
            checkStmt.setInt(2, item.getProductId());
            ResultSet rs = checkStmt.executeQuery();
            
            if (rs.next()) {
                PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                updateStmt.setInt(1, item.getQuantity());
                updateStmt.setInt(2, item.getUserId());
                updateStmt.setInt(3, item.getProductId());
                updateStmt.executeUpdate();
            } else {
                PreparedStatement insertStmt = conn.prepareStatement(insertSql);
                insertStmt.setInt(1, item.getUserId());
                insertStmt.setInt(2, item.getProductId());
                insertStmt.setInt(3, item.getQuantity());
                insertStmt.executeUpdate();
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateCartItem(int userId, int productId, int quantity) {
        String sql = "UPDATE cart SET quantity = ? WHERE user_id = ? AND product_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, quantity);
            pstmt.setInt(2, userId);
            pstmt.setInt(3, productId);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean removeFromCart(int userId, int productId) {
        String sql = "DELETE FROM cart WHERE user_id = ? AND product_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, productId);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<CartItem> getCartItems(int userId) {
        List<CartItem> cartItems = new ArrayList<>();
        String sql = "SELECT c.*, p.name, p.description, p.price, p.category_id " +
                     "FROM cart c JOIN products p ON c.product_id = p.id WHERE c.user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                CartItem item = new CartItem();
                item.setUserId(rs.getInt("user_id"));
                item.setProductId(rs.getInt("product_id"));
                item.setQuantity(rs.getInt("quantity"));
                
                Product product = new Product();
                product.setId(rs.getInt("product_id"));
                product.setName(rs.getString("name"));
                product.setDescription(rs.getString("description"));
                product.setPrice(rs.getDouble("price"));
                product.setCategoryId(rs.getInt("category_id"));
                item.setProduct(product);
                
                cartItems.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cartItems;
    }

    @Override
    public boolean clearCart(int userId) {
        String sql = "DELETE FROM cart WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
