package com.andromedaastroshop.crudfullstack.crud_fullstack.stock.repository;

import com.andromedaastroshop.crudfullstack.crud_fullstack.stock.model.MovementType;
import com.andromedaastroshop.crudfullstack.crud_fullstack.stock.model.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {

    List<StockMovement> findByProductIdOrderByCreatedAtDesc(Long productId);

    List<StockMovement> findByTypeOrderByCreatedAtDesc(MovementType type);

    List<StockMovement> findAllByOrderByCreatedAtDesc();
}