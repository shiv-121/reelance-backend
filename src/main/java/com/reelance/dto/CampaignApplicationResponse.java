package com.reelance.dto;

import com.reelance.entity.ApplicationStatus;

public record CampaignApplicationResponse(
        Long applicationId,
        Long campaignId,
        String campaignTitle,
        String influencerUsername,
        String proposal,
        Long expectedPrice,
        ApplicationStatus status
) {
}

