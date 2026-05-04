package com.andromedaastroshop.crudfullstack.crud_fullstack.payments.dto;

public record MercadoPagoWebhookRequest(
        String type,
        WebhookData data
) {
    public record WebhookData(String id) {}
}