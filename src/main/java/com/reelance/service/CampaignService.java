package com.reelance.service;

import com.reelance.dto.CampaignRequest;
import com.reelance.dto.CampaignResponse;
import com.reelance.entity.Campaign;
import com.reelance.entity.CampaignStatus;
import com.reelance.entity.User;
import com.reelance.repository.CampaignRepository;
import org.springframework.stereotype.Service;

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
}

