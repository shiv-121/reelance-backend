package com.reelance.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InfluencerProfileResponse {

    private Long id;
    private String instagramHandle;
    private Long followers;
    private String niche;
    private Long pricePerPost;
    private String username;
}

