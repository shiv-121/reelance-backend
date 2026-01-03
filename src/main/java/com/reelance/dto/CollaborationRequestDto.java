package com.reelance.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CollaborationRequestDto {

    @NotNull
    private Long influencerProfileId;

    @NotBlank
    private String message;
}
