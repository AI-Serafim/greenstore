package com.example.beans;

public class CartItem {
    private int userId;
    private int productId;
    private int quantity;
    private Product product;

    public CartItem() {}

    public int getUserId() { 
        return userId; 
    }
    
    public void setUserId(int userId) { 
        this.userId = userId; 
    }

    public int getProductId() { 
        return productId; 
    }
    
    public void setProductId(int productId) { 
        this.productId = productId; 
    }

    public int getQuantity() { 
        return quantity; 
    }
    
    public void setQuantity(int quantity) { 
        this.quantity = quantity; 
    }

    public Product getProduct() { 
        return product; 
    }
    
    public void setProduct(Product product) { 
        this.product = product; 
    }
}
