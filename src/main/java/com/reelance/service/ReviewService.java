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

    private final ReviewRepository reviewRepo;
    private final CollaborationRequestRepository collabRepo;

    public ReviewService(
            ReviewRepository reviewRepo,
            CollaborationRequestRepository collabRepo) {
        this.reviewRepo = reviewRepo;
        this.collabRepo = collabRepo;
    }

    public ReviewResponse createReview(
            User reviewer,
            ReviewRequest request) {

        CollaborationRequest collab =
                collabRepo.findById(request.getCollaborationId())
                        .orElseThrow(() ->
                                new RuntimeException("Collaboration not found"));

        if (collab.getStatus() != CollaborationStatus.COMPLETED) {
            throw new RuntimeException("Collaboration not completed");
        }

        if (reviewRepo.existsByCollaboration_IdAndReviewer_Id(
                collab.getId(), reviewer.getId())) {
            throw new RuntimeException("Review already submitted");
        }

        User reviewee =
                collab.getBrand().getId().equals(reviewer.getId())
                        ? collab.getInfluencerProfile().getUser()
                        : collab.getBrand();

        Review review = reviewRepo.save(
                Review.builder()
                        .collaboration(collab)
                        .reviewer(reviewer)
                        .reviewee(reviewee)
                        .rating(request.getRating())
                        .comment(request.getComment())
                        .build()
        );

        return map(review);
    }

    private ReviewResponse map(Review r) {
        return new ReviewResponse(
                r.getId(),
                r.getCollaboration().getId(),
                r.getRating(),
                r.getComment(),
                r.getReviewer().getUsername(),
                r.getReviewee().getUsername(),
                r.getCreatedAt()
        );
    }
}
