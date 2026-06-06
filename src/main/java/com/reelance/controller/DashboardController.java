package com.reelance.controller;

import com.reelance.dto.BrandAnalyticsResponse;
import com.reelance.entity.User;
import com.reelance.service.DashboardService;
import com.reelance.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
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
    public BrandAnalyticsResponse analytics(
            Authentication authentication) {

        User brand =
                userService.findByEmail(
                        authentication.getName());

        return dashboardService
                .getBrandAnalytics(brand);
    }
}

