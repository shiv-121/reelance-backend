package com.reelance.controller;

import com.reelance.dto.CampaignApplicationRequest;
import com.reelance.dto.CampaignApplicationResponse;
import com.reelance.entity.ApplicationStatus;
import com.reelance.entity.User;
import com.reelance.service.CampaignApplicationService;
import com.reelance.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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

    // BRAND → ACCEPT / REJECT
    @PatchMapping("/{id}")
    @Operation(summary = "Update Application Status", description = "Brand accepts or rejects a campaign application")
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
}

