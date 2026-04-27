package com.andromedaastroshop.crudfullstack.crud_fullstack.stock.service.impl;

import com.andromedaastroshop.crudfullstack.crud_fullstack.products.model.Product;
import com.andromedaastroshop.crudfullstack.crud_fullstack.products.repository.ProductRepository;
import com.andromedaastroshop.crudfullstack.crud_fullstack.shared.exception.ResourceNotFoundException;
import com.andromedaastroshop.crudfullstack.crud_fullstack.stock.dto.CreateStockMovementRequest;
import com.andromedaastroshop.crudfullstack.crud_fullstack.stock.dto.StockMovementResponse;
import com.andromedaastroshop.crudfullstack.crud_fullstack.stock.model.MovementType;
import com.andromedaastroshop.crudfullstack.crud_fullstack.stock.model.StockMovement;
import com.andromedaastroshop.crudfullstack.crud_fullstack.stock.repository.StockMovementRepository;
import com.andromedaastroshop.crudfullstack.crud_fullstack.stock.service.StockMovementService;
import com.andromedaastroshop.crudfullstack.crud_fullstack.user.model.User;
import com.andromedaastroshop.crudfullstack.crud_fullstack.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StockMovementServiceImpl implements StockMovementService {

    private final StockMovementRepository stockMovementRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public StockMovementServiceImpl(StockMovementRepository stockMovementRepository,
                                    ProductRepository productRepository,
                                    UserRepository userRepository) {
        this.stockMovementRepository = stockMovementRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public StockMovementResponse create(CreateStockMovementRequest request, String userEmail) {
        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + request.productId()));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        int stockBefore = product.getStock();
        int stockAfter = calculateStockAfter(request.type(), stockBefore, request.quantity());

        product.setStock(stockAfter);
        productRepository.save(product);

        StockMovement movement = new StockMovement();
        movement.setProduct(product);
        movement.setUser(user);
        movement.setType(request.type());
        movement.setReason(request.reason());
        movement.setQuantity(request.quantity());
        movement.setUnitCost(request.unitCost());
        movement.setStockBefore(stockBefore);
        movement.setStockAfter(stockAfter);
        movement.setReferenceId(request.referenceId());
        movement.setReferenceType(request.referenceType());
        movement.setNotes(request.notes());

        return mapToResponse(stockMovementRepository.save(movement));
    }

    @Override
    public List<StockMovementResponse> findAll() {
        return stockMovementRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<StockMovementResponse> findByProduct(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product not found with id: " + productId);
        }
        return stockMovementRepository.findByProductIdOrderByCreatedAtDesc(productId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<StockMovementResponse> findByType(MovementType type) {
        return stockMovementRepository.findByTypeOrderByCreatedAtDesc(type).stream()
                .map(this::mapToResponse)
                .toList();
    }

    private int calculateStockAfter(MovementType type, int stockBefore, int quantity) {
        return switch (type) {
            case RESTOCK, RETURN, ADJUSTMENT_IN -> stockBefore + quantity;
            case SALE, ADJUSTMENT_OUT -> stockBefore - quantity;
        };
    }

    private StockMovementResponse mapToResponse(StockMovement movement) {
        return new StockMovementResponse(
                movement.getId(),
                movement.getProduct().getId(),
                movement.getProduct().getName(),
                movement.getUser().getId(),
                movement.getUser().getName(),
                movement.getType(),
                movement.getReason(),
                movement.getQuantity(),
                movement.getUnitCost(),
                movement.getStockBefore(),
                movement.getStockAfter(),
                movement.getReferenceId(),
                movement.getReferenceType(),
                movement.getNotes(),
                movement.getCreatedAt()
        );
    }
}
