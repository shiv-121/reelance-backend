package com.reelance.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NegotiationOfferRequest {

    @NotNull(message = "Offer amount is required")
    @Positive(message = "Offer amount must be positive")
    private Long amount;
}
