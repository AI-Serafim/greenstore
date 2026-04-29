package com.example.dao;

import com.example.beans.Category;

import java.util.List;

public interface CategoryDAO {
    List<Category> getAllCategories();
    Category getCategoryById(int categoryId);
}
