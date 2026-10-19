package com.reelance.controller;

import com.reelance.dto.*;
import com.reelance.entity.ApplicationStatus;
import com.reelance.entity.User;
import com.reelance.service.CampaignApplicationService;
import com.reelance.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/campaign-applications")
@Tag(name = "Campaign Applications", description = "Campaign application management")
@SecurityRequirement(name = "Bearer")
public class CampaignApplicationController {

    private final CampaignApplicationService service;
    private final UserService userService;

    public CampaignApplicationController(
            CampaignApplicationService service,
            UserService userService) {

        this.service = service;
        this.userService = userService;
    }

    // INFLUENCER → APPLY
    @PostMapping
    @Operation(summary = "Apply to Campaign", description = "Influencer submits an application for a campaign")
    public CampaignApplicationResponse apply(
            Authentication authentication,
            @Valid @RequestBody
            CampaignApplicationRequest request) {

        User influencer =
                userService.findByEmail(
                        authentication.getName());

        return service.apply(
                influencer,
                request);
    }

    // BRAND → VIEW APPLICATIONS
    @GetMapping("/campaign/{campaignId}")
    @Operation(summary = "Get Campaign Applications", description = "View all applications for a specific campaign")
    public List<CampaignApplicationResponse>
    campaignApplications(
            @PathVariable Long campaignId) {

        return service.getCampaignApplications(
                campaignId);
    }

    // INFLUENCER → MY APPLICATIONS
    @GetMapping("/my-applications")
    @Operation(summary = "Get My Applications", description = "View all applications submitted by the influencer")
    public List<CampaignApplicationResponse>
    myApplications(
            Authentication authentication) {

        User influencer =
                userService.findByEmail(
                        authentication.getName());

        return service.getMyApplications(
                influencer);
    }

    // BRAND → SHORTLIST / ACCEPT / REJECT
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('BRAND')")
    @Operation(summary = "Update Application Status", description = "Brand shortlists, accepts, or rejects a campaign application")
    public CampaignApplicationResponse updateStatus(
            @PathVariable Long id,
            @RequestParam ApplicationStatus status,
            Authentication authentication) {

        User brand =
                userService.findByEmail(
                        authentication.getName());

        return service.updateStatus(
                id,
                status,
                brand);
    }

    @PostMapping("/{id}/brand-counter-offer")
    @PreAuthorize("hasRole('BRAND')")
    @Operation(summary = "Submit a brand counteroffer", description = "Brand submits or updates a counteroffer for the application")
    public NegotiationResponse brandCounterOffer(
            @PathVariable Long id,
            @Valid @RequestBody NegotiationOfferRequest request,
            Authentication authentication) {

        User brand = userService.findByEmail(authentication.getName());
        return service.brandCounterOffer(id, request, brand);
    }

    @PostMapping("/{id}/influencer-accept-brand-offer")
    @PreAuthorize("hasRole('INFLUENCER')")
    @Operation(summary = "Accept a brand offer", description = "Influencer accepts the current brand price offer")
    public NegotiationResponse influencerAcceptBrandOffer(
            @PathVariable Long id,
            Authentication authentication) {

        User influencer = userService.findByEmail(authentication.getName());
        return service.influencerAcceptBrandOffer(id, influencer);
    }

    @PostMapping("/{id}/influencer-reject-brand-offer")
    @PreAuthorize("hasRole('INFLUENCER')")
    @Operation(summary = "Reject a brand offer", description = "Influencer rejects the current brand price offer")
    public NegotiationResponse influencerRejectBrandOffer(
            @PathVariable Long id,
            Authentication authentication) {

        User influencer = userService.findByEmail(authentication.getName());
        return service.influencerRejectBrandOffer(id, influencer);
    }

    @PostMapping("/{id}/influencer-counter-offer")
    @PreAuthorize("hasRole('INFLUENCER')")
    @Operation(summary = "Submit an influencer counteroffer", description = "Influencer submits a counteroffer to the current brand offer")
    public NegotiationResponse influencerCounterOffer(
            @PathVariable Long id,
            @Valid @RequestBody NegotiationOfferRequest request,
            Authentication authentication) {

        User influencer = userService.findByEmail(authentication.getName());
        return service.influencerCounterOffer(id, request, influencer);
    }

    @PostMapping("/{id}/brand-accept-influencer-counter")
    @PreAuthorize("hasRole('BRAND')")
    @Operation(summary = "Accept an influencer counteroffer", description = "Brand accepts the current influencer counteroffer")
    public NegotiationResponse brandAcceptInfluencerCounterOffer(
            @PathVariable Long id,
            Authentication authentication) {

        User brand = userService.findByEmail(authentication.getName());
        return service.brandAcceptInfluencerCounterOffer(id, brand);
    }

    @PostMapping("/{id}/brand-reject-influencer-counter")
    @PreAuthorize("hasRole('BRAND')")
    @Operation(summary = "Reject an influencer counteroffer", description = "Brand rejects the current influencer counteroffer")
    public NegotiationResponse brandRejectInfluencerCounterOffer(
            @PathVariable Long id,
            Authentication authentication) {

        User brand = userService.findByEmail(authentication.getName());
        return service.brandRejectInfluencerCounterOffer(id, brand);
    }

    @PostMapping("/{id}/brand-counter-offer-after-influencer-counter")
    @PreAuthorize("hasRole('BRAND')")
    @Operation(summary = "Submit another brand counteroffer", description = "Brand submits a new counteroffer after an influencer counteroffer")
    public NegotiationResponse brandCounterOfferAfterInfluencerCounter(
            @PathVariable Long id,
            @Valid @RequestBody NegotiationOfferRequest request,
            Authentication authentication) {

        User brand = userService.findByEmail(authentication.getName());
        return service.brandCounterOfferAfterInfluencerCounter(id, request, brand);
    }
}

