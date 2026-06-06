package com.reelance.repository;

import com.reelance.entity.CampaignApplication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CampaignApplicationRepository extends JpaRepository<CampaignApplication, Long> {

    List<CampaignApplication> findByCampaign_Id(Long campaignId);

    List<CampaignApplication> findByInfluencer_Id(Long influencerId);

    boolean existsByCampaign_IdAndInfluencer_Id(
            Long campaignId,
            Long influencerId
    );

    long countByCampaign_Brand_Id(Long brandId);
}

