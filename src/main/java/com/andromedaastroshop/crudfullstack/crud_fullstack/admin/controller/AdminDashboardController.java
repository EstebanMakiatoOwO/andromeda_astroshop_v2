package com.andromedaastroshop.crudfullstack.crud_fullstack.admin.controller;

import com.andromedaastroshop.crudfullstack.crud_fullstack.admin.dto.CategoryRevenueResponse;
import com.andromedaastroshop.crudfullstack.crud_fullstack.admin.dto.DashboardStatsResponse;
import com.andromedaastroshop.crudfullstack.crud_fullstack.admin.dto.LowStockResponse;
import com.andromedaastroshop.crudfullstack.crud_fullstack.admin.dto.SalesDataPoint;
import com.andromedaastroshop.crudfullstack.crud_fullstack.admin.service.AdminDashboardService;
import com.andromedaastroshop.crudfullstack.crud_fullstack.orders.dto.OrderResponse;
import com.andromedaastroshop.crudfullstack.crud_fullstack.shared.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminDashboardController {

    private final AdminDashboardService dashboardService;

    public AdminDashboardController(AdminDashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/dashboard/stats")
    public ResponseEntity<ApiResponse<DashboardStatsResponse>> getStats() {
        return ResponseEntity.ok(
                ApiResponse.success("Stats obtenidas", dashboardService.getStats())
        );
    }

    @GetMapping("/dashboard/sales")
    public ResponseEntity<ApiResponse<List<SalesDataPoint>>> getSales(
            @RequestParam(defaultValue = "30d") String period
    ) {
        return ResponseEntity.ok(
                ApiResponse.success("Ventas obtenidas", dashboardService.getSalesByPeriod(period))
        );
    }

    @GetMapping("/dashboard/top-categories")
    public ResponseEntity<ApiResponse<List<CategoryRevenueResponse>>> getTopCategories() {
        return ResponseEntity.ok(
                ApiResponse.success("Categorías obtenidas", dashboardService.getTopCategories())
        );
    }

    @GetMapping("/orders")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getRecentOrders(
            @RequestParam(defaultValue = "5") int size
    ) {
        return ResponseEntity.ok(
                ApiResponse.success("Órdenes obtenidas", dashboardService.getRecentOrders(size))
        );
    }

    @GetMapping("/products/low-stock")
    public ResponseEntity<ApiResponse<List<LowStockResponse>>> getLowStock() {
        return ResponseEntity.ok(
                ApiResponse.success("Productos con stock bajo", dashboardService.getLowStockProducts())
        );
    }
}