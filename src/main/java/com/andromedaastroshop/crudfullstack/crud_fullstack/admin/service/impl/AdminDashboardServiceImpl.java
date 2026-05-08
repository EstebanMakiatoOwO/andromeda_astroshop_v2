package com.andromedaastroshop.crudfullstack.crud_fullstack.admin.service.impl;

import com.andromedaastroshop.crudfullstack.crud_fullstack.admin.dto.*;
import com.andromedaastroshop.crudfullstack.crud_fullstack.admin.service.AdminDashboardService;
import com.andromedaastroshop.crudfullstack.crud_fullstack.orders.dto.OrderItemResponse;
import com.andromedaastroshop.crudfullstack.crud_fullstack.orders.dto.OrderResponse;
import com.andromedaastroshop.crudfullstack.crud_fullstack.orders.model.Order;
import com.andromedaastroshop.crudfullstack.crud_fullstack.orders.model.OrderStatus;
import com.andromedaastroshop.crudfullstack.crud_fullstack.orders.repository.OrderRepository;
import com.andromedaastroshop.crudfullstack.crud_fullstack.payments.model.Payment;
import com.andromedaastroshop.crudfullstack.crud_fullstack.payments.model.PaymentStatus;
import com.andromedaastroshop.crudfullstack.crud_fullstack.payments.repository.PaymentRepository;
import com.andromedaastroshop.crudfullstack.crud_fullstack.products.repository.ProductRepository;
import com.andromedaastroshop.crudfullstack.crud_fullstack.shared.exception.ResourceNotFoundException;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
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

    @Override
    public Page<OrderResponse> getOrdersPaginated(int page, int size, String status, String q, String dateFrom, String dateTo) {
        Specification<Order> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (status != null && !status.isBlank()) {
                predicates.add(cb.equal(root.get("status"), OrderStatus.valueOf(status)));
            }

            if (q != null && !q.isBlank()) {
                String like = "%" + q.toLowerCase() + "%";
                List<Predicate> search = new ArrayList<>();
                search.add(cb.like(cb.lower(root.get("guestName")), like));
                search.add(cb.like(cb.lower(root.get("guestEmail")), like));
                try {
                    search.add(cb.equal(root.get("id"), Long.parseLong(q)));
                } catch (NumberFormatException ignored) {}
                predicates.add(cb.or(search.toArray(new Predicate[0])));
            }

            if (dateFrom != null && !dateFrom.isBlank()) {
                predicates.add(cb.greaterThanOrEqualTo(
                        root.get("createdAt"), LocalDate.parse(dateFrom).atStartOfDay()
                ));
            }

            if (dateTo != null && !dateTo.isBlank()) {
                predicates.add(cb.lessThanOrEqualTo(
                        root.get("createdAt"), LocalDate.parse(dateTo).atTime(LocalTime.MAX)
                ));
            }

            query.orderBy(cb.desc(root.get("createdAt")));
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return orderRepository
                .findAll(spec, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")))
                .map(this::mapToOrderResponse);
    }

    @Override
    public OrderCountsResponse getOrderCounts() {
        return new OrderCountsResponse(
                orderRepository.countByStatus(OrderStatus.PENDING),
                orderRepository.countByStatus(OrderStatus.PAID),
                orderRepository.countByStatus(OrderStatus.SHIPPED),
                orderRepository.countByStatus(OrderStatus.CANCELLED),
                orderRepository.countByStatus(OrderStatus.REFUNDED),
                orderRepository.count()
        );
    }

    @Override
    public OrderDetailResponse getOrderDetail(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada: " + id));

        PaymentDetailDto paymentDto = paymentRepository
                .findByOrderIdOrderByCreatedAtDesc(id)
                .stream()
                .filter(p -> p.getStatus() == PaymentStatus.APPROVED)
                .findFirst()
                .map(this::mapToPaymentDetail)
                .orElse(null);

        return new OrderDetailResponse(mapToOrderResponse(order), paymentDto);
    }

    @Override
    @Transactional
    public OrderResponse updateOrderNotes(Long id, String notes) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada: " + id));
        order.setNotes(notes);
        return mapToOrderResponse(orderRepository.save(order));
    }

    private PaymentDetailDto mapToPaymentDetail(Payment p) {
        return new PaymentDetailDto(
                p.getId(),
                p.getMpPaymentId(),
                p.getPaymentMethod(),
                p.getPayerEmail(),
                p.getAmount(),
                p.getCurrencyId(),
                p.getStatus(),
                p.getPaidAt()
        );
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
                order.getShippingStreet(),
                order.getShippingCity(),
                order.getShippingState(),
                order.getShippingZipCode(),
                order.getShippingCountry(),
                items,
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }
}