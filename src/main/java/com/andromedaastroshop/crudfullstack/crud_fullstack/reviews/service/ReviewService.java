package com.andromedaastroshop.crudfullstack.crud_fullstack.reviews.service;

import com.andromedaastroshop.crudfullstack.crud_fullstack.reviews.dto.CreateReviewRequest;
import com.andromedaastroshop.crudfullstack.crud_fullstack.reviews.dto.ReviewResponse;
import com.andromedaastroshop.crudfullstack.crud_fullstack.reviews.dto.UpdateReviewRequest;
import com.andromedaastroshop.crudfullstack.crud_fullstack.user.model.User;

import java.util.List;

public interface ReviewService {
    ReviewResponse create(CreateReviewRequest request, User currentUser);
    List<ReviewResponse> findByProduct(Long productId);
    List<ReviewResponse> findByCurrentUser(User currentUser);
    ReviewResponse update(Long id, UpdateReviewRequest request, User currentUser);
    void delete(Long id, User currentUser);
}