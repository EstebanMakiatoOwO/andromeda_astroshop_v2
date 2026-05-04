package com.andromedaastroshop.crudfullstack.crud_fullstack.payments.client;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MercadoPagoPaymentData(
        String id,
        String status,
        String preferenceId,
        String merchantOrderId,
        String paymentMethodId,
        BigDecimal transactionAmount,
        String currencyId,
        String payerEmail,
        LocalDateTime dateApproved,
        String rawResponse
) {}