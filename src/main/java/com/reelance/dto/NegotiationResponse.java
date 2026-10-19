package com.reelance.dto;

import com.reelance.entity.ApplicationStatus;
import com.reelance.entity.NegotiationParty;
import com.reelance.entity.NegotiationStatus;

public record NegotiationResponse(
        Long applicationId,
        Long campaignId,
        Long influencerId,
        Long brandId,
        Long originalExpectedPrice,
        Long currentOfferPrice,
        Long finalAgreedPrice,
        NegotiationStatus negotiationStatus,
        NegotiationParty nextResponseParty,
        ApplicationStatus applicationStatus
) {
}
