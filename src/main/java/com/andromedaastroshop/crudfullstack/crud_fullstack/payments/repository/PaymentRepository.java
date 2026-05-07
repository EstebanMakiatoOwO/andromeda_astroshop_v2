package com.andromedaastroshop.crudfullstack.crud_fullstack.payments.repository;

import com.andromedaastroshop.crudfullstack.crud_fullstack.payments.model.Payment;
import com.andromedaastroshop.crudfullstack.crud_fullstack.payments.model.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByMpPaymentId(String mpPaymentId);

    List<Payment> findByOrderIdOrderByCreatedAtDesc(Long orderId);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.status = :status")
    BigDecimal sumAmountByStatus(@Param("status") PaymentStatus status);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.status = :status AND p.paidAt >= :from")
    BigDecimal sumAmountByStatusSince(@Param("status") PaymentStatus status, @Param("from") LocalDateTime from);

    @Query("SELECT FUNCTION('DATE', p.paidAt), SUM(p.amount) FROM Payment p " +
           "WHERE p.status = :status AND p.paidAt >= :from " +
           "GROUP BY FUNCTION('DATE', p.paidAt) ORDER BY FUNCTION('DATE', p.paidAt) ASC")
    List<Object[]> findDailySalesSince(@Param("status") PaymentStatus status,
                                       @Param("from") LocalDateTime from);

    @Query("SELECT c.name, SUM(oi.subtotal) FROM Payment pay " +
           "JOIN pay.order o JOIN o.items oi JOIN oi.product pr JOIN pr.categories c " +
           "WHERE pay.status = :status " +
           "GROUP BY c.id, c.name ORDER BY SUM(oi.subtotal) DESC")
    List<Object[]> findTopCategoriesByRevenue(@Param("status") PaymentStatus status);
}