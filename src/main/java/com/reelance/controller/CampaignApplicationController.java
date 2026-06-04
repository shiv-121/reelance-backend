package com.reelance.controller;

import com.reelance.dto.CampaignApplicationRequest;
import com.reelance.dto.CampaignApplicationResponse;
import com.reelance.entity.ApplicationStatus;
import com.reelance.entity.User;
import com.reelance.service.CampaignApplicationService;
import com.reelance.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/campaign-applications")
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
    public List<CampaignApplicationResponse>
    campaignApplications(
            @PathVariable Long campaignId) {

        return service.getCampaignApplications(
                campaignId);
    }

    // INFLUENCER → MY APPLICATIONS
    @GetMapping("/my-applications")
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

