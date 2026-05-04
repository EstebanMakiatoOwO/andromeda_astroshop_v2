package com.andromedaastroshop.crudfullstack.crud_fullstack.payments.service;

import com.andromedaastroshop.crudfullstack.crud_fullstack.payments.dto.MercadoPagoWebhookRequest;
import com.andromedaastroshop.crudfullstack.crud_fullstack.payments.dto.PaymentResponse;

import java.util.List;

public interface PaymentService {

    void processWebhook(MercadoPagoWebhookRequest request);

    PaymentResponse findById(Long id);

    List<PaymentResponse> findByOrderId(Long orderId);

    List<PaymentResponse> findAll();
}