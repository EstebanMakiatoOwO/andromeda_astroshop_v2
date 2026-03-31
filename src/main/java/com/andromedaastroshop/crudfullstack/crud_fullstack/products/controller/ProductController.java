package com.andromedaastroshop.crudfullstack.crud_fullstack.products.controller;

import com.andromedaastroshop.crudfullstack.crud_fullstack.products.dto.CreateProductRequest;
import com.andromedaastroshop.crudfullstack.crud_fullstack.products.dto.ProductResponse;
import com.andromedaastroshop.crudfullstack.crud_fullstack.products.service.ProductService;
import com.andromedaastroshop.crudfullstack.crud_fullstack.shared.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.time.LocalDateTime;

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
}
