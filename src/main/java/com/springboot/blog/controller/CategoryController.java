package com.springboot.blog.controller;

import com.springboot.blog.dto.post.CategoryDto;
import com.springboot.blog.service.CategoryService;
import com.springboot.blog.utils.constants.UserRoles;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@SecurityRequirement(
        name = "Bear Authentication"
)
@Tag(
        name = "Category"
)
public class CategoryController {

    private CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // Add Category
    @PreAuthorize(UserRoles.HAS_ROLE_ADMIN)
    @PostMapping
    public ResponseEntity<CategoryDto> addCategory(@RequestBody CategoryDto categoryDto){
        CategoryDto savedCategory = categoryService.addCategory(categoryDto);
        return new ResponseEntity<>(savedCategory, HttpStatus.CREATED);
    }

    // Get Category by id
    @GetMapping("/{categoryId}")
    public  ResponseEntity<CategoryDto> getCategory(@PathVariable(name = "categoryId") Long categoryId){
        return ResponseEntity.ok(categoryService.getCategoryById(categoryId));
    }

    // Get all Categories
    @GetMapping
    public  ResponseEntity<List<CategoryDto>> getCategories(){
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    // Update Category by id
    @PreAuthorize(UserRoles.HAS_ROLE_ADMIN)
    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryDto> updateCategory(@RequestBody CategoryDto categoryDto, @PathVariable(name = "categoryId") Long id){
        CategoryDto updatedCategory = categoryService.updateCategory(id, categoryDto);
        return new ResponseEntity<>(updatedCategory, HttpStatus.OK);
    }

    // Delete Category by id
    @PreAuthorize(UserRoles.HAS_ROLE_ADMIN)
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<String> deleteCategory(@PathVariable(name = "categoryId") Long id){
        categoryService.deleteCategoryById(id);
        return new ResponseEntity<>("Category deleted successfully", HttpStatus.OK);
    }
}
