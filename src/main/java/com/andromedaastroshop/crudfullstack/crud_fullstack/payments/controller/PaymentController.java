package com.andromedaastroshop.crudfullstack.crud_fullstack.payments.controller;

import com.andromedaastroshop.crudfullstack.crud_fullstack.payments.dto.MercadoPagoWebhookRequest;
import com.andromedaastroshop.crudfullstack.crud_fullstack.payments.dto.PaymentResponse;
import com.andromedaastroshop.crudfullstack.crud_fullstack.payments.service.PaymentService;
import com.andromedaastroshop.crudfullstack.crud_fullstack.shared.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> webhook(@RequestBody MercadoPagoWebhookRequest request) {
        paymentService.processWebhook(request);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PaymentResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Pago encontrado", paymentService.findById(id)));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> findByOrderId(@PathVariable Long orderId) {
        return ResponseEntity.ok(ApiResponse.success("Pagos encontrados", paymentService.findByOrderId(orderId)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> findAll() {
        return ResponseEntity.ok(ApiResponse.success("Pagos encontrados", paymentService.findAll()));
    }
}