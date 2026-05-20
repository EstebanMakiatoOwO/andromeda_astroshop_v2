package com.andromedaastroshop.crudfullstack.crud_fullstack.stock.controller;

import com.andromedaastroshop.crudfullstack.crud_fullstack.shared.response.ApiResponse;
import com.andromedaastroshop.crudfullstack.crud_fullstack.stock.dto.CreateStockMovementRequest;
import com.andromedaastroshop.crudfullstack.crud_fullstack.stock.dto.StockMovementResponse;
import com.andromedaastroshop.crudfullstack.crud_fullstack.stock.model.MovementType;
import com.andromedaastroshop.crudfullstack.crud_fullstack.stock.service.StockMovementService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/stock-movements")
@PreAuthorize("hasRole('ADMIN')")
public class StockMovementController {

    private final StockMovementService stockMovementService;

    public StockMovementController(StockMovementService stockMovementService) {
        this.stockMovementService = stockMovementService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<StockMovementResponse>> create(
            @Valid @RequestBody CreateStockMovementRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(ApiResponse.success("Movimiento de stock registrado",
                stockMovementService.create(request, userDetails.getUsername())));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<StockMovementResponse>>> findAll() {
        return ResponseEntity.ok(ApiResponse.success("Movimientos encontrados", stockMovementService.findAll()));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<ApiResponse<List<StockMovementResponse>>> findByProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(ApiResponse.success("Movimientos encontrados", stockMovementService.findByProduct(productId)));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<ApiResponse<List<StockMovementResponse>>> findByType(@PathVariable MovementType type) {
        return ResponseEntity.ok(ApiResponse.success("Movimientos encontrados", stockMovementService.findByType(type)));
    }
}