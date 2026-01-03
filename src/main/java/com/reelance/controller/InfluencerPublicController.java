package com.reelance.controller;

import com.reelance.dto.InfluencerProfileResponse;
import com.reelance.service.InfluencerPublicService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/influencers")
public class InfluencerPublicController {

    private final InfluencerPublicService service;

    public InfluencerPublicController(InfluencerPublicService service) {
        this.service = service;
    }

    @GetMapping
    public Page<InfluencerProfileResponse> listInfluencers(
            @RequestParam(required = false) String niche,
            @RequestParam(required = false) Long minFollowers,
            @RequestParam(required = false) Long maxFollowers,
            @RequestParam(required = false) Long minPrice,
            @RequestParam(required = false) Long maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "followers,desc") String sort
    ) {
        return service.searchInfluencers(
                niche,
                minFollowers,
                maxFollowers,
                minPrice,
                maxPrice,
                page,
                size,
                sort
        );
    }

    @GetMapping("/{id}")
    public InfluencerProfileResponse getInfluencer(
            @PathVariable Long id) {

        return service.getInfluencerById(id);
    }

}
