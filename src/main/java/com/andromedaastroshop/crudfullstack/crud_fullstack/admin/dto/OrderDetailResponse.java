package com.andromedaastroshop.crudfullstack.crud_fullstack.admin.dto;

import com.andromedaastroshop.crudfullstack.crud_fullstack.orders.dto.OrderResponse;

public record OrderDetailResponse(
        OrderResponse order,
        PaymentDetailDto payment
) {}