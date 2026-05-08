package com.andromedaastroshop.crudfullstack.crud_fullstack.admin.controller;

import com.andromedaastroshop.crudfullstack.crud_fullstack.admin.dto.CategoryRevenueResponse;
import com.andromedaastroshop.crudfullstack.crud_fullstack.admin.dto.DashboardStatsResponse;
import com.andromedaastroshop.crudfullstack.crud_fullstack.admin.dto.LowStockResponse;
import com.andromedaastroshop.crudfullstack.crud_fullstack.admin.dto.SalesDataPoint;
import com.andromedaastroshop.crudfullstack.crud_fullstack.admin.dto.*;
import com.andromedaastroshop.crudfullstack.crud_fullstack.admin.service.AdminDashboardService;
import org.springframework.data.domain.Page;
import com.andromedaastroshop.crudfullstack.crud_fullstack.orders.dto.OrderResponse;
import com.andromedaastroshop.crudfullstack.crud_fullstack.orders.dto.UpdateOrderStatusRequest;
import com.andromedaastroshop.crudfullstack.crud_fullstack.orders.service.OrderService;
import com.andromedaastroshop.crudfullstack.crud_fullstack.products.dto.ProductResponse;
import com.andromedaastroshop.crudfullstack.crud_fullstack.products.service.ProductService;
import com.andromedaastroshop.crudfullstack.crud_fullstack.reviews.dto.ReviewResponse;
import com.andromedaastroshop.crudfullstack.crud_fullstack.reviews.service.ReviewService;
import com.andromedaastroshop.crudfullstack.crud_fullstack.shared.response.ApiResponse;
import com.andromedaastroshop.crudfullstack.crud_fullstack.user.dto.UserRespose;
import com.andromedaastroshop.crudfullstack.crud_fullstack.user.model.User;
import com.andromedaastroshop.crudfullstack.crud_fullstack.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminDashboardController {

    private final AdminDashboardService dashboardService;
    private final UserService userService;
    private final OrderService orderService;
    private final ProductService productService;
    private final ReviewService reviewService;

    public AdminDashboardController(AdminDashboardService dashboardService,
                                    UserService userService,
                                    OrderService orderService,
                                    ProductService productService,
                                    ReviewService reviewService) {
        this.dashboardService = dashboardService;
        this.userService = userService;
        this.orderService = orderService;
        this.productService = productService;
        this.reviewService = reviewService;
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
    public ResponseEntity<ApiResponse<Page<OrderResponse>>> getOrders(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false)    String status,
            @RequestParam(required = false)    String q,
            @RequestParam(required = false)    String dateFrom,
            @RequestParam(required = false)    String dateTo
    ) {
        return ResponseEntity.ok(
                ApiResponse.success("Órdenes obtenidas", dashboardService.getOrdersPaginated(page, size, status, q, dateFrom, dateTo))
        );
    }

    @GetMapping("/orders/counts")
    public ResponseEntity<ApiResponse<OrderCountsResponse>> getOrderCounts() {
        return ResponseEntity.ok(
                ApiResponse.success("Conteos obtenidos", dashboardService.getOrderCounts())
        );
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<ApiResponse<OrderDetailResponse>> getOrderDetail(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success("Orden obtenida", dashboardService.getOrderDetail(id))
        );
    }

    @PatchMapping("/orders/{id}/status")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateOrderStatusRequest request
    ) {
        return ResponseEntity.ok(
                ApiResponse.success("Estado actualizado", orderService.updateStatus(id, request))
        );
    }

    @PatchMapping("/orders/{id}/cancel")
    public ResponseEntity<ApiResponse<OrderResponse>> cancelOrder(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success("Orden cancelada", orderService.adminCancel(id))
        );
    }

    @PatchMapping("/orders/{id}/notes")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderNotes(
            @PathVariable Long id,
            @RequestBody UpdateOrderNotesRequest request
    ) {
        return ResponseEntity.ok(
                ApiResponse.success("Nota actualizada", dashboardService.updateOrderNotes(id, request.notes()))
        );
    }

    @GetMapping("/products/low-stock")
    public ResponseEntity<ApiResponse<List<LowStockResponse>>> getLowStock() {
        return ResponseEntity.ok(
                ApiResponse.success("Productos con stock bajo", dashboardService.getLowStockProducts())
        );
    }

    @GetMapping("/users/search")
    public ResponseEntity<ApiResponse<List<UserRespose>>> searchUsers(@RequestParam String q) {
        return ResponseEntity.ok(
                ApiResponse.success("Usuarios encontrados", userService.search(q))
        );
    }

    @GetMapping("/orders/search")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> searchOrders(@RequestParam String q) {
        return ResponseEntity.ok(
                ApiResponse.success("Órdenes encontradas", orderService.search(q))
        );
    }

    @GetMapping("/products/search")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> searchProducts(@RequestParam String q) {
        return ResponseEntity.ok(
                ApiResponse.success("Productos encontrados", productService.findByName(q))
        );
    }

    @GetMapping("/notifications")
    public ResponseEntity<ApiResponse<NotificationResponse>> getNotifications() {
        List<ReviewResponse> reviews = reviewService.findUnreplied();
        long unseen = reviewService.countUnseen();
        return ResponseEntity.ok(
                ApiResponse.success("Notificaciones obtenidas", new NotificationResponse(unseen, reviews))
        );
    }

    @PatchMapping("/notifications/seen")
    public ResponseEntity<ApiResponse<Void>> markAllNotificationsSeen() {
        reviewService.markAllSeen();
        return ResponseEntity.ok(ApiResponse.success("Notificaciones marcadas como vistas", null));
    }

    @PatchMapping("/notifications/{reviewId}/seen")
    public ResponseEntity<ApiResponse<Void>> markNotificationSeen(@PathVariable Long reviewId) {
        reviewService.markAsSeen(reviewId);
        return ResponseEntity.ok(ApiResponse.success("Notificación descartada", null));
    }

    @GetMapping("/reviews")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getAllReviews() {
        return ResponseEntity.ok(
                ApiResponse.success("Reseñas obtenidas", reviewService.findAll())
        );
    }

    @DeleteMapping("/reviews/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteReview(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser
    ) {
        reviewService.delete(id, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Reseña eliminada", null));
    }

    @PatchMapping("/reviews/{id}/reply")
    public ResponseEntity<ApiResponse<ReviewResponse>> replyToReview(
            @PathVariable Long id,
            @Valid @RequestBody AdminReplyRequest request
    ) {
        return ResponseEntity.ok(
                ApiResponse.success("Respuesta guardada", reviewService.adminReply(id, request.reply()))
        );
    }
}