package com.reelance.service;

import com.reelance.dto.CampaignRequest;
import com.reelance.dto.CampaignResponse;
import com.reelance.dto.CampaignSummaryResponse;
import com.reelance.dto.CampaignWorkspaceResponse;
import com.reelance.entity.Campaign;
import com.reelance.entity.CampaignStatus;
import com.reelance.entity.User;
import com.reelance.repository.CampaignRepository;
import com.reelance.specification.CampaignSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CampaignService {

    private final CampaignRepository repository;

    public CampaignService(CampaignRepository repository) {
        this.repository = repository;
    }

    // BRAND → CREATE CAMPAIGN
    public CampaignResponse createCampaign(
            User brand,
            CampaignRequest request) {

        Campaign campaign = repository.save(
                Campaign.builder()
                        .title(request.getTitle())
                        .description(request.getDescription())
                        .budget(request.getBudget())
                        .niche(request.getNiche())
                        .deadline(request.getDeadline())
                        .status(CampaignStatus.OPEN)
                        .createdAt(LocalDateTime.now())
                        .brand(brand)
                        .build()
        );

        return map(campaign);
    }

    // BRAND → MY CAMPAIGNS
    public List<CampaignResponse> getBrandCampaigns(
            User brand) {

        return repository.findByBrand_Id(
                        brand.getId())
                .stream()
                .map(this::map)
                .toList();
    }

    // INFLUENCER → BROWSE OPEN CAMPAIGNS
    public List<CampaignResponse> getOpenCampaigns() {

        return repository.findByStatus(
                        CampaignStatus.OPEN)
                .stream()
                .map(this::map)
                .toList();
    }

    // BRAND → WORKSPACE WITH SEARCH, FILTER, SORT, PAGINATION
    public CampaignWorkspaceResponse getBrandCampaignsWorkspace(
            User brand,
            String search,
            CampaignStatus status,
            String niche,
            Long budgetMin,
            Long budgetMax,
            LocalDate deadlineBefore,
            LocalDate deadlineAfter,
            Pageable pageable) {

        // Build specification with filters
        Specification<Campaign> spec = CampaignSpecification.filterByBrandAndCriteria(
                brand.getId(),
                search,
                status,
                niche,
                budgetMin,
                budgetMax,
                deadlineBefore,
                deadlineAfter
        );

        // Execute paginated query
        Page<Campaign> page = repository.findAll(spec, pageable);

        // Map campaigns to summary responses
        List<CampaignSummaryResponse> campaigns = page.getContent()
                .stream()
                .map(this::mapToSummary)
                .toList();

        // Get counts for dashboard
        long totalCampaigns = repository.countByBrand_Id(brand.getId());
        long openCampaigns = repository.countByBrand_IdAndStatus(brand.getId(), CampaignStatus.OPEN);
        long closedCampaigns = repository.countByBrand_IdAndStatus(brand.getId(), CampaignStatus.CLOSED);
        long completedCampaigns = repository.countByBrand_IdAndStatus(brand.getId(), CampaignStatus.COMPLETED);
        long totalBudget = repository.sumBudgetByBrandId(brand.getId());

        return new CampaignWorkspaceResponse(
                campaigns,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                totalCampaigns,
                openCampaigns,
                closedCampaigns,
                completedCampaigns,
                totalBudget
        );
    }

    // BRAND → CLOSE CAMPAIGN
    public CampaignResponse closeCampaign(
            Long campaignId,
            User brand) {

        Campaign campaign =
                repository.findById(campaignId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Campaign not found"));

        if (!campaign.getBrand()
                .getId()
                .equals(brand.getId())) {

            throw new RuntimeException(
                    "Unauthorized");
        }

        if (campaign.getStatus() == CampaignStatus.COMPLETED) {
            throw new RuntimeException(
                    "Campaign already completed");
        }

        campaign.setStatus(CampaignStatus.CLOSED);

        return map(repository.save(campaign));
    }

    // BRAND → COMPLETE CAMPAIGN
    public CampaignResponse completeCampaign(
            Long campaignId,
            User brand) {

        Campaign campaign =
                repository.findById(campaignId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Campaign not found"));

        if (!campaign.getBrand()
                .getId()
                .equals(brand.getId())) {

            throw new RuntimeException(
                    "Unauthorized");
        }

        if (campaign.getStatus() == CampaignStatus.OPEN) {
            throw new RuntimeException(
                    "Close campaign before completing");
        }

        campaign.setStatus(CampaignStatus.COMPLETED);

        return map(repository.save(campaign));
    }

    private CampaignResponse map(
            Campaign campaign) {

        return new CampaignResponse(
                campaign.getId(),
                campaign.getTitle(),
                campaign.getDescription(),
                campaign.getBudget(),
                campaign.getNiche(),
                campaign.getDeadline(),
                campaign.getStatus(),
                campaign.getBrand().getUsername()
        );
    }

    private CampaignSummaryResponse mapToSummary(Campaign campaign) {
        return new CampaignSummaryResponse(
                campaign.getId(),
                campaign.getTitle(),
                campaign.getDescription(),
                campaign.getBudget(),
                campaign.getNiche(),
                campaign.getDeadline(),
                campaign.getStatus()
        );
    }
}

