package com.reelance.dto;

import com.reelance.entity.ApplicationStatus;
import com.reelance.entity.NegotiationParty;
import com.reelance.entity.NegotiationStatus;

public record CampaignApplicationResponse(
        Long applicationId,
        Long campaignId,
        String campaignTitle,
        String influencerUsername,
        String proposal,
        Long expectedPrice,
        Long currentOfferPrice,
        Long finalAgreedPrice,
        NegotiationStatus negotiationStatus,
        NegotiationParty nextResponseParty,
        ApplicationStatus status
) {
}

