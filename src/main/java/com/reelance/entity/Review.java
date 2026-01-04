package com.reelance.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private CollaborationRequest collaboration;

    @ManyToOne
    private User reviewer;   // BRAND or INFLUENCER

    @ManyToOne
    private User reviewee;   // INFLUENCER or BRAND

    private int rating;      // 1–5

    private String comment;

    private LocalDateTime createdAt;
}
