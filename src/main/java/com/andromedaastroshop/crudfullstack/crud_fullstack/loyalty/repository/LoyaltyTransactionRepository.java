package com.andromedaastroshop.crudfullstack.crud_fullstack.loyalty.repository;

import com.andromedaastroshop.crudfullstack.crud_fullstack.loyalty.model.LoyaltyTransaction;
import com.andromedaastroshop.crudfullstack.crud_fullstack.loyalty.model.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoyaltyTransactionRepository extends JpaRepository<LoyaltyTransaction, Long> {
    List<LoyaltyTransaction> findByUserIdOrderByCreatedAtDesc(Long userId);
    Optional<LoyaltyTransaction> findByOrderIdAndType(Long orderId, TransactionType type);
}