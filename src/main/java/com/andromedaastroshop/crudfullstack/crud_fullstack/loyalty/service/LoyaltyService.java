package com.andromedaastroshop.crudfullstack.crud_fullstack.loyalty.service;

import com.andromedaastroshop.crudfullstack.crud_fullstack.loyalty.dto.LoyaltyAccountResponse;
import com.andromedaastroshop.crudfullstack.crud_fullstack.loyalty.dto.LoyaltyTransactionResponse;
import com.andromedaastroshop.crudfullstack.crud_fullstack.orders.model.Order;
import com.andromedaastroshop.crudfullstack.crud_fullstack.user.model.User;

import java.util.List;

public interface LoyaltyService {
    void earnPoints(Order order);
    void reversePoints(Order order);
    LoyaltyAccountResponse getAccount(User currentUser);
    List<LoyaltyTransactionResponse> getTransactions(User currentUser);
    LoyaltyAccountResponse getAccountByUserId(Long userId);
}