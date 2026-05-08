package com.andromedaastroshop.crudfullstack.crud_fullstack.admin.dto;

import com.andromedaastroshop.crudfullstack.crud_fullstack.payments.model.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentDetailDto(
        Long id,
        String mpPaymentId,
        String paymentMethod,
        String payerEmail,
        BigDecimal amount,
        String currencyId,
        PaymentStatus status,
        LocalDateTime paidAt
) {}