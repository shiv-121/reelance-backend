package com.reelance.dto;

import java.util.List;

public record BrandDashboardResponse(
        long activeCampaigns,
        long pendingApplications,
        long acceptedApplications,
        long totalBudget,
        List<RecentCampaignResponse> recentCampaigns,
        List<RecentApplicationResponse> recentApplications
) {}

