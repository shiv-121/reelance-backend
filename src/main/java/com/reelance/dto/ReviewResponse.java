package com.reelance.dto;

import java.time.LocalDateTime;

public record ReviewResponse(
        int rating,
        String comment,
        String reviewer,
        LocalDateTime createdAt
) {}
