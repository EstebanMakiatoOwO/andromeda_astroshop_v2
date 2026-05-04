package com.andromedaastroshop.crudfullstack.crud_fullstack.payments.client;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MercadoPagoPaymentData(
        Long id,
        String status,
        String externalReference,
        Long merchantOrderId,
        String paymentMethodId,
        BigDecimal transactionAmount,
        String currencyId,
        String payerEmail,
        LocalDateTime dateApproved
) {}