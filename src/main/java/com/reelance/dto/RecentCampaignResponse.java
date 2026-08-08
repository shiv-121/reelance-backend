package com.reelance.dto;

import com.reelance.entity.CampaignStatus;

import java.time.LocalDate;

public record RecentCampaignResponse(
        Long campaignId,
        String title,
        CampaignStatus status,
        Long budget,
        LocalDate deadline,
        long applicationCount,
        long pendingApplicationCount,
        long acceptedApplicationCount
) {}