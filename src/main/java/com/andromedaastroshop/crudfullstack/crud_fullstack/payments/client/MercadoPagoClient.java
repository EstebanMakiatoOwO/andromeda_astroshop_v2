package com.andromedaastroshop.crudfullstack.crud_fullstack.payments.client;

import com.andromedaastroshop.crudfullstack.crud_fullstack.orders.model.Order;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.preference.Preference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class MercadoPagoClient {

    @Value("${mercadopago.webhook-url}")
    private String webhookUrl;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    public String createPreference(Order order) {
        List<PreferenceItemRequest> items = order.getItems().stream()
                .map(item -> PreferenceItemRequest.builder()
                        .id(String.valueOf(item.getProduct().getId()))
                        .title(item.getProduct().getName())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .currencyId("ARS")
                        .build())
                .toList();

        PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                .success(frontendUrl + "/checkout/success")
                .failure(frontendUrl + "/checkout/failure")
                .pending(frontendUrl + "/checkout/pending")
                .build();

        PreferenceRequest request = PreferenceRequest.builder()
                .items(items)
                .externalReference(String.valueOf(order.getId()))
                .notificationUrl(webhookUrl)
                .backUrls(backUrls)
                .autoReturn("approved")
                .build();

        try {
            Preference preference = new PreferenceClient().create(request);
            return preference.getId();
        } catch (MPApiException | MPException e) {
            throw new RuntimeException("Error al crear preferencia en MercadoPago: " + e.getMessage(), e);
        }
    }

    public MercadoPagoPaymentData getPayment(Long paymentId) {
        try {
            com.mercadopago.resources.payment.Payment mp = new PaymentClient().get(paymentId);

            String payerEmail = mp.getPayer() != null ? mp.getPayer().getEmail() : null;
            Long merchantOrderId = mp.getOrder() != null ? mp.getOrder().getId() : null;
            LocalDateTime dateApproved = mp.getDateApproved() != null
                    ? mp.getDateApproved().toLocalDateTime() : null;

            return new MercadoPagoPaymentData(
                    mp.getId(),
                    mp.getStatus(),
                    mp.getExternalReference(),
                    merchantOrderId,
                    mp.getPaymentMethodId(),
                    mp.getTransactionAmount(),
                    mp.getCurrencyId(),
                    payerEmail,
                    dateApproved
            );
        } catch (MPApiException | MPException e) {
            throw new RuntimeException("Error al obtener pago de MercadoPago: " + e.getMessage(), e);
        }
    }
}