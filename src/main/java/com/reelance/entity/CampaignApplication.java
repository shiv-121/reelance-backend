package com.reelance.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "campaign_applications")
public class CampaignApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String proposal;

    private Long expectedPrice;

    private Long currentOfferPrice;

    private Long finalAgreedPrice;

    @Enumerated(EnumType.STRING)
    private NegotiationStatus negotiationStatus;

    @Enumerated(EnumType.STRING)
    private NegotiationParty nextResponseParty;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "campaign_id")
    private Campaign campaign;

    @ManyToOne
    @JoinColumn(name = "influencer_id")
    private User influencer;
}

