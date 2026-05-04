package com.andromedaastroshop.crudfullstack.crud_fullstack.payments.dto;

import com.andromedaastroshop.crudfullstack.crud_fullstack.payments.model.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentResponse(
        Long id,
        Long orderId,
        String mpPaymentId,
        String mpPreferenceId,
        String mpMerchantOrderId,
        PaymentStatus status,
        String paymentMethod,
        BigDecimal amount,
        String currencyId,
        String payerEmail,
        LocalDateTime paidAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}