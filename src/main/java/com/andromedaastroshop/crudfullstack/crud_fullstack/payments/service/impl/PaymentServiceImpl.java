package com.andromedaastroshop.crudfullstack.crud_fullstack.payments.service.impl;

import com.andromedaastroshop.crudfullstack.crud_fullstack.loyalty.service.LoyaltyService;
import com.andromedaastroshop.crudfullstack.crud_fullstack.orders.model.Order;
import com.andromedaastroshop.crudfullstack.crud_fullstack.orders.repository.OrderRepository;
import com.andromedaastroshop.crudfullstack.crud_fullstack.orders.service.OrderService;
import com.andromedaastroshop.crudfullstack.crud_fullstack.payments.client.MercadoPagoClient;
import com.andromedaastroshop.crudfullstack.crud_fullstack.payments.client.MercadoPagoPaymentData;
import com.andromedaastroshop.crudfullstack.crud_fullstack.payments.dto.MercadoPagoWebhookRequest;
import com.andromedaastroshop.crudfullstack.crud_fullstack.payments.dto.PaymentResponse;
import com.andromedaastroshop.crudfullstack.crud_fullstack.payments.model.Payment;
import com.andromedaastroshop.crudfullstack.crud_fullstack.payments.model.PaymentStatus;
import com.andromedaastroshop.crudfullstack.crud_fullstack.payments.repository.PaymentRepository;
import com.andromedaastroshop.crudfullstack.crud_fullstack.payments.service.PaymentService;
import com.andromedaastroshop.crudfullstack.crud_fullstack.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final OrderService orderService;
    private final MercadoPagoClient mercadoPagoClient;
    private final LoyaltyService loyaltyService;

    public PaymentServiceImpl(PaymentRepository paymentRepository,
                              OrderRepository orderRepository,
                              OrderService orderService,
                              MercadoPagoClient mercadoPagoClient,
                              LoyaltyService loyaltyService) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.orderService = orderService;
        this.mercadoPagoClient = mercadoPagoClient;
        this.loyaltyService = loyaltyService;
    }

    @Override
    @Transactional
    public void processWebhook(MercadoPagoWebhookRequest notification) {
        if (!"payment".equals(notification.type()) || notification.data() == null) {
            return;
        }

        Long mpPaymentId = Long.parseLong(notification.data().id());
        MercadoPagoPaymentData mpData = mercadoPagoClient.getPayment(mpPaymentId);
        PaymentStatus newStatus = mapStatus(mpData.status());

        Optional<Payment> existingOpt = paymentRepository.findByMpPaymentId(String.valueOf(mpPaymentId));
        if (existingOpt.isPresent()) {
            updateExistingPayment(existingOpt.get(), newStatus, mpData);
            return;
        }

        createPayment(mpPaymentId, mpData, newStatus);
    }

    private void updateExistingPayment(Payment payment, PaymentStatus newStatus, MercadoPagoPaymentData mpData) {
        if (payment.getStatus() == newStatus) {
            return;
        }
        payment.setStatus(newStatus);
        if (newStatus == PaymentStatus.APPROVED && mpData.dateApproved() != null) {
            payment.setPaidAt(mpData.dateApproved());
        }
        paymentRepository.save(payment);
        if (newStatus == PaymentStatus.APPROVED) {
            orderService.handlePaymentConfirmed(payment.getOrder().getMpPreferenceId());
            loyaltyService.earnPoints(payment.getOrder());
        } else if (newStatus == PaymentStatus.REFUNDED) {
            loyaltyService.reversePoints(payment.getOrder());
        }
    }

    private void createPayment(Long mpPaymentId, MercadoPagoPaymentData mpData, PaymentStatus status) {
        Long orderId = Long.parseLong(mpData.externalReference());
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada: " + orderId));

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setMpPaymentId(String.valueOf(mpPaymentId));
        payment.setMpPreferenceId(order.getMpPreferenceId());
        payment.setMpMerchantOrderId(mpData.merchantOrderId() != null ? String.valueOf(mpData.merchantOrderId()) : null);
        payment.setStatus(status);
        payment.setPaymentMethod(mpData.paymentMethodId());
        payment.setAmount(mpData.transactionAmount());
        payment.setCurrencyId(mpData.currencyId());
        payment.setPayerEmail(mpData.payerEmail());
        if (status == PaymentStatus.APPROVED && mpData.dateApproved() != null) {
            payment.setPaidAt(mpData.dateApproved());
        }

        paymentRepository.save(payment);

        if (status == PaymentStatus.APPROVED) {
            orderService.handlePaymentConfirmed(order.getMpPreferenceId());
            loyaltyService.earnPoints(order);
        }
    }

    @Override
    public PaymentResponse findById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pago no encontrado: " + id));
        return mapToResponse(payment);
    }

    @Override
    public List<PaymentResponse> findByOrderId(Long orderId) {
        return paymentRepository.findByOrderIdOrderByCreatedAtDesc(orderId)
                .stream().map(this::mapToResponse).toList();
    }

    @Override
    public List<PaymentResponse> findAll() {
        return paymentRepository.findAll()
                .stream().map(this::mapToResponse).toList();
    }

    private PaymentStatus mapStatus(String mpStatus) {
        return switch (mpStatus) {
            case "approved" -> PaymentStatus.APPROVED;
            case "in_process", "authorized" -> PaymentStatus.IN_PROCESS;
            case "rejected" -> PaymentStatus.REJECTED;
            case "cancelled" -> PaymentStatus.CANCELLED;
            case "refunded" -> PaymentStatus.REFUNDED;
            case "charged_back" -> PaymentStatus.CHARGED_BACK;
            default -> PaymentStatus.PENDING;
        };
    }

    private PaymentResponse mapToResponse(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getOrder().getId(),
                payment.getMpPaymentId(),
                payment.getMpPreferenceId(),
                payment.getMpMerchantOrderId(),
                payment.getStatus(),
                payment.getPaymentMethod(),
                payment.getAmount(),
                payment.getCurrencyId(),
                payment.getPayerEmail(),
                payment.getPaidAt(),
                payment.getCreatedAt(),
                payment.getUpdatedAt()
        );
    }
}