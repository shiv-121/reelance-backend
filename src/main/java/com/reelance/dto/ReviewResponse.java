package com.reelance.dto;

import java.time.LocalDateTime;

public record ReviewResponse(
        Long reviewId,
        Long collaborationId,
        int rating,
        String comment,
        String reviewer,
        String reviewee,
        LocalDateTime createdAt
) {}