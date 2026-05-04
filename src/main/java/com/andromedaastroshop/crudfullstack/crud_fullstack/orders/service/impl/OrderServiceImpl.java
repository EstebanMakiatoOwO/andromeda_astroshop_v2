package com.andromedaastroshop.crudfullstack.crud_fullstack.orders.service.impl;

import com.andromedaastroshop.crudfullstack.crud_fullstack.orders.dto.CreateOrderRequest;
import com.andromedaastroshop.crudfullstack.crud_fullstack.orders.dto.OrderItemResponse;
import com.andromedaastroshop.crudfullstack.crud_fullstack.orders.dto.OrderResponse;
import com.andromedaastroshop.crudfullstack.crud_fullstack.orders.dto.UpdateOrderStatusRequest;
import com.andromedaastroshop.crudfullstack.crud_fullstack.orders.model.Order;
import com.andromedaastroshop.crudfullstack.crud_fullstack.orders.model.OrderItem;
import com.andromedaastroshop.crudfullstack.crud_fullstack.orders.model.OrderStatus;
import com.andromedaastroshop.crudfullstack.crud_fullstack.orders.repository.OrderRepository;
import com.andromedaastroshop.crudfullstack.crud_fullstack.orders.service.OrderService;
import com.andromedaastroshop.crudfullstack.crud_fullstack.payments.client.MercadoPagoClient;
import com.andromedaastroshop.crudfullstack.crud_fullstack.payments.client.PreferenceData;
import com.andromedaastroshop.crudfullstack.crud_fullstack.products.model.Product;
import com.andromedaastroshop.crudfullstack.crud_fullstack.products.repository.ProductRepository;
import com.andromedaastroshop.crudfullstack.crud_fullstack.shared.exception.InsufficientStockException;
import com.andromedaastroshop.crudfullstack.crud_fullstack.shared.exception.OrderNotModifiableException;
import com.andromedaastroshop.crudfullstack.crud_fullstack.shared.exception.PaymentAlreadyProcessedException;
import com.andromedaastroshop.crudfullstack.crud_fullstack.shared.exception.ResourceNotFoundException;
import com.andromedaastroshop.crudfullstack.crud_fullstack.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final MercadoPagoClient mercadoPagoClient;

    public OrderServiceImpl(OrderRepository orderRepository,
                            ProductRepository productRepository,
                            UserRepository userRepository,
                            MercadoPagoClient mercadoPagoClient) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.mercadoPagoClient = mercadoPagoClient;
    }

    @Override
    @Transactional
    public OrderResponse create(CreateOrderRequest request, Long userId) {
        Order order = new Order();

        if (userId != null) {
            order.setUser(userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado")));
        } else {
            order.setGuestEmail(request.guestEmail());
            order.setGuestName(request.guestName());
        }

        List<OrderItem> items = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;

        for (var itemRequest : request.items()) {
            Product product = productRepository.findById(itemRequest.productId())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado: " + itemRequest.productId()));

            if (!product.getIsActive()) {
                throw new ResourceNotFoundException("Producto no disponible: " + product.getName());
            }

            boolean isCatalog = Boolean.TRUE.equals(product.getIsCatalog());

            if (!isCatalog) {
                try {
                    int updated = productRepository.decrementStock(product.getId(), itemRequest.quantity());
                    if (updated == 0) {
                        throw new InsufficientStockException(
                                "Stock insuficiente para \"" + product.getName() + "\". Disponible: " + product.getStock()
                        );
                    }
                } catch (InsufficientStockException e) {
                    throw e;
                } catch (Exception e) {
                    throw new InsufficientStockException(
                            "Stock insuficiente para \"" + product.getName() + "\". Intentá de nuevo."
                    );
                }
            }

            BigDecimal unitPrice = product.getPrice();
            BigDecimal itemSubtotal = unitPrice.multiply(BigDecimal.valueOf(itemRequest.quantity()));

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(product);
            item.setQuantity(itemRequest.quantity());
            item.setUnitPrice(unitPrice);
            item.setSubtotal(itemSubtotal);
            item.setIsCatalog(isCatalog);

            items.add(item);
            subtotal = subtotal.add(itemSubtotal);
        }

        order.setItems(items);
        order.setSubtotal(subtotal);
        order.setShippingCost(request.shippingCost());
        order.setTotal(subtotal.add(request.shippingCost()));
        order.setNotes(request.notes());

        Order savedOrder = orderRepository.save(order);
        PreferenceData preference = mercadoPagoClient.createPreference(savedOrder);
        savedOrder.setMpPreferenceId(preference.preferenceId());
        savedOrder.setCheckoutUrl(preference.checkoutUrl());
        return mapToOrderResponse(orderRepository.save(savedOrder));
    }

    @Override
    public OrderResponse findById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada: " + id));
        return mapToOrderResponse(order);
    }

    @Override
    public List<OrderResponse> findByUserId(Long userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream().map(this::mapToOrderResponse).toList();
    }

    @Override
    public List<OrderResponse> findAll() {
        return orderRepository.findAllByOrderByCreatedAtDesc()
                .stream().map(this::mapToOrderResponse).toList();
    }

    @Override
    public List<OrderResponse> findByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status)
                .stream().map(this::mapToOrderResponse).toList();
    }

    @Override
    @Transactional
    public OrderResponse updateStatus(Long id, UpdateOrderStatusRequest request) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada: " + id));

        if (order.getStatus() == OrderStatus.CANCELLED || order.getStatus() == OrderStatus.REFUNDED) {
            throw new OrderNotModifiableException("La orden ya está en estado " + order.getStatus() + " y no puede modificarse");
        }

        order.setStatus(request.status());
        return mapToOrderResponse(orderRepository.save(order));
    }

    @Override
    @Transactional
    public OrderResponse cancel(Long id, Long userId) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada: " + id));

        if (order.getUser() == null || !order.getUser().getId().equals(userId)) {
            throw new OrderNotModifiableException("No tenés permiso para cancelar esta orden");
        }

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new OrderNotModifiableException("Solo se pueden cancelar órdenes en estado PENDING");
        }

        order.getItems().forEach(item -> {
            if (!Boolean.TRUE.equals(item.getIsCatalog())) {
                productRepository.incrementStock(item.getProduct().getId(), item.getQuantity());
            }
        });

        order.setStatus(OrderStatus.CANCELLED);
        return mapToOrderResponse(orderRepository.save(order));
    }

    @Override
    @Transactional
    public void handlePaymentConfirmed(String mpPreferenceId) {
        Order order = orderRepository.findByMpPreferenceId(mpPreferenceId)
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada para preferenceId: " + mpPreferenceId));

        if (order.getStatus() == OrderStatus.PAID) {
            throw new PaymentAlreadyProcessedException("El pago ya fue procesado para la orden: " + order.getId());
        }

        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);
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
