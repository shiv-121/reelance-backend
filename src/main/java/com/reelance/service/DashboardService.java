package com.reelance.service;

import com.reelance.dto.BrandAnalyticsResponse;
import com.reelance.entity.CampaignStatus;
import com.reelance.entity.User;
import com.reelance.repository.CampaignApplicationRepository;
import com.reelance.repository.CampaignParticipantRepository;
import com.reelance.repository.CampaignRepository;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    private final CampaignRepository campaignRepository;
    private final CampaignApplicationRepository applicationRepository;
    private final CampaignParticipantRepository participantRepository;

    public DashboardService(
            CampaignRepository campaignRepository,
            CampaignApplicationRepository applicationRepository,
            CampaignParticipantRepository participantRepository) {

        this.campaignRepository = campaignRepository;
        this.applicationRepository = applicationRepository;
        this.participantRepository = participantRepository;
    }

    public BrandAnalyticsResponse getBrandAnalytics(
            User brand) {

        long campaigns =
                campaignRepository.countByBrand_Id(
                        brand.getId());

        long applications =
                applicationRepository
                        .countByCampaign_Brand_Id(
                                brand.getId());

        long participants =
                participantRepository
                        .countByCampaign_Brand_Id(
                                brand.getId());

        long completedCampaigns =
                campaignRepository
                        .countByBrand_IdAndStatus(
                                brand.getId(),
                                CampaignStatus.COMPLETED);

        return new BrandAnalyticsResponse(
                campaigns,
                applications,
                participants,
                completedCampaigns
        );
    }
}

