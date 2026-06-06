package com.reelance.service;

import com.reelance.dto.CampaignApplicationRequest;
import com.reelance.dto.CampaignApplicationResponse;
import com.reelance.entity.*;
import com.reelance.repository.CampaignApplicationRepository;
import com.reelance.repository.CampaignParticipantRepository;
import com.reelance.repository.CampaignRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CampaignApplicationService {

    private final CampaignApplicationRepository repository;
    private final CampaignRepository campaignRepository;
    private final CampaignParticipantRepository participantRepository;

    public CampaignApplicationService(
            CampaignApplicationRepository repository,
            CampaignRepository campaignRepository,
            CampaignParticipantRepository participantRepository) {

        this.repository = repository;
        this.campaignRepository = campaignRepository;
        this.participantRepository = participantRepository;
    }

    // INFLUENCER → APPLY
    public CampaignApplicationResponse apply(
            User influencer,
            CampaignApplicationRequest request) {

        Campaign campaign =
                campaignRepository.findById(
                                request.getCampaignId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Campaign not found"));

        if (repository.existsByCampaign_IdAndInfluencer_Id(
                campaign.getId(),
                influencer.getId())) {

            throw new RuntimeException(
                    "Already applied to this campaign");
        }

        CampaignApplication application =
                repository.save(
                        CampaignApplication.builder()
                                .campaign(campaign)
                                .influencer(influencer)
                                .proposal(request.getProposal())
                                .expectedPrice(
                                        request.getExpectedPrice())
                                .status(ApplicationStatus.PENDING)
                                .createdAt(LocalDateTime.now())
                                .build()
                );

        return map(application);
    }

    // BRAND → VIEW CAMPAIGN APPLICATIONS
    public List<CampaignApplicationResponse>
    getCampaignApplications(Long campaignId) {

        return repository.findByCampaign_Id(campaignId)
                .stream()
                .map(this::map)
                .toList();
    }

    // INFLUENCER → MY APPLICATIONS
    public List<CampaignApplicationResponse>
    getMyApplications(User influencer) {

        return repository.findByInfluencer_Id(
                        influencer.getId())
                .stream()
                .map(this::map)
                .toList();
    }

    // BRAND → ACCEPT / REJECT
    public CampaignApplicationResponse updateStatus(
            Long applicationId,
            ApplicationStatus status,
            User brand) {

        CampaignApplication application =
                repository.findById(applicationId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Application not found"));

        if (!application.getCampaign()
                .getBrand()
                .getId()
                .equals(brand.getId())) {

            throw new RuntimeException(
                    "Unauthorized");
        }

        application.setStatus(status);

        // If application is accepted, create a participant
        if (status == ApplicationStatus.ACCEPTED) {
            if (!participantRepository.existsByCampaign_IdAndInfluencer_Id(
                    application.getCampaign().getId(),
                    application.getInfluencer().getId())) {

                participantRepository.save(
                        CampaignParticipant.builder()
                                .campaign(application.getCampaign())
                                .influencer(application.getInfluencer())
                                .joinedAt(LocalDateTime.now())
                                .build()
                );
            }
        }

        return map(repository.save(application));
    }

    private CampaignApplicationResponse map(
            CampaignApplication application) {

        return new CampaignApplicationResponse(
                application.getId(),
                application.getCampaign().getId(),
                application.getCampaign().getTitle(),
                application.getInfluencer().getUsername(),
                application.getProposal(),
                application.getExpectedPrice(),
                application.getStatus()
        );
    }
}

