package com.reelance.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CollaborationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Brand user
    @ManyToOne(optional = false)
    private User brand;

    // Influencer profile
    @ManyToOne(optional = false)
    private InfluencerProfile influencerProfile;

    @Column(nullable = false, length = 1000)
    private String message;

    @Enumerated(EnumType.STRING)
    private CollaborationStatus status;

    private LocalDateTime createdAt;
}
