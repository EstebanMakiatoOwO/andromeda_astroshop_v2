package com.andromedaastroshop.crudfullstack.crud_fullstack.orders.controller;

import com.andromedaastroshop.crudfullstack.crud_fullstack.orders.dto.CreateOrderRequest;
import com.andromedaastroshop.crudfullstack.crud_fullstack.orders.dto.OrderResponse;
import com.andromedaastroshop.crudfullstack.crud_fullstack.orders.dto.UpdateOrderStatusRequest;
import com.andromedaastroshop.crudfullstack.crud_fullstack.orders.model.OrderStatus;
import com.andromedaastroshop.crudfullstack.crud_fullstack.orders.service.OrderService;
import com.andromedaastroshop.crudfullstack.crud_fullstack.shared.response.ApiResponse;
import com.andromedaastroshop.crudfullstack.crud_fullstack.user.model.User;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> create(
            @Valid @RequestBody CreateOrderRequest request,
            Authentication authentication
    ) {
        Long userId = null;
        if (authentication != null && authentication.getPrincipal() instanceof User user) {
            userId = user.getId();
        }
        OrderResponse response = orderService.create(request, userId);
        return ResponseEntity.ok(ApiResponse.success("Orden creada correctamente", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Orden encontrada", orderService.findById(id)));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> findMyOrders(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(ApiResponse.success("Órdenes encontradas", orderService.findByUserId(user.getId())));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<OrderResponse>> cancel(
            @PathVariable Long id,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(ApiResponse.success("Orden cancelada", orderService.cancel(id, user.getId())));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderResponse>>> findAll() {
        return ResponseEntity.ok(ApiResponse.success("Órdenes encontradas", orderService.findAll()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> findByStatus(@PathVariable OrderStatus status) {
        return ResponseEntity.ok(ApiResponse.success("Órdenes encontradas", orderService.findByStatus(status)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<OrderResponse>> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateOrderStatusRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success("Estado actualizado", orderService.updateStatus(id, request)));
    }

    @PostMapping("/webhook/payment")
    public ResponseEntity<Void> handlePaymentWebhook(@RequestParam String preferenceId) {
        orderService.handlePaymentConfirmed(preferenceId);
        return ResponseEntity.ok().build();
    }
}