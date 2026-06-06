package com.reelance.service;

import com.reelance.dto.CampaignParticipantResponse;
import com.reelance.entity.CampaignParticipant;
import com.reelance.entity.User;
import com.reelance.repository.CampaignParticipantRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CampaignParticipantService {

    private final CampaignParticipantRepository repository;

    public CampaignParticipantService(
            CampaignParticipantRepository repository) {

        this.repository = repository;
    }

    // BRAND → PARTICIPANTS OF CAMPAIGN
    public List<CampaignParticipantResponse>
    getCampaignParticipants(Long campaignId) {

        return repository.findByCampaign_Id(campaignId)
                .stream()
                .map(this::map)
                .toList();
    }

    // INFLUENCER → MY ACTIVE CAMPAIGNS
    public List<CampaignParticipantResponse>
    getMyCampaigns(User influencer) {

        return repository.findByInfluencer_Id(
                        influencer.getId())
                .stream()
                .map(this::map)
                .toList();
    }

    private CampaignParticipantResponse map(
            CampaignParticipant participant) {

        return new CampaignParticipantResponse(
                participant.getId(),
                participant.getCampaign().getId(),
                participant.getCampaign().getTitle(),
                participant.getInfluencer().getUsername(),
                participant.getJoinedAt()
        );
    }
}

