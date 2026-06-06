package com.reelance.controller;

import com.reelance.dto.CampaignParticipantResponse;
import com.reelance.dto.CampaignRequest;
import com.reelance.dto.CampaignResponse;
import com.reelance.entity.User;
import com.reelance.service.CampaignParticipantService;
import com.reelance.service.CampaignService;
import com.reelance.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/campaigns")
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
    public List<CampaignResponse> browseCampaigns() {

        return service.getOpenCampaigns();
    }

    // BRAND → CLOSE CAMPAIGN
    @PatchMapping("/{id}/close")
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
    public List<CampaignParticipantResponse>
    participants(
            @PathVariable Long campaignId) {

        return participantService
                .getCampaignParticipants(
                        campaignId);
    }
}

