package com.andromedaastroshop.crudfullstack.crud_fullstack.orders.repository;

import com.andromedaastroshop.crudfullstack.crud_fullstack.orders.model.Order;
import com.andromedaastroshop.crudfullstack.crud_fullstack.orders.model.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Order> findAllByOrderByCreatedAtDesc();

    Page<Order> findAllByOrderByCreatedAtDesc(Pageable pageable);

    List<Order> findByStatus(OrderStatus status);

    long countByStatus(OrderStatus status);

    List<Order> findByUserIdAndStatus(Long id, OrderStatus status);

    Optional<Order> findByMpPreferenceId(String mPreferenceId);

    @Query("SELECT COALESCE(AVG(o.total), 0) FROM Order o WHERE o.status = :status")
    BigDecimal avgTotalByStatus(@Param("status") OrderStatus status);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.createdAt >= :from")
    long countSince(@Param("from") LocalDateTime from);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = :status AND o.createdAt >= :from")
    long countByStatusSince(@Param("status") OrderStatus status, @Param("from") LocalDateTime from);

    @Query("SELECT COALESCE(AVG(o.total), 0) FROM Order o WHERE o.status = :status AND o.createdAt >= :from")
    BigDecimal avgTotalByStatusSince(@Param("status") OrderStatus status, @Param("from") LocalDateTime from);

    @Query("SELECT o FROM Order o WHERE " +
           "LOWER(o.guestName) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(o.guestEmail) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "CAST(o.id AS string) LIKE CONCAT('%', :q, '%') " +
           "ORDER BY o.createdAt DESC")
    List<Order> searchByQuery(@Param("q") String q);
}
