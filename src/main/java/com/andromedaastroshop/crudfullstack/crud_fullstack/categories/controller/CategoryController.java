package com.andromedaastroshop.crudfullstack.crud_fullstack.categories.controller;

import com.andromedaastroshop.crudfullstack.crud_fullstack.categories.dto.CategoryResponse;
import com.andromedaastroshop.crudfullstack.crud_fullstack.categories.dto.CreateCategoryRequest;
import com.andromedaastroshop.crudfullstack.crud_fullstack.categories.service.CategoryService;
import com.andromedaastroshop.crudfullstack.crud_fullstack.shared.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<CategoryResponse>> create(@Valid @RequestBody CreateCategoryRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Categoría creada correctamente", categoryService.create(request))
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success("Categoría encontrada", categoryService.findById(id))
        );
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<ApiResponse<CategoryResponse>> findBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(
                ApiResponse.success("Categoría encontrada", categoryService.findBySlug(slug))
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> findAll() {
        return ResponseEntity.ok(
                ApiResponse.success("Categorías encontradas", categoryService.findAllCategories())
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody CreateCategoryRequest request
    ) {
        return ResponseEntity.ok(
                ApiResponse.success("Categoría actualizada correctamente", categoryService.updateById(id, request))
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        categoryService.deleteById(id);
        return ResponseEntity.ok(
                ApiResponse.success("Categoría eliminada correctamente", null)
        );
    }
}
