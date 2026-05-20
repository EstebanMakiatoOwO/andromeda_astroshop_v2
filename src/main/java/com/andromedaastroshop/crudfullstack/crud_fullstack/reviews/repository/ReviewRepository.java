package com.andromedaastroshop.crudfullstack.crud_fullstack.reviews.repository;

import com.andromedaastroshop.crudfullstack.crud_fullstack.payments.model.PaymentStatus;
import com.andromedaastroshop.crudfullstack.crud_fullstack.reviews.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByProductIdOrderByCreatedAtDesc(Long productId);

    List<Review> findByUserIdOrderByCreatedAtDesc(Long userId);

    boolean existsByUserIdAndProductId(Long userId, Long productId);

    Optional<Review> findByIdAndUserId(Long id, Long userId);

    long countBySeenByAdminFalse();

    List<Review> findByAdminReplyIsNullOrderByCreatedAtDesc();

    List<Review> findBySeenByAdminFalse();

    @Modifying
    @Query("UPDATE Review r SET r.seenByAdmin = true WHERE r.seenByAdmin = false")
    void markAllAsSeen();

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END " +
           "FROM Payment p JOIN p.order o JOIN o.items i " +
           "WHERE o.user.id = :userId AND i.product.id = :productId AND p.status = :status")
    boolean hasUserPurchasedProduct(@Param("userId") Long userId,
                                    @Param("productId") Long productId,
                                    @Param("status") PaymentStatus status);
}