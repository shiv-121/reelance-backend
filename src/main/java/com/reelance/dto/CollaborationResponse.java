package com.reelance.dto;

import com.reelance.entity.CollaborationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CollaborationResponse {

    private Long collaborationId;
    private String brandName;
    private String influencerUsername;
    private String message;
    private CollaborationStatus status;
}
