package com.andromedaastroshop.crudfullstack.crud_fullstack.admin.service;

import com.andromedaastroshop.crudfullstack.crud_fullstack.admin.dto.*;
import com.andromedaastroshop.crudfullstack.crud_fullstack.orders.dto.OrderResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface AdminDashboardService {
    DashboardStatsResponse getStats();
    List<SalesDataPoint> getSalesByPeriod(String period);
    List<CategoryRevenueResponse> getTopCategories();
    List<OrderResponse> getRecentOrders(int size);
    List<LowStockResponse> getLowStockProducts();

    Page<OrderResponse> getOrdersPaginated(int page, int size, String status, String q);
    OrderCountsResponse getOrderCounts();
    OrderDetailResponse getOrderDetail(Long id);
    OrderResponse updateOrderNotes(Long id, String notes);
}