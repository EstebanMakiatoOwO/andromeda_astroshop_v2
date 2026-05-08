package com.andromedaastroshop.crudfullstack.crud_fullstack.orders.service;

import com.andromedaastroshop.crudfullstack.crud_fullstack.orders.dto.CreateOrderRequest;
import com.andromedaastroshop.crudfullstack.crud_fullstack.orders.dto.OrderResponse;
import com.andromedaastroshop.crudfullstack.crud_fullstack.orders.dto.UpdateOrderStatusRequest;
import com.andromedaastroshop.crudfullstack.crud_fullstack.orders.model.OrderStatus;

import java.util.List;

public interface OrderService {

    OrderResponse create(CreateOrderRequest request, Long userId);

    OrderResponse findById(Long id);

    List<OrderResponse> findByUserId(Long userId);

    List<OrderResponse> findAll();

    List<OrderResponse> findByStatus(OrderStatus status);

    OrderResponse updateStatus(Long id, UpdateOrderStatusRequest request);

    OrderResponse cancel(Long id, Long userId);

    OrderResponse adminCancel(Long id);

    void handlePaymentConfirmed(String mpPreferenceId);

    List<OrderResponse> search(String q);
}
