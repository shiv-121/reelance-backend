package com.reelance.dto;

import java.util.List;

public record CampaignWorkspaceResponse(
        List<CampaignSummaryResponse> campaigns,
        int currentPage,
        int pageSize,
        long totalElements,
        int totalPages,
        long totalCampaigns,
        long openCampaigns,
        long closedCampaigns,
        long completedCampaigns,
        long totalBudget
) {}

