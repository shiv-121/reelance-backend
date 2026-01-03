package com.reelance.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InfluencerProfileRequest {

    @NotBlank
    private String instagramHandle;

    @NotNull
    @Min(0)
    private Integer followers;

    @NotBlank
    private String niche;

    @NotNull
    @Min(100)
    private Integer pricePerPost;
}
