package com.reelance.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@Table(
        name = "influencer_profile",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "instagram_handle")
        }
)
public class InfluencerProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "instagram_handle", nullable = false, unique = true)
    private String instagramHandle;

    private Long followers;
    private String niche;
    private Long pricePerPost;

    @OneToOne
    private User user;
}
