package com.andromedaastroshop.crudfullstack.crud_fullstack.admin.service;

import com.andromedaastroshop.crudfullstack.crud_fullstack.admin.dto.CategoryRevenueResponse;
import com.andromedaastroshop.crudfullstack.crud_fullstack.admin.dto.DashboardStatsResponse;
import com.andromedaastroshop.crudfullstack.crud_fullstack.admin.dto.LowStockResponse;
import com.andromedaastroshop.crudfullstack.crud_fullstack.admin.dto.SalesDataPoint;
import com.andromedaastroshop.crudfullstack.crud_fullstack.orders.dto.OrderResponse;

import java.util.List;

public interface AdminDashboardService {
    DashboardStatsResponse getStats();
    List<SalesDataPoint> getSalesByPeriod(String period);
    List<CategoryRevenueResponse> getTopCategories();
    List<OrderResponse> getRecentOrders(int size);
    List<LowStockResponse> getLowStockProducts();
}