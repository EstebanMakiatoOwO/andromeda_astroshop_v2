package com.andromedaastroshop.crudfullstack.crud_fullstack.brands.controller;

import com.andromedaastroshop.crudfullstack.crud_fullstack.brands.dto.BrandResponse;
import com.andromedaastroshop.crudfullstack.crud_fullstack.brands.service.BrandService;
import com.andromedaastroshop.crudfullstack.crud_fullstack.shared.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/brands")
public class PublicBrandController {

    private final BrandService brandService;

    public PublicBrandController(BrandService brandService) {
        this.brandService = brandService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BrandResponse>>> findAll() {
        return ResponseEntity.ok(ApiResponse.success("Marcas obtenidas", brandService.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BrandResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Marca obtenida", brandService.findById(id)));
    }
}