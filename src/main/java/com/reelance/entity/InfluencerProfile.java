package com.reelance.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@Table(name = "influencer_profiles")
public class InfluencerProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String instagramHandle;

    @Column(nullable = false)
    private Integer followers;

    @Column(nullable = false)
    private String niche; // fashion, tech, fitness

    @Column(nullable = false)
    private Integer pricePerPost;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
}
