package com.reelance.controller;

import com.reelance.dto.CampaignParticipantResponse;
import com.reelance.dto.CampaignRequest;
import com.reelance.dto.CampaignResponse;
import com.reelance.entity.User;
import com.reelance.service.CampaignParticipantService;
import com.reelance.service.CampaignService;
import com.reelance.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/campaigns")
@Tag(name = "Campaigns", description = "Campaign management endpoints")
@SecurityRequirement(name = "Bearer")
public class CampaignController {

    private final CampaignService service;
    private final UserService userService;
    private final CampaignParticipantService participantService;

    public CampaignController(
            CampaignService service,
            UserService userService,
            CampaignParticipantService participantService) {

        this.service = service;
        this.userService = userService;
        this.participantService = participantService;
    }

    // BRAND → CREATE CAMPAIGN
    @PostMapping
    @Operation(summary = "Create Campaign", description = "Brand creates a new campaign")
    public CampaignResponse createCampaign(
            Authentication authentication,
            @Valid @RequestBody CampaignRequest request) {

        User brand =
                userService.findByEmail(
                        authentication.getName());

        return service.createCampaign(
                brand,
                request);
    }

    // BRAND → MY CAMPAIGNS
    @GetMapping("/brand")
    @Operation(summary = "Get My Campaigns", description = "Retrieve all campaigns created by the logged-in brand")
    public List<CampaignResponse> myCampaigns(
            Authentication authentication) {

        User brand =
                userService.findByEmail(
                        authentication.getName());

        return service.getBrandCampaigns(
                brand);
    }

    // INFLUENCER → BROWSE CAMPAIGNS
    @GetMapping
    @Operation(summary = "Browse Open Campaigns", description = "Get all open campaigns for browsing")
    public List<CampaignResponse> browseCampaigns() {

        return service.getOpenCampaigns();
    }

    // BRAND → CLOSE CAMPAIGN
    @PatchMapping("/{id}/close")
    @Operation(summary = "Close Campaign", description = "Brand closes a campaign (changes status to CLOSED)")
    public CampaignResponse closeCampaign(
            @PathVariable Long id,
            Authentication authentication) {

        User brand =
                userService.findByEmail(
                        authentication.getName());

        return service.closeCampaign(
                id,
                brand);
    }

    // BRAND → COMPLETE CAMPAIGN
    @PatchMapping("/{id}/complete")
    @Operation(summary = "Complete Campaign", description = "Brand completes a campaign (changes status to COMPLETED)")
    public CampaignResponse completeCampaign(
            @PathVariable Long id,
            Authentication authentication) {

        User brand =
                userService.findByEmail(
                        authentication.getName());

        return service.completeCampaign(
                id,
                brand);
    }

    // BRAND → VIEW PARTICIPANTS
    @GetMapping("/{campaignId}/participants")
    @Operation(summary = "Get Campaign Participants", description = "View all participants (accepted influencers) for a campaign")
    public List<CampaignParticipantResponse>
    participants(
            @PathVariable Long campaignId) {

        return participantService
                .getCampaignParticipants(
                        campaignId);
    }
}

