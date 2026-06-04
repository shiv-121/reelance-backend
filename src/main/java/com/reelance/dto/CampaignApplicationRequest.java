package com.reelance.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CampaignApplicationRequest {

    private Long campaignId;

    private String proposal;

    private Long expectedPrice;
}

