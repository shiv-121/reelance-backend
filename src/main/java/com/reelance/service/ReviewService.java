package com.reelance.service;

import com.reelance.dto.ReviewRequest;
import com.reelance.dto.ReviewResponse;
import com.reelance.entity.*;
import com.reelance.repository.CollaborationRequestRepository;
import com.reelance.repository.ReviewRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service

public class ReviewService {

    private final ReviewRepository repository;
    private final CollaborationRequestRepository collaborationRepo;

    public ReviewService(
            ReviewRepository repository,
            CollaborationRequestRepository collaborationRepo) {
        this.repository = repository;
        this.collaborationRepo = collaborationRepo;
    }

    public ReviewResponse submitReview(
            Long collaborationId,
            User reviewer,
            ReviewRequest request) {

        CollaborationRequest collaboration =
                collaborationRepo.findById(collaborationId)
                        .orElseThrow(() ->
                                new RuntimeException("Collaboration not found"));

        if (collaboration.getStatus() != CollaborationStatus.COMPLETED) {
            throw new RuntimeException("Collaboration not completed");
        }

        if (repository.existsByCollaboration_IdAndReviewer_Id(
                collaborationId, reviewer.getId())) {
            throw new RuntimeException("Review already submitted");
        }

        User reviewee =
                reviewer.getRole() == Role.BRAND
                        ? collaboration.getInfluencerProfile().getUser()
                        : collaboration.getBrand();

        Review review = repository.save(
                Review.builder()
                        .collaboration(collaboration)
                        .reviewer(reviewer)
                        .reviewee(reviewee)
                        .rating(request.getRating())
                        .comment(request.getComment())
                        .createdAt(LocalDateTime.now())
                        .build()
        );

        return new ReviewResponse(
                review.getRating(),
                review.getComment(),
                reviewer.getUsername(),
                review.getCreatedAt()
        );
    }
}
