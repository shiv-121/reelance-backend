package com.reelance.service;

import com.reelance.dto.CampaignApplicationRequest;
import com.reelance.dto.CampaignApplicationResponse;
import com.reelance.dto.NegotiationOfferRequest;
import com.reelance.dto.NegotiationResponse;
import com.reelance.entity.*;
import com.reelance.repository.CampaignApplicationRepository;
import com.reelance.repository.CampaignParticipantRepository;
import com.reelance.repository.CampaignRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
                                .currentOfferPrice(null)
                                .finalAgreedPrice(null)
                                .negotiationStatus(NegotiationStatus.NOT_STARTED)
                                .nextResponseParty(null)
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
            createParticipantIfNeeded(application);
        }

        return map(repository.save(application));
    }

    private void createParticipantIfNeeded(CampaignApplication application) {
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

    @Transactional
    public NegotiationResponse brandCounterOffer(
            Long applicationId,
            NegotiationOfferRequest request,
            User brand) {

        CampaignApplication application = getApplicationForNegotiation(applicationId);
        ensureBrandOwnsCampaign(application, brand);
        ensureNegotiationOpen(application);
        ensureNegotiationCanStartOrContinue(application, NegotiationParty.BRAND);

        application.setCurrentOfferPrice(request.getAmount());
        application.setNegotiationStatus(NegotiationStatus.PENDING_INFLUENCER_RESPONSE);
        application.setNextResponseParty(NegotiationParty.INFLUENCER);
        application.setFinalAgreedPrice(null);
        return mapNegotiation(repository.save(application));
    }

    @Transactional
    public NegotiationResponse influencerAcceptBrandOffer(
            Long applicationId,
            User influencer) {

        CampaignApplication application = getApplicationForNegotiation(applicationId);
        ensureInfluencerOwnsApplication(application, influencer);
        ensureNegotiationOpen(application);
        ensureNegotiationAwaitingReply(application, NegotiationParty.INFLUENCER);

        application.setFinalAgreedPrice(application.getCurrentOfferPrice());
        application.setNegotiationStatus(NegotiationStatus.AGREED);
        application.setNextResponseParty(null);
        return mapNegotiation(repository.save(application));
    }

    @Transactional
    public NegotiationResponse influencerRejectBrandOffer(
            Long applicationId,
            User influencer) {

        CampaignApplication application = getApplicationForNegotiation(applicationId);
        ensureInfluencerOwnsApplication(application, influencer);
        ensureNegotiationOpen(application);
        ensureNegotiationAwaitingReply(application, NegotiationParty.INFLUENCER);

        application.setCurrentOfferPrice(null);
        application.setFinalAgreedPrice(null);
        application.setNegotiationStatus(NegotiationStatus.DECLINED);
        application.setNextResponseParty(null);
        return mapNegotiation(repository.save(application));
    }

    @Transactional
    public NegotiationResponse influencerCounterOffer(
            Long applicationId,
            NegotiationOfferRequest request,
            User influencer) {

        CampaignApplication application = getApplicationForNegotiation(applicationId);
        ensureInfluencerOwnsApplication(application, influencer);
        ensureNegotiationOpen(application);
        ensureNegotiationAwaitingReply(application, NegotiationParty.INFLUENCER);

        application.setCurrentOfferPrice(request.getAmount());
        application.setNegotiationStatus(NegotiationStatus.PENDING_BRAND_RESPONSE);
        application.setNextResponseParty(NegotiationParty.BRAND);
        application.setFinalAgreedPrice(null);
        return mapNegotiation(repository.save(application));
    }

    @Transactional
    public NegotiationResponse brandAcceptInfluencerCounterOffer(
            Long applicationId,
            User brand) {

        CampaignApplication application = getApplicationForNegotiation(applicationId);
        ensureBrandOwnsCampaign(application, brand);
        ensureNegotiationOpen(application);
        ensureNegotiationAwaitingReply(application, NegotiationParty.BRAND);

        application.setFinalAgreedPrice(application.getCurrentOfferPrice());
        application.setNegotiationStatus(NegotiationStatus.AGREED);
        application.setNextResponseParty(null);
        return mapNegotiation(repository.save(application));
    }

    @Transactional
    public NegotiationResponse brandRejectInfluencerCounterOffer(
            Long applicationId,
            User brand) {

        CampaignApplication application = getApplicationForNegotiation(applicationId);
        ensureBrandOwnsCampaign(application, brand);
        ensureNegotiationOpen(application);
        ensureNegotiationAwaitingReply(application, NegotiationParty.BRAND);

        application.setCurrentOfferPrice(null);
        application.setFinalAgreedPrice(null);
        application.setNegotiationStatus(NegotiationStatus.DECLINED);
        application.setNextResponseParty(null);
        return mapNegotiation(repository.save(application));
    }

    @Transactional
    public NegotiationResponse brandCounterOfferAfterInfluencerCounter(
            Long applicationId,
            NegotiationOfferRequest request,
            User brand) {

        CampaignApplication application = getApplicationForNegotiation(applicationId);
        ensureBrandOwnsCampaign(application, brand);
        ensureNegotiationOpen(application);
        ensureNegotiationAwaitingReply(application, NegotiationParty.BRAND);

        application.setCurrentOfferPrice(request.getAmount());
        application.setNegotiationStatus(NegotiationStatus.PENDING_INFLUENCER_RESPONSE);
        application.setNextResponseParty(NegotiationParty.INFLUENCER);
        application.setFinalAgreedPrice(null);
        return mapNegotiation(repository.save(application));
    }

    private CampaignApplication getApplicationForNegotiation(Long applicationId) {
        return repository.findById(applicationId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Application not found"));
    }

    private void ensureBrandOwnsCampaign(CampaignApplication application, User brand) {
        if (!application.getCampaign().getBrand().getId().equals(brand.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unauthorized");
        }
    }

    private void ensureInfluencerOwnsApplication(CampaignApplication application, User influencer) {
        if (!application.getInfluencer().getId().equals(influencer.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unauthorized");
        }
    }

    private void ensureNegotiationOpen(CampaignApplication application) {
        if (application.getStatus() == ApplicationStatus.ACCEPTED || application.getStatus() == ApplicationStatus.REJECTED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Negotiation is not allowed on accepted or rejected applications");
        }
        if (application.getCampaign().getStatus() != CampaignStatus.OPEN) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Campaign must be open for negotiation");
        }
    }

    private void ensureNegotiationCanStartOrContinue(CampaignApplication application, NegotiationParty actor) {
        NegotiationStatus negotiationStatus = normalizeNegotiationStatus(application);

        if (negotiationStatus == NegotiationStatus.AGREED
                || negotiationStatus == NegotiationStatus.DECLINED) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Negotiation has already been concluded");
        }

        if (actor == NegotiationParty.BRAND) {
            if (negotiationStatus == NegotiationStatus.NOT_STARTED
                    || negotiationStatus == NegotiationStatus.PENDING_BRAND_RESPONSE) {
                return;
            }
        }

        if (actor == NegotiationParty.INFLUENCER) {
            if (negotiationStatus == NegotiationStatus.NOT_STARTED
                    || negotiationStatus == NegotiationStatus.PENDING_INFLUENCER_RESPONSE) {
                return;
            }
        }

        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Invalid negotiation state for this action");
    }

    private void ensureNegotiationAwaitingReply(CampaignApplication application, NegotiationParty actor) {
        NegotiationStatus negotiationStatus = normalizeNegotiationStatus(application);

        if (negotiationStatus == NegotiationStatus.NOT_STARTED) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "No negotiation is currently in progress");
        }
        if (negotiationStatus == NegotiationStatus.AGREED
                || negotiationStatus == NegotiationStatus.DECLINED) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Negotiation has already been concluded");
        }

        NegotiationParty nextResponseParty = normalizeNextResponseParty(application, negotiationStatus);
        if (nextResponseParty != actor) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "It is not the current party's turn to respond");
        }
    }

    private NegotiationStatus normalizeNegotiationStatus(CampaignApplication application) {
        NegotiationStatus negotiationStatus = application.getNegotiationStatus();
        if (negotiationStatus == null) {
            return NegotiationStatus.NOT_STARTED;
        }
        return negotiationStatus;
    }

    private NegotiationParty normalizeNextResponseParty(CampaignApplication application, NegotiationStatus negotiationStatus) {
        NegotiationParty nextResponseParty = application.getNextResponseParty();
        if (nextResponseParty != null) {
            return nextResponseParty;
        }
        if (negotiationStatus == NegotiationStatus.PENDING_INFLUENCER_RESPONSE) {
            return NegotiationParty.INFLUENCER;
        }
        if (negotiationStatus == NegotiationStatus.PENDING_BRAND_RESPONSE) {
            return NegotiationParty.BRAND;
        }
        return null;
    }

    private NegotiationResponse mapNegotiation(CampaignApplication application) {
        NegotiationStatus negotiationStatus = normalizeNegotiationStatus(application);
        NegotiationParty nextResponseParty = normalizeNextResponseParty(application, negotiationStatus);

        return new NegotiationResponse(
                application.getId(),
                application.getCampaign().getId(),
                application.getInfluencer().getId(),
                application.getCampaign().getBrand().getId(),
                application.getExpectedPrice(),
                application.getCurrentOfferPrice(),
                application.getFinalAgreedPrice(),
                negotiationStatus,
                nextResponseParty,
                application.getStatus()
        );
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
                application.getCurrentOfferPrice(),
                application.getFinalAgreedPrice(),
                application.getNegotiationStatus(),
                application.getNextResponseParty(),
                application.getStatus()
        );
    }
}

