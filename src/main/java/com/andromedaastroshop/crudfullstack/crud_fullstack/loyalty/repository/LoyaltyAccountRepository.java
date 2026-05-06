package com.andromedaastroshop.crudfullstack.crud_fullstack.loyalty.repository;

import com.andromedaastroshop.crudfullstack.crud_fullstack.loyalty.model.LoyaltyAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LoyaltyAccountRepository extends JpaRepository<LoyaltyAccount, Long> {
    Optional<LoyaltyAccount> findByUserId(Long userId);
}