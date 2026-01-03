package com.reelance.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InfluencerProfileResponse {

    private Long id;
    private String instagramHandle;
    private Integer followers;
    private String niche;
    private Integer pricePerPost;
    private String username;
}
