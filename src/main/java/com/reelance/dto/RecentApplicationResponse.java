package com.reelance.dto;

import com.reelance.entity.ApplicationStatus;

import java.time.LocalDateTime;

public record RecentApplicationResponse(
        Long applicationId,
        Long campaignId,
        Long creatorId,
        String campaignTitle,
        String creatorName,
        ApplicationStatus status,
        LocalDateTime appliedOn
) {}

