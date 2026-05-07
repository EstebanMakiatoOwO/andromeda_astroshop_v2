package com.andromedaastroshop.crudfullstack.crud_fullstack.admin.service.impl;

import com.andromedaastroshop.crudfullstack.crud_fullstack.admin.dto.CategoryRevenueResponse;
import com.andromedaastroshop.crudfullstack.crud_fullstack.admin.dto.DashboardStatsResponse;
import com.andromedaastroshop.crudfullstack.crud_fullstack.admin.dto.LowStockResponse;
import com.andromedaastroshop.crudfullstack.crud_fullstack.admin.dto.SalesDataPoint;
import com.andromedaastroshop.crudfullstack.crud_fullstack.admin.service.AdminDashboardService;
import com.andromedaastroshop.crudfullstack.crud_fullstack.orders.dto.OrderItemResponse;
import com.andromedaastroshop.crudfullstack.crud_fullstack.orders.dto.OrderResponse;
import com.andromedaastroshop.crudfullstack.crud_fullstack.orders.model.Order;
import com.andromedaastroshop.crudfullstack.crud_fullstack.orders.model.OrderStatus;
import com.andromedaastroshop.crudfullstack.crud_fullstack.orders.repository.OrderRepository;
import com.andromedaastroshop.crudfullstack.crud_fullstack.payments.model.PaymentStatus;
import com.andromedaastroshop.crudfullstack.crud_fullstack.payments.repository.PaymentRepository;
import com.andromedaastroshop.crudfullstack.crud_fullstack.products.repository.ProductRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class AdminDashboardServiceImpl implements AdminDashboardService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public AdminDashboardServiceImpl(PaymentRepository paymentRepository,
                                     OrderRepository orderRepository,
                                     ProductRepository productRepository) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    @Override
    public DashboardStatsResponse getStats() {
        LocalDateTime startOfToday = LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT);

        BigDecimal totalSales = paymentRepository.sumAmountByStatusSince(PaymentStatus.APPROVED, startOfToday);
        long totalOrders = orderRepository.countSince(startOfToday);
        BigDecimal avgTicket = orderRepository.avgTotalByStatusSince(OrderStatus.PAID, startOfToday);
        long paidOrders = orderRepository.countByStatusSince(OrderStatus.PAID, startOfToday);
        double conversionRate = totalOrders > 0
                ? BigDecimal.valueOf(paidOrders * 100.0 / totalOrders)
                        .setScale(1, RoundingMode.HALF_UP).doubleValue()
                : 0.0;

        return new DashboardStatsResponse(totalSales, totalOrders, avgTicket, conversionRate);
    }

    @Override
    public List<SalesDataPoint> getSalesByPeriod(String period) {
        return switch (period) {
            case "1h" -> {
                LocalDateTime from = LocalDateTime.now().minusHours(1);
                yield paymentRepository.findMinutelySalesSince(PaymentStatus.APPROVED, from).stream()
                        .map(row -> new SalesDataPoint(
                                String.format("%02d:%02d",
                                        ((Number) row[0]).intValue(),
                                        ((Number) row[1]).intValue()),
                                (BigDecimal) row[2]
                        ))
                        .toList();
            }
            case "1d" -> {
                LocalDateTime from = LocalDateTime.now().minusDays(1);
                yield paymentRepository.findHourlySalesSince(PaymentStatus.APPROVED, from).stream()
                        .map(row -> new SalesDataPoint(
                                String.format("%02d:00", ((Number) row[0]).intValue()),
                                (BigDecimal) row[1]
                        ))
                        .toList();
            }
            default -> {
                int days = switch (period) {
                    case "7d"  -> 7;
                    case "90d" -> 90;
                    default    -> 30;
                };
                LocalDateTime from = LocalDateTime.now().minusDays(days);
                yield paymentRepository.findDailySalesSince(PaymentStatus.APPROVED, from).stream()
                        .map(row -> new SalesDataPoint(
                                ((java.sql.Date) row[0]).toLocalDate().toString(),
                                (BigDecimal) row[1]
                        ))
                        .toList();
            }
        };
    }

    @Override
    public List<CategoryRevenueResponse> getTopCategories() {
        List<Object[]> rows = paymentRepository.findTopCategoriesByRevenue(PaymentStatus.APPROVED);

        BigDecimal grandTotal = rows.stream()
                .map(row -> (BigDecimal) row[1])
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return rows.stream()
                .map(row -> {
                    String name = (String) row[0];
                    BigDecimal amount = (BigDecimal) row[1];
                    double percent = grandTotal.compareTo(BigDecimal.ZERO) > 0
                            ? amount.multiply(BigDecimal.valueOf(100))
                                    .divide(grandTotal, 1, RoundingMode.HALF_UP)
                                    .doubleValue()
                            : 0.0;
                    return new CategoryRevenueResponse(name, amount, percent);
                })
                .toList();
    }

    @Override
    public List<OrderResponse> getRecentOrders(int size) {
        return orderRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(0, size))
                .stream()
                .map(this::mapToOrderResponse)
                .toList();
    }

    @Override
    public List<LowStockResponse> getLowStockProducts() {
        return productRepository.findLowStockProducts().stream()
                .map(p -> new LowStockResponse(
                        p.getId(),
                        p.getName(),
                        p.getSku(),
                        p.getStock(),
                        p.getStockAlertThreshold()
                ))
                .toList();
    }

    private OrderResponse mapToOrderResponse(Order order) {
        List<OrderItemResponse> items = order.getItems().stream()
                .map(item -> new OrderItemResponse(
                        item.getId(),
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getSubtotal(),
                        item.getIsCatalog()
                ))
                .toList();

        return new OrderResponse(
                order.getId(),
                order.getUser() != null ? order.getUser().getId() : null,
                order.getGuestEmail(),
                order.getGuestName(),
                order.getStatus(),
                order.getSubtotal(),
                order.getShippingCost(),
                order.getTotal(),
                order.getMpPreferenceId(),
                order.getCheckoutUrl(),
                order.getNotes(),
                items,
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }
}