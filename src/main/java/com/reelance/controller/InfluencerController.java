package com.reelance.controller;

import com.reelance.dto.InfluencerProfileResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.reelance.service.InfluencerProfileService;

@RestController
@RequestMapping("/api/influencer")
public class InfluencerController {

    private final InfluencerProfileService influencerProfileService;

    public InfluencerController(InfluencerProfileService influencerProfileService) {
        this.influencerProfileService = influencerProfileService;
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('INFLUENCER')")
    public String influencerDashboard() {
        return "Welcome Influencer 🎥";
    }

    @GetMapping("/profile")
    @PreAuthorize("hasRole('INFLUENCER')")
    public ResponseEntity<InfluencerProfileResponse>
    getMyProfile() {

        return ResponseEntity.ok(
                influencerProfileService
                        .getMyProfile()
        );
    }
}
