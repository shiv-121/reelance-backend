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

        // Count metrics
        long activeCampaigns = campaignRepository.countByBrand_IdAndStatus(brandId, CampaignStatus.OPEN);
        long pendingApplications = applicationRepository.countByCampaign_Brand_IdAndStatus(brandId, ApplicationStatus.PENDING);
        long acceptedApplications = applicationRepository.countByCampaign_Brand_IdAndStatus(brandId, ApplicationStatus.ACCEPTED);
        long totalBudget = campaignRepository.sumBudgetByBrandId(brandId);

        // Get recent campaigns with optimized query
        List<RecentCampaignResponse> recentCampaigns = campaignRepository
                .findTop5ByBrand_IdOrderByCreatedAtDesc(brandId)
                .stream()
                .map(this::mapCampaign)
                .toList();

        // Get recent applications with fetch joins to avoid N+1 (limit to 5 results)
        Pageable top5 = PageRequest.of(0, 5);
        List<RecentApplicationResponse> recentApplications = applicationRepository
                .findRecentApplicationsForBrand(brandId, top5)
                .stream()
                .map(this::mapApplication)
                .toList();

        return new BrandDashboardResponse(
                activeCampaigns,
                pendingApplications,
                acceptedApplications,
                totalBudget,
                recentCampaigns,
                recentApplications
        );
    }

    private RecentCampaignResponse mapCampaign(Campaign campaign) {
        return new RecentCampaignResponse(
                campaign.getId(),
                campaign.getTitle(),
                campaign.getStatus(),
                campaign.getBudget(),
                campaign.getDeadline()
        );
    }

    private RecentApplicationResponse mapApplication(CampaignApplication application) {
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





