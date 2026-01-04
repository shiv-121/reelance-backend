package com.reelance.controller;

import com.reelance.dto.ReviewRequest;
import com.reelance.dto.ReviewResponse;
import com.reelance.entity.User;
import com.reelance.service.ReviewService;
import com.reelance.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService service;
    private final UserService userService;

    public ReviewController(
            ReviewService service,
            UserService userService) {
        this.service = service;
        this.userService = userService;
    }

    /**
     * CREATE REVIEW (Brand or Influencer)
     * Only allowed after collaboration is COMPLETED
     */
    @PostMapping
    public ReviewResponse createReview(
            @Valid @RequestBody ReviewRequest request,
            Authentication authentication) {

        User reviewer =
                userService.findByEmail(authentication.getName());

        return service.createReview(reviewer, request);
    }
}
