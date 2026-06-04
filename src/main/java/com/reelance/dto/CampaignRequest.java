package com.reelance.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CampaignRequest {
    private String title;

    private String description;

    private Long budget;

    private String niche;

    private LocalDate deadline;
}

