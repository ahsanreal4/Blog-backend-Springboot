package com.springboot.blog.service;

import com.springboot.blog.dto.post.CategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto addCategory(CategoryDto categoryDto);
    CategoryDto getCategoryById(Long categoryId);
    List<CategoryDto> getAllCategories();
    CategoryDto updateCategory(Long categoryId, CategoryDto categoryDto);
    void deleteCategoryById(Long categoryId);
}
