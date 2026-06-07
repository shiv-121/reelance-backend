package com.reelance.controller;

import com.reelance.dto.BrandAnalyticsResponse;
import com.reelance.entity.User;
import com.reelance.service.DashboardService;
import com.reelance.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@Tag(name = "Dashboard", description = "Dashboard analytics and metrics")
@SecurityRequirement(name = "Bearer")
public class DashboardController {

    private final DashboardService dashboardService;
    private final UserService userService;

    public DashboardController(
            DashboardService dashboardService,
            UserService userService) {

        this.dashboardService = dashboardService;
        this.userService = userService;
    }

    @GetMapping("/brand/analytics")
    @Operation(summary = "Get Brand Analytics", description = "Retrieve analytics metrics for the logged-in brand (campaigns, applications, participants, completed)")
    public BrandAnalyticsResponse analytics(
            Authentication authentication) {

        User brand =
                userService.findByEmail(
                        authentication.getName());

        return dashboardService
                .getBrandAnalytics(brand);
    }
}

