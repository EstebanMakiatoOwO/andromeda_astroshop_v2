package com.andromedaastroshop.crudfullstack.crud_fullstack.products.controller;

import com.andromedaastroshop.crudfullstack.crud_fullstack.products.dto.CreateProductRequest;
import com.andromedaastroshop.crudfullstack.crud_fullstack.products.dto.ProductResponse;
import com.andromedaastroshop.crudfullstack.crud_fullstack.products.dto.UpdateProductRequest;
import com.andromedaastroshop.crudfullstack.crud_fullstack.products.service.ProductService;
import com.andromedaastroshop.crudfullstack.crud_fullstack.shared.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping(value = "/create", consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<ProductResponse>> create(
            @Valid @RequestPart("request") CreateProductRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {

        ProductResponse newProduct = productService.create(request, image);

        return ResponseEntity.ok(
                ApiResponse.success("Producto creado correctamente", newProduct)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> findById(@PathVariable Long id) {
        ProductResponse product = productService.findById(id);

        return ResponseEntity.ok(
                ApiResponse.success("Producto encontrado", product)
        );
    }

    @GetMapping("/sku/{sku}")
    public ResponseEntity<ApiResponse<ProductResponse>> findBySku(@PathVariable String sku) {
        return ResponseEntity.ok(
                ApiResponse.success("Producto encontrado", productService.findBySku(sku))
        );
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> findByName(@RequestParam(required = false, defaultValue = "") String name) {
        return ResponseEntity.ok(
                ApiResponse.success("Producto encontrado", productService.findByName(name))
        );
    }

    @GetMapping()
    public ResponseEntity<ApiResponse<List<ProductResponse>>> findAllProducts(){
        return ResponseEntity.ok(
                ApiResponse.success("Productos encontrados", productService.findAllProducts())
        );
    }

    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<ProductResponse>> update(
            @PathVariable Long id,
            @Valid @RequestPart("request") UpdateProductRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        ProductResponse updatedProduct = productService.updateById(id, request, image);
        return ResponseEntity.ok(
                ApiResponse.success("Producto actualizado correctamente", updatedProduct)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleted(@PathVariable Long id) {
        productService.deleteById(id);
        return ResponseEntity.ok(
                ApiResponse.success("Producto eliminado correctamente", null)
        );
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> findByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(
                ApiResponse.success("Productos encontrados", productService.findByCategory(categoryId))
        );
    }

    @PatchMapping("/{id}/categories")
    public ResponseEntity<ApiResponse<ProductResponse>> updateCategories(
            @PathVariable Long id,
            @RequestBody List<Long> categoryIds
    ) {
        return ResponseEntity.ok(
                ApiResponse.success("Categorías actualizadas", productService.updateCategories(id, categoryIds))
        );
    }
}
