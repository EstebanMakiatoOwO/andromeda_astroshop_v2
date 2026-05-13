package com.andromedaastroshop.crudfullstack.crud_fullstack.products.controller;

import com.andromedaastroshop.crudfullstack.crud_fullstack.products.dto.ProductResponse;
import com.andromedaastroshop.crudfullstack.crud_fullstack.products.service.ProductService;
import com.andromedaastroshop.crudfullstack.crud_fullstack.shared.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Producto encontrado", productService.findById(id)));
    }

    @GetMapping("/sku/{sku}")
    public ResponseEntity<ApiResponse<ProductResponse>> findBySku(@PathVariable String sku) {
        return ResponseEntity.ok(ApiResponse.success("Producto encontrado", productService.findBySku(sku)));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> findByName(
            @RequestParam(required = false, defaultValue = "") String name) {
        return ResponseEntity.ok(ApiResponse.success("Productos encontrados", productService.findByName(name)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponse>>> findAll() {
        return ResponseEntity.ok(ApiResponse.success("Productos encontrados", productService.findAllProducts()));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> findByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(ApiResponse.success("Productos encontrados", productService.findByCategory(categoryId)));
    }
}