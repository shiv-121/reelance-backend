package com.reelance.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@Table(
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"collaboration_id", "reviewer_id"}
        )
)
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔗 Collaboration
    @ManyToOne(optional = false)
    private CollaborationRequest collaboration;

    // 👤 Who wrote the review
    @ManyToOne(optional = false)
    private User reviewer;

    // 👤 Who is being reviewed
    @ManyToOne(optional = false)
    private User reviewee;

    @Min(1)
    @Max(5)
    private int rating;

    @Column(length = 1000)
    private String comment;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
