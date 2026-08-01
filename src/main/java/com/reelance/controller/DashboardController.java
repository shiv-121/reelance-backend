package com.reelance.controller;

import com.reelance.dto.BrandAnalyticsResponse;
import com.reelance.dto.BrandDashboardResponse;
import com.reelance.entity.User;
import com.reelance.service.BrandDashboardService;
import com.reelance.service.DashboardService;
import com.reelance.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/brand")
@Tag(name = "Brand Dashboard", description = "Brand dashboard analytics and metrics")
@SecurityRequirement(name = "Bearer")
public class DashboardController {

    private final DashboardService dashboardService;
    private final BrandDashboardService brandDashboardService;
    private final UserService userService;

    public DashboardController(
            DashboardService dashboardService,
            BrandDashboardService brandDashboardService,
            UserService userService) {

        this.dashboardService = dashboardService;
        this.brandDashboardService = brandDashboardService;
        this.userService = userService;
    }

    @GetMapping("/analytics")
    @Operation(summary = "Get Brand Analytics", description = "Retrieve analytics metrics for the logged-in brand (campaigns, applications, participants, completed)")
    public BrandAnalyticsResponse analytics(
            Authentication authentication) {

        User brand =
                userService.findByEmail(
                        authentication.getName());

        return dashboardService
                .getBrandAnalytics(brand);
    }

    @GetMapping("/dashboard")
    @Operation(summary = "Get Brand Dashboard", description = "Retrieve comprehensive brand dashboard with metrics, recent campaigns, and recent applications")
    public BrandDashboardResponse dashboard(
            Authentication authentication) {

        User brand =
                userService.findByEmail(
                        authentication.getName());

        return brandDashboardService
                .getBrandDashboard(brand);
    }
}

