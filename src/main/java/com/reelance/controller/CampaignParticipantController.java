package com.reelance.controller;

import com.reelance.dto.CampaignParticipantResponse;
import com.reelance.entity.User;
import com.reelance.service.CampaignParticipantService;
import com.reelance.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/campaign-participants")
public class CampaignParticipantController {

    private final CampaignParticipantService service;
    private final UserService userService;

    public CampaignParticipantController(
            CampaignParticipantService service,
            UserService userService) {

        this.service = service;
        this.userService = userService;
    }

    // INFLUENCER → MY ACTIVE CAMPAIGNS
    @GetMapping("/my-campaigns")
    public List<CampaignParticipantResponse>
    myCampaigns(Authentication authentication) {

        User influencer =
                userService.findByEmail(
                        authentication.getName());

        return service.getMyCampaigns(
                influencer);
    }
}

