package com.andromedaastroshop.crudfullstack.crud_fullstack.brands.controller;

import com.andromedaastroshop.crudfullstack.crud_fullstack.brands.dto.BrandResponse;
import com.andromedaastroshop.crudfullstack.crud_fullstack.brands.dto.CreateBrandRequest;
import com.andromedaastroshop.crudfullstack.crud_fullstack.brands.service.BrandService;
import com.andromedaastroshop.crudfullstack.crud_fullstack.shared.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/brands")
@PreAuthorize("hasRole('ADMIN')")
public class BrandController {

    private final BrandService brandService;

    public BrandController(BrandService brandService) {
        this.brandService = brandService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BrandResponse>>> findAll() {
        return ResponseEntity.ok(ApiResponse.success("Brands obtenidas", brandService.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BrandResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Brand obtenida", brandService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<BrandResponse>> create(@Valid @RequestBody CreateBrandRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Brand creada", brandService.create(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BrandResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody CreateBrandRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success("Brand actualizada", brandService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        brandService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Brand eliminada", null));
    }
}