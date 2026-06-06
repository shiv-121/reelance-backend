package com.reelance.dto;

public record BrandAnalyticsResponse(
        long campaigns,
        long applications,
        long participants,
        long completedCampaigns
) {}

