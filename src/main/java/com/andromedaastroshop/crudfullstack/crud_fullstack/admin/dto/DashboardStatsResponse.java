package com.andromedaastroshop.crudfullstack.crud_fullstack.admin.dto;

import java.math.BigDecimal;

public record DashboardStatsResponse(
        BigDecimal totalSales,
        long totalOrders,
        BigDecimal avgTicket,
        double conversionRate
) {}