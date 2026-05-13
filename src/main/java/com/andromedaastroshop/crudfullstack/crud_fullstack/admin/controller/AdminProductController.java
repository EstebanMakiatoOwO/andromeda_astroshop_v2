package com.andromedaastroshop.crudfullstack.crud_fullstack.admin.controller;

import com.andromedaastroshop.crudfullstack.crud_fullstack.admin.dto.AdminProductResponse;
import com.andromedaastroshop.crudfullstack.crud_fullstack.admin.service.AdminProductService;
import com.andromedaastroshop.crudfullstack.crud_fullstack.categories.dto.CategoryResponse;
import com.andromedaastroshop.crudfullstack.crud_fullstack.categories.service.CategoryService;
import com.andromedaastroshop.crudfullstack.crud_fullstack.products.dto.CreateProductRequest;
import com.andromedaastroshop.crudfullstack.crud_fullstack.products.dto.UpdateProductRequest;
import com.andromedaastroshop.crudfullstack.crud_fullstack.shared.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/products")
@PreAuthorize("hasRole('ADMIN')")
public class AdminProductController {

    private final AdminProductService adminProductService;
    private final CategoryService categoryService;

    public AdminProductController(AdminProductService adminProductService, CategoryService categoryService) {
        this.adminProductService = adminProductService;
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<AdminProductResponse>>> getProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String availability,
            @RequestParam(required = false) String stock
    ) {
        return ResponseEntity.ok(ApiResponse.success("Productos obtenidos",
                adminProductService.getProducts(page, size, q, status, availability, stock)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AdminProductResponse>> getProduct(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Producto obtenido", adminProductService.getProductById(id)));
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<AdminProductResponse>> create(
            @Valid @RequestPart("request") CreateProductRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        return ResponseEntity.ok(ApiResponse.success("Producto creado", adminProductService.createProduct(request, image)));
    }

    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<AdminProductResponse>> update(
            @PathVariable Long id,
            @Valid @RequestPart("request") UpdateProductRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        return ResponseEntity.ok(ApiResponse.success("Producto actualizado", adminProductService.updateProduct(id, request, image)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        adminProductService.deleteProduct(id);
        return ResponseEntity.ok(ApiResponse.success("Producto eliminado", null));
    }

    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getCategories() {
        return ResponseEntity.ok(ApiResponse.success("Categorías obtenidas", categoryService.findAllCategories()));
    }
}