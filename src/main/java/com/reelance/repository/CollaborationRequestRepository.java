package com.reelance.repository;

import com.reelance.entity.CollaborationRequest;
import com.reelance.entity.CollaborationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CollaborationRequestRepository
        extends JpaRepository<CollaborationRequest, Long> {

    List<CollaborationRequest> findByInfluencerProfile_User_Id(Long userId);

    List<CollaborationRequest> findByBrand_Id(Long brandId);

    boolean existsByBrand_IdAndInfluencerProfile_IdAndStatus(
            Long brandId,
            Long influencerProfileId,
            CollaborationStatus status
    );

    List<CollaborationRequest> findByBrand_IdAndStatus(
            Long brandId,
            CollaborationStatus status
    );

}
