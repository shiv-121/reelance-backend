package com.reelance.service;

import com.reelance.dto.BrandDashboardResponse;
import com.reelance.dto.RecentApplicationResponse;
import com.reelance.dto.RecentCampaignResponse;
import com.reelance.entity.ApplicationStatus;
import com.reelance.entity.Campaign;
import com.reelance.entity.CampaignApplication;
import com.reelance.entity.CampaignStatus;
import com.reelance.entity.User;
import com.reelance.repository.CampaignApplicationRepository;
import com.reelance.repository.CampaignRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BrandDashboardService {

    private final CampaignRepository campaignRepository;
    private final CampaignApplicationRepository applicationRepository;

    public BrandDashboardService(
            CampaignRepository campaignRepository,
            CampaignApplicationRepository applicationRepository) {

        this.campaignRepository = campaignRepository;
        this.applicationRepository = applicationRepository;
    }

    public BrandDashboardResponse getBrandDashboard(User brand) {

        Long brandId = brand.getId();

        /*
         * ============================
         * CAMPAIGN METRICS
         * ============================
         */

        long totalCampaigns =
                campaignRepository.countByBrand_Id(brandId);

        long activeCampaigns =
                campaignRepository.countByBrand_IdAndStatus(
                        brandId,
                        CampaignStatus.OPEN
                );

        long completedCampaigns =
                campaignRepository.countByBrand_IdAndStatus(
                        brandId,
                        CampaignStatus.COMPLETED
                );

        long closedCampaigns =
                campaignRepository.countByBrand_IdAndStatus(
                        brandId,
                        CampaignStatus.CLOSED
                );

        /*
         * ============================
         * APPLICATION METRICS
         * ============================
         */

        long totalApplications =
                applicationRepository.countByCampaign_Brand_Id(
                        brandId
                );

        long pendingApplications =
                applicationRepository.countByCampaign_Brand_IdAndStatus(
                        brandId,
                        ApplicationStatus.PENDING
                );

        long acceptedApplications =
                applicationRepository.countByCampaign_Brand_IdAndStatus(
                        brandId,
                        ApplicationStatus.ACCEPTED
                );

        long rejectedApplications =
                applicationRepository.countByCampaign_Brand_IdAndStatus(
                        brandId,
                        ApplicationStatus.REJECTED
                );

        /*
         * ============================
         * BUDGET
         * ============================
         */

        long totalBudget =
                campaignRepository.sumBudgetByBrandId(brandId);

        /*
         * ============================
         * RATES
         * ============================
         */

        double acceptanceRate =
                totalApplications == 0
                        ? 0
                        : ((double) acceptedApplications
                           / totalApplications) * 100;

        double completionRate =
                totalCampaigns == 0
                        ? 0
                        : ((double) completedCampaigns
                           / totalCampaigns) * 100;

        /*
         * ============================
         * RECENT CAMPAIGNS
         * ============================
         */

        List<RecentCampaignResponse> recentCampaigns =
                campaignRepository
                        .findTop5ByBrand_IdOrderByCreatedAtDesc(brandId)
                        .stream()
                        .map(this::mapCampaign)
                        .toList();

        /*
         * ============================
         * RECENT APPLICATIONS
         * ============================
         */

        Pageable top5 =
                PageRequest.of(0, 5);

        List<RecentApplicationResponse> recentApplications =
                applicationRepository
                        .findRecentApplicationsForBrand(
                                brandId,
                                top5
                        )
                        .stream()
                        .map(this::mapApplication)
                        .toList();

        return new BrandDashboardResponse(

                totalCampaigns,
                activeCampaigns,
                completedCampaigns,
                closedCampaigns,
                totalApplications,
                pendingApplications,
                acceptedApplications,
                rejectedApplications,
                totalBudget,
                acceptanceRate,
                completionRate,
                recentCampaigns,
                recentApplications
        );
    }

    private RecentCampaignResponse mapCampaign(
            Campaign campaign) {

        Long campaignId =
                campaign.getId();

        long applicationCount =
                applicationRepository
                        .countByCampaign_Id(
                                campaignId
                        );

        long pendingApplicationCount =
                applicationRepository
                        .countByCampaign_IdAndStatus(
                                campaignId,
                                ApplicationStatus.PENDING
                        );

        long acceptedApplicationCount =
                applicationRepository
                        .countByCampaign_IdAndStatus(
                                campaignId,
                                ApplicationStatus.ACCEPTED
                        );

        return new RecentCampaignResponse(

                campaign.getId(),
                campaign.getTitle(),
                campaign.getStatus(),
                campaign.getBudget(),
                campaign.getDeadline(),
                applicationCount,
                pendingApplicationCount,
                acceptedApplicationCount
        );
    }

    private RecentApplicationResponse mapApplication(
            CampaignApplication application) {

        return new RecentApplicationResponse(

                application.getId(),
                application.getCampaign().getId(),
                application.getInfluencer().getId(),
                application.getCampaign().getTitle(),
                application.getInfluencer().getUsername(),
                application.getStatus(),
                application.getCreatedAt()
        );
    }
}