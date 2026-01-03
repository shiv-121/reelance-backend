package com.reelance.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/brand")
public class BrandController {

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('BRAND')")
    public String brandDashboard() {
        return "Welcome Brand 💼";
    }
}
