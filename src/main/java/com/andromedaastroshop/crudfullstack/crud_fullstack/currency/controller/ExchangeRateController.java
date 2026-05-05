package com.andromedaastroshop.crudfullstack.crud_fullstack.currency.controller;

import com.andromedaastroshop.crudfullstack.crud_fullstack.currency.dto.ExchangeRateResponse;
import com.andromedaastroshop.crudfullstack.crud_fullstack.currency.dto.UpdateExchangeRateRequest;
import com.andromedaastroshop.crudfullstack.crud_fullstack.currency.service.ExchangeRateService;
import com.andromedaastroshop.crudfullstack.crud_fullstack.shared.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/currency")
public class ExchangeRateController {

    private final ExchangeRateService exchangeRateService;

    public ExchangeRateController(ExchangeRateService exchangeRateService) {
        this.exchangeRateService = exchangeRateService;
    }

    @GetMapping("/rate")
    public ResponseEntity<ApiResponse<ExchangeRateResponse>> getRate() {
        return ResponseEntity.ok(ApiResponse.success("Tasa de cambio actual", exchangeRateService.getMxnToUsd()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/rate")
    public ResponseEntity<ApiResponse<ExchangeRateResponse>> updateRate(
            @Valid @RequestBody UpdateExchangeRateRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success("Tasa de cambio actualizada", exchangeRateService.updateMxnToUsd(request)));
    }
}