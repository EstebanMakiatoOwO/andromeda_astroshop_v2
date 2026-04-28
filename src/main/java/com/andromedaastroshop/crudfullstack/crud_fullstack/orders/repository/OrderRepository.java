package com.andromedaastroshop.crudfullstack.crud_fullstack.orders.repository;

import com.andromedaastroshop.crudfullstack.crud_fullstack.orders.model.Order;
import com.andromedaastroshop.crudfullstack.crud_fullstack.orders.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Order> findAllByOrderByCreatedAtDesc();

    List<Order> findByStatus(OrderStatus status);

    List<Order> findByUserIdAndStatus(Long id, OrderStatus status);

    Optional<Order> findByMpPreferenceId (String mPreferenceId);
}
