package com.reelance.controller;

import com.reelance.dto.InfluencerProfileRequest;
import com.reelance.dto.InfluencerProfileResponse;
import com.reelance.entity.User;
import com.reelance.repository.UserRepository;
import com.reelance.service.InfluencerProfileService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/influencer/profile")
@PreAuthorize("hasRole('INFLUENCER')")
public class InfluencerProfileController {

    private final InfluencerProfileService profileService;
    private final UserRepository userRepository;

    public InfluencerProfileController(
            InfluencerProfileService profileService,
            UserRepository userRepository) {
        this.profileService = profileService;
        this.userRepository = userRepository;
    }

    @PostMapping
    public InfluencerProfileResponse createOrUpdateProfile(
            @Valid @RequestBody InfluencerProfileRequest request,
            Authentication authentication) {

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow();

        var profile = profileService.createOrUpdate(user, request);

        return new InfluencerProfileResponse(
                profile.getId(),
                profile.getInstagramHandle(),
                profile.getFollowers(),
                profile.getNiche(),
                profile.getPricePerPost(),
                user.getUsername()
        );
    }
}
