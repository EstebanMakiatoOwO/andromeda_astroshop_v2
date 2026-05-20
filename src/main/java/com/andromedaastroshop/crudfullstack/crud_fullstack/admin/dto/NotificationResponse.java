package com.andromedaastroshop.crudfullstack.crud_fullstack.admin.dto;

import com.andromedaastroshop.crudfullstack.crud_fullstack.reviews.dto.ReviewResponse;

import java.util.List;

public record NotificationResponse(
        long unrepliedReviews,
        List<ReviewResponse> reviews
) {}