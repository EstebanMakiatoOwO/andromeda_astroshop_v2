package com.andromedaastroshop.crudfullstack.crud_fullstack.reviews.service.impl;

import com.andromedaastroshop.crudfullstack.crud_fullstack.payments.model.PaymentStatus;
import com.andromedaastroshop.crudfullstack.crud_fullstack.products.model.Product;
import com.andromedaastroshop.crudfullstack.crud_fullstack.products.repository.ProductRepository;
import com.andromedaastroshop.crudfullstack.crud_fullstack.reviews.dto.CreateReviewRequest;
import com.andromedaastroshop.crudfullstack.crud_fullstack.reviews.dto.ReviewResponse;
import com.andromedaastroshop.crudfullstack.crud_fullstack.reviews.dto.UpdateReviewRequest;
import com.andromedaastroshop.crudfullstack.crud_fullstack.reviews.model.Review;
import com.andromedaastroshop.crudfullstack.crud_fullstack.reviews.repository.ReviewRepository;
import com.andromedaastroshop.crudfullstack.crud_fullstack.reviews.service.ReviewService;
import com.andromedaastroshop.crudfullstack.crud_fullstack.shared.exception.ProductNotPurchasedException;
import com.andromedaastroshop.crudfullstack.crud_fullstack.shared.exception.ResourceAlreadyExistsException;
import com.andromedaastroshop.crudfullstack.crud_fullstack.shared.exception.ResourceNotFoundException;
import com.andromedaastroshop.crudfullstack.crud_fullstack.user.model.Role;
import com.andromedaastroshop.crudfullstack.crud_fullstack.user.model.User;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;

    public ReviewServiceImpl(ReviewRepository reviewRepository, ProductRepository productRepository) {
        this.reviewRepository = reviewRepository;
        this.productRepository = productRepository;
    }

    @Override
    public ReviewResponse create(CreateReviewRequest request, User currentUser) {
        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + request.productId()));

        if (reviewRepository.existsByUserIdAndProductId(currentUser.getId(), product.getId())) {
            throw new ResourceAlreadyExistsException("Ya existe una reseña de este usuario para este producto");
        }

        if (!reviewRepository.hasUserPurchasedProduct(currentUser.getId(), product.getId(), PaymentStatus.APPROVED)) {
            throw new ProductNotPurchasedException("Solo podés reseñar productos que hayas comprado");
        }

        Review review = new Review();
        review.setUser(currentUser);
        review.setProduct(product);
        review.setRating(request.rating());
        review.setComment(request.comment());

        return mapToResponse(reviewRepository.save(review));
    }

    @Override
    public List<ReviewResponse> findByProduct(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Producto no encontrado con id: " + productId);
        }
        return reviewRepository.findByProductIdOrderByCreatedAtDesc(productId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<ReviewResponse> findByCurrentUser(User currentUser) {
        return reviewRepository.findByUserIdOrderByCreatedAtDesc(currentUser.getId()).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public ReviewResponse update(Long id, UpdateReviewRequest request, User currentUser) {
        Review review = reviewRepository.findByIdAndUserId(id, currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Reseña no encontrada o no te pertenece"));

        review.setRating(request.rating());
        review.setComment(request.comment());

        return mapToResponse(reviewRepository.save(review));
    }

    @Override
    public void delete(Long id, User currentUser) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reseña no encontrada con id: " + id));

        boolean isOwner = review.getUser().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;

        if (!isOwner && !isAdmin) {
            throw new AccessDeniedException("No tenés permisos para eliminar esta reseña");
        }

        reviewRepository.delete(review);
    }

    @Override
    public List<ReviewResponse> findUnreplied() {
        return reviewRepository.findByAdminReplyIsNullOrderByCreatedAtDesc().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public void markAsSeen(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Reseña no encontrada con id: " + reviewId));
        review.setSeenByAdmin(true);
        reviewRepository.save(review);
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public void markAllSeen() {
        reviewRepository.markAllAsSeen();
    }

    @Override
    public long countUnseen() {
        return reviewRepository.countBySeenByAdminFalse();
    }

    @Override
    public List<ReviewResponse> findAll() {
        return reviewRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public ReviewResponse adminReply(Long id, String reply) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reseña no encontrada con id: " + id));
        review.setAdminReply(reply);
        return mapToResponse(reviewRepository.save(review));
    }

    private ReviewResponse mapToResponse(Review review) {
        return new ReviewResponse(
                review.getId(),
                review.getUser().getId(),
                review.getUser().getName(),
                review.getProduct().getId(),
                review.getProduct().getName(),
                review.getRating(),
                review.getComment(),
                review.getAdminReply(),
                review.getCreatedAt(),
                review.getUpdatedAt()
        );
    }
}