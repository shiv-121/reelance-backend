package com.reelance.service;

import com.reelance.dto.CampaignApplicationRequest;
import com.reelance.dto.CampaignApplicationResponse;
import com.reelance.entity.*;
import com.reelance.repository.CampaignApplicationRepository;
import com.reelance.repository.CampaignParticipantRepository;
import com.reelance.repository.CampaignRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

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

    // BRAND → ACCEPT / REJECT / SHORTLIST
    public CampaignApplicationResponse updateStatus(
            Long applicationId,
            ApplicationStatus status,
            User brand) {

        if (brand == null || brand.getRole() != Role.BRAND) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only brands can update application status");
        }

        CampaignApplication application =
                repository.findById(applicationId)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Application not found"));

        if (!application.getCampaign()
                .getBrand()
                .getId()
                .equals(brand.getId())) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Unauthorized");
        }

        validateStatusTransition(application, status);
        application.setStatus(status);

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

    private void validateStatusTransition(
            CampaignApplication application,
            ApplicationStatus requestedStatus) {

        if (requestedStatus == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Status is required");
        }

        ApplicationStatus currentStatus = application.getStatus();
        Campaign campaign = application.getCampaign();

        if (campaign == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Campaign is missing from the application");
        }

        Set<ApplicationStatus> validTransitions = switch (currentStatus) {
            case PENDING -> Set.of(
                    ApplicationStatus.SHORTLISTED,
                    ApplicationStatus.ACCEPTED,
                    ApplicationStatus.REJECTED
            );
            case SHORTLISTED -> Set.of(
                    ApplicationStatus.ACCEPTED,
                    ApplicationStatus.REJECTED
            );
            case ACCEPTED, REJECTED -> Set.of();
        };

        if (!validTransitions.contains(requestedStatus)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid status transition: "
                            + currentStatus
                            + " -> "
                            + requestedStatus);
        }

        if (Set.of(
                ApplicationStatus.SHORTLISTED,
                ApplicationStatus.ACCEPTED,
                ApplicationStatus.REJECTED
        ).contains(requestedStatus)
                && campaign.getStatus() != CampaignStatus.OPEN) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Applications can only be shortlisted, accepted, or rejected while the campaign is open");
        }
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

