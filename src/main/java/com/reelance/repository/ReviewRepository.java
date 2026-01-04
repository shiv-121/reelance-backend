package com.reelance.repository;
import com.reelance.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository
        extends JpaRepository<Review, Long> {

    boolean existsByCollaboration_IdAndReviewer_Id(
            Long collaborationId,
            Long reviewerId
    );

    List<Review> findByReviewee_Id(Long userId);
}
