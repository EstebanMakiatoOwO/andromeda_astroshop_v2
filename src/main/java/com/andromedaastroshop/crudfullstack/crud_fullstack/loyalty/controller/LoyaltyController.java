package com.andromedaastroshop.crudfullstack.crud_fullstack.loyalty.controller;

import com.andromedaastroshop.crudfullstack.crud_fullstack.loyalty.dto.LoyaltyAccountResponse;
import com.andromedaastroshop.crudfullstack.crud_fullstack.loyalty.dto.LoyaltyTransactionResponse;
import com.andromedaastroshop.crudfullstack.crud_fullstack.loyalty.service.LoyaltyService;
import com.andromedaastroshop.crudfullstack.crud_fullstack.shared.response.ApiResponse;
import com.andromedaastroshop.crudfullstack.crud_fullstack.user.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/loyalty")
public class LoyaltyController {

    private final LoyaltyService loyaltyService;

    public LoyaltyController(LoyaltyService loyaltyService) {
        this.loyaltyService = loyaltyService;
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<LoyaltyAccountResponse>> getMyAccount(
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(
                ApiResponse.success("Cuenta de puntos encontrada", loyaltyService.getAccount(currentUser))
        );
    }

    @GetMapping("/my/transactions")
    public ResponseEntity<ApiResponse<List<LoyaltyTransactionResponse>>> getMyTransactions(
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(
                ApiResponse.success("Transacciones encontradas", loyaltyService.getTransactions(currentUser))
        );
    }

    @GetMapping("/users/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<LoyaltyAccountResponse>> getAccountByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(
                ApiResponse.success("Cuenta de puntos encontrada", loyaltyService.getAccountByUserId(userId))
        );
    }
}