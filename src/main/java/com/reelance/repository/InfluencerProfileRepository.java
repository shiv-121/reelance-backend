package com.reelance.repository;

import com.reelance.entity.InfluencerProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface InfluencerProfileRepository
        extends JpaRepository<InfluencerProfile, Long>,
        JpaSpecificationExecutor<InfluencerProfile> {


    // For authenticated influencer (create/update profile)
    Optional<InfluencerProfile> findByUserId(Long userId);

    // For public listing
    Page<InfluencerProfile> findByNicheIgnoreCase(
            String niche,
            Pageable pageable
    );

    boolean existsByInstagramHandleIgnoreCase(String instagramHandle);

    Optional<InfluencerProfile> findByInstagramHandleIgnoreCase(
            String instagramHandle);
}
