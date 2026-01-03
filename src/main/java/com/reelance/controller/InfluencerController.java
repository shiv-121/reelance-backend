package com.reelance.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/influencer")
public class InfluencerController {

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('INFLUENCER')")
    public String influencerDashboard() {
        return "Welcome Influencer 🎥";
    }
}
