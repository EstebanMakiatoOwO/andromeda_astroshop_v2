package com.andromedaastroshop.crudfullstack.crud_fullstack.reviews.controller;

import com.andromedaastroshop.crudfullstack.crud_fullstack.reviews.dto.CreateReviewRequest;
import com.andromedaastroshop.crudfullstack.crud_fullstack.reviews.dto.ReviewResponse;
import com.andromedaastroshop.crudfullstack.crud_fullstack.reviews.dto.UpdateReviewRequest;
import com.andromedaastroshop.crudfullstack.crud_fullstack.reviews.service.ReviewService;
import com.andromedaastroshop.crudfullstack.crud_fullstack.shared.response.ApiResponse;
import com.andromedaastroshop.crudfullstack.crud_fullstack.user.model.User;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ReviewResponse>> create(
            @Valid @RequestBody CreateReviewRequest request,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(
                ApiResponse.success("Reseña creada correctamente", reviewService.create(request, currentUser))
        );
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> findByProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(
                ApiResponse.success("Reseñas encontradas", reviewService.findByProduct(productId))
        );
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> findMy(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(
                ApiResponse.success("Reseñas encontradas", reviewService.findByCurrentUser(currentUser))
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ReviewResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateReviewRequest request,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(
                ApiResponse.success("Reseña actualizada correctamente", reviewService.update(id, request, currentUser))
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser
    ) {
        reviewService.delete(id, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Reseña eliminada correctamente", null));
    }
}