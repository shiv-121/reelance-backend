package com.reelance.controller;

import com.reelance.dto.CollaborationRequestDto;
import com.reelance.dto.CollaborationResponse;
import com.reelance.entity.CollaborationStatus;
import com.reelance.entity.User;
import com.reelance.service.CollaborationService;
import com.reelance.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/collaborations")
public class CollaborationController {

    private final CollaborationService service;
    private final UserService userService;

    public CollaborationController(
            CollaborationService service,
            UserService userService) {
        this.service = service;
        this.userService = userService;
    }

    // ================================
    // 🏢 BRAND → SEND REQUEST
    // ================================
    @PostMapping("/brand")
    public CollaborationResponse sendRequest(
            Authentication authentication,
            @Valid @RequestBody CollaborationRequestDto dto) {

        User brand = userService.findByEmail(authentication.getName());
        return service.sendRequest(brand, dto);
    }

    // ================================
    // 👤 INFLUENCER → VIEW REQUESTS
    // ================================
    @GetMapping("/influencer")
    public List<CollaborationResponse> influencerRequests(
            Authentication authentication) {

        User influencer = userService.findByEmail(authentication.getName());
        return service.getInfluencerRequests(influencer);
    }

    // ================================
    // 👤 INFLUENCER → ACCEPT / REJECT
    // ================================
    @PatchMapping("/influencer/{id}")
    public CollaborationResponse updateStatus(
            @PathVariable Long id,
            @RequestParam CollaborationStatus status,
            Authentication authentication) {

        User influencer = userService.findByEmail(authentication.getName());
        return service.updateStatus(id, status, influencer);
    }
}
