package com.reelance.controller;

import com.reelance.dto.CampaignRequest;
import com.reelance.dto.CampaignResponse;
import com.reelance.entity.User;
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

    public CampaignController(
            CampaignService service,
            UserService userService) {

        this.service = service;
        this.userService = userService;
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
}

