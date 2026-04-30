package com.greenstore.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Модель категории товаров
 */
public class Category implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String name;
    private String description;
    private String imageUrl;
    private int sortOrder;
    private boolean active;
    private Timestamp createdAt;

    public Category() {}

    public Category(String name, String description) {
        this.name = name;
        this.description = description;
        this.active = true;
        this.sortOrder = 0;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public int getSortOrder() { return sortOrder; }
    public void setSortOrder(int sortOrder) { this.sortOrder = sortOrder; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
