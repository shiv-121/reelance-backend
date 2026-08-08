package com.reelance.dto;

import java.util.List;

public record BrandDashboardResponse(
        long totalCampaigns,
        long activeCampaigns,
        long completedCampaigns,
        long closedCampaigns,

        long totalApplications,
        long pendingApplications,
        long acceptedApplications,
        long rejectedApplications,

        long totalBudget,

        double acceptanceRate,
        double completionRate,

        List<RecentCampaignResponse> recentCampaigns,
        List<RecentApplicationResponse> recentApplications
) {}