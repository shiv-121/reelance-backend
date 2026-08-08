package com.reelance.repository;

import com.reelance.entity.ApplicationStatus;
import com.reelance.entity.CampaignApplication;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CampaignApplicationRepository extends JpaRepository<CampaignApplication, Long> {

    List<CampaignApplication> findByCampaign_Id(Long campaignId);

    List<CampaignApplication> findByInfluencer_Id(Long influencerId);

    boolean existsByCampaign_IdAndInfluencer_Id(
            Long campaignId,
            Long influencerId
    );

    long countByCampaign_Brand_Id(Long brandId);

    long countByCampaign_Brand_IdAndStatus(Long brandId, ApplicationStatus status);

    @Query("SELECT ca FROM CampaignApplication ca " +
           "JOIN FETCH ca.campaign c " +
           "JOIN FETCH ca.influencer i " +
           "WHERE c.brand.id = :brandId " +
           "ORDER BY ca.createdAt DESC")
    List<CampaignApplication> findRecentApplicationsForBrand(Long brandId, Pageable pageable);

    long countByCampaign_Id(Long campaignId);

    long countByCampaign_IdAndStatus(
            Long campaignId,
            ApplicationStatus status
    );
}

