package com.andromedaastroshop.crudfullstack.crud_fullstack.stock.service;

import com.andromedaastroshop.crudfullstack.crud_fullstack.stock.dto.CreateStockMovementRequest;
import com.andromedaastroshop.crudfullstack.crud_fullstack.stock.dto.StockMovementResponse;
import com.andromedaastroshop.crudfullstack.crud_fullstack.stock.model.MovementType;

import java.util.List;

public interface StockMovementService {

    StockMovementResponse create(CreateStockMovementRequest request, String userEmail);

    List<StockMovementResponse> findAll();

    List<StockMovementResponse> findByProduct(Long productId);

    List<StockMovementResponse> findByType(MovementType type);
}