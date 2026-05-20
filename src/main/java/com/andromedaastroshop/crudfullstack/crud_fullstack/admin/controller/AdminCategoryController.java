package com.andromedaastroshop.crudfullstack.crud_fullstack.admin.controller;

import com.andromedaastroshop.crudfullstack.crud_fullstack.admin.dto.AdminCategoryResponse;
import com.andromedaastroshop.crudfullstack.crud_fullstack.admin.service.AdminCategoryService;
import com.andromedaastroshop.crudfullstack.crud_fullstack.categories.dto.CreateCategoryRequest;
import com.andromedaastroshop.crudfullstack.crud_fullstack.shared.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/categories")
@PreAuthorize("hasRole('ADMIN')")
public class AdminCategoryController {

    private final AdminCategoryService adminCategoryService;

    public AdminCategoryController(AdminCategoryService adminCategoryService) {
        this.adminCategoryService = adminCategoryService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AdminCategoryResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success("Categorías obtenidas", adminCategoryService.getAll()));
    }

    @GetMapping("/tree")
    public ResponseEntity<ApiResponse<List<AdminCategoryResponse>>> getTree() {
        return ResponseEntity.ok(ApiResponse.success("Árbol de categorías obtenido", adminCategoryService.getTree()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AdminCategoryResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Categoría obtenida", adminCategoryService.getById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AdminCategoryResponse>> create(@Valid @RequestBody CreateCategoryRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Categoría creada", adminCategoryService.create(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AdminCategoryResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody CreateCategoryRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success("Categoría actualizada", adminCategoryService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        adminCategoryService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Categoría eliminada", null));
    }
}