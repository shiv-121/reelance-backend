package com.reelance.dto;

import java.time.LocalDateTime;

public record CampaignParticipantResponse(
        Long participantId,
        Long campaignId,
        String campaignTitle,
        String influencerUsername,
        LocalDateTime joinedAt
) {}

