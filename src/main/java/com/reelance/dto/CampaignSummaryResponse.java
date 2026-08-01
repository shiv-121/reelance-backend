package com.reelance.dto;

import com.reelance.entity.CampaignStatus;

import java.time.LocalDate;

public record CampaignSummaryResponse(
        Long campaignId,
        String title,
        String description,
        Long budget,
        String niche,
        LocalDate deadline,
        CampaignStatus status
) {}

