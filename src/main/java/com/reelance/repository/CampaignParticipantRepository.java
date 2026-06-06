package com.reelance.repository;

import com.reelance.entity.CampaignParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CampaignParticipantRepository
        extends JpaRepository<CampaignParticipant, Long> {

    List<CampaignParticipant>
    findByCampaign_Id(Long campaignId);

    List<CampaignParticipant>
    findByInfluencer_Id(Long influencerId);

    boolean existsByCampaign_IdAndInfluencer_Id(
            Long campaignId,
            Long influencerId);

    long countByCampaign_Brand_Id(Long brandId);
}

