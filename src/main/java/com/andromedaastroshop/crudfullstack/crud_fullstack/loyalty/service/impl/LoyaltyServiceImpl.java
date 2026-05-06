package com.andromedaastroshop.crudfullstack.crud_fullstack.loyalty.service.impl;

import com.andromedaastroshop.crudfullstack.crud_fullstack.loyalty.dto.LoyaltyAccountResponse;
import com.andromedaastroshop.crudfullstack.crud_fullstack.loyalty.dto.LoyaltyTransactionResponse;
import com.andromedaastroshop.crudfullstack.crud_fullstack.loyalty.model.LoyaltyAccount;
import com.andromedaastroshop.crudfullstack.crud_fullstack.loyalty.model.LoyaltyTransaction;
import com.andromedaastroshop.crudfullstack.crud_fullstack.loyalty.model.TransactionType;
import com.andromedaastroshop.crudfullstack.crud_fullstack.loyalty.repository.LoyaltyAccountRepository;
import com.andromedaastroshop.crudfullstack.crud_fullstack.loyalty.repository.LoyaltyTransactionRepository;
import com.andromedaastroshop.crudfullstack.crud_fullstack.loyalty.service.LoyaltyService;
import com.andromedaastroshop.crudfullstack.crud_fullstack.orders.model.Order;
import com.andromedaastroshop.crudfullstack.crud_fullstack.shared.exception.ResourceNotFoundException;
import com.andromedaastroshop.crudfullstack.crud_fullstack.user.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class LoyaltyServiceImpl implements LoyaltyService {

    private final LoyaltyAccountRepository accountRepository;
    private final LoyaltyTransactionRepository transactionRepository;

    public LoyaltyServiceImpl(LoyaltyAccountRepository accountRepository,
                               LoyaltyTransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    @Transactional
    public void earnPoints(Order order) {
        if (order.getUser() == null) return;

        int points = calculatePoints(order.getTotal());
        if (points <= 0) return;

        LoyaltyAccount account = accountRepository.findByUserId(order.getUser().getId())
                .orElseGet(() -> createAccount(order.getUser()));

        account.setPointsBalance(account.getPointsBalance() + points);
        account.setLifetimePoints(account.getLifetimePoints() + points);
        accountRepository.save(account);

        LoyaltyTransaction tx = new LoyaltyTransaction();
        tx.setUser(order.getUser());
        tx.setOrder(order);
        tx.setType(TransactionType.EARN);
        tx.setPoints(points);
        tx.setBalanceAfter(account.getPointsBalance());
        tx.setDescription("Puntos ganados por orden #" + order.getId());
        transactionRepository.save(tx);
    }

    @Override
    @Transactional
    public void reversePoints(Order order) {
        if (order.getUser() == null) return;

        transactionRepository.findByOrderIdAndType(order.getId(), TransactionType.EARN).ifPresent(earnTx -> {
            LoyaltyAccount account = accountRepository.findByUserId(order.getUser().getId()).orElseThrow();
            int deduction = earnTx.getPoints();
            int newBalance = Math.max(0, account.getPointsBalance() - deduction);
            account.setPointsBalance(newBalance);
            accountRepository.save(account);

            LoyaltyTransaction adjustTx = new LoyaltyTransaction();
            adjustTx.setUser(order.getUser());
            adjustTx.setOrder(order);
            adjustTx.setType(TransactionType.ADJUST);
            adjustTx.setPoints(-deduction);
            adjustTx.setBalanceAfter(newBalance);
            adjustTx.setDescription("Reembolso de puntos por orden #" + order.getId());
            transactionRepository.save(adjustTx);
        });
    }

    @Override
    public LoyaltyAccountResponse getAccount(User currentUser) {
        LoyaltyAccount account = accountRepository.findByUserId(currentUser.getId())
                .orElseGet(() -> createAccount(currentUser));
        return mapToAccountResponse(account);
    }

    @Override
    public List<LoyaltyTransactionResponse> getTransactions(User currentUser) {
        return transactionRepository.findByUserIdOrderByCreatedAtDesc(currentUser.getId()).stream()
                .map(this::mapToTransactionResponse)
                .toList();
    }

    @Override
    public LoyaltyAccountResponse getAccountByUserId(Long userId) {
        LoyaltyAccount account = accountRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta de loyalty no encontrada para el usuario: " + userId));
        return mapToAccountResponse(account);
    }

    private LoyaltyAccount createAccount(User user) {
        LoyaltyAccount account = new LoyaltyAccount();
        account.setUser(user);
        return accountRepository.save(account);
    }

    private int calculatePoints(BigDecimal total) {
        return total.divide(BigDecimal.valueOf(100), 0, RoundingMode.DOWN).intValue();
    }

    private LoyaltyAccountResponse mapToAccountResponse(LoyaltyAccount account) {
        return new LoyaltyAccountResponse(
                account.getId(),
                account.getUser().getId(),
                account.getUser().getName(),
                account.getPointsBalance(),
                account.getLifetimePoints(),
                account.getCreatedAt(),
                account.getUpdatedAt()
        );
    }

    private LoyaltyTransactionResponse mapToTransactionResponse(LoyaltyTransaction tx) {
        return new LoyaltyTransactionResponse(
                tx.getId(),
                tx.getType(),
                tx.getPoints(),
                tx.getBalanceAfter(),
                tx.getDescription(),
                tx.getOrder() != null ? tx.getOrder().getId() : null,
                tx.getCreatedAt()
        );
    }
}