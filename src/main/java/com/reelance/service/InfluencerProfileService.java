package com.reelance.service;

import com.reelance.dto.InfluencerProfileRequest;
import com.reelance.entity.InfluencerProfile;
import com.reelance.entity.User;
import com.reelance.exception.InstagramHandleAlreadyExistsException;
import com.reelance.repository.InfluencerProfileRepository;
import com.reelance.specification.InfluencerProfileSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class InfluencerProfileService {

    private final InfluencerProfileRepository profileRepository;

    public InfluencerProfileService(
            InfluencerProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    // ===============================
    // CREATE OR UPDATE PROFILE
    // ===============================
    public InfluencerProfile createOrUpdate(
            User user,
            InfluencerProfileRequest request) {

        String normalizedHandle =
                normalizeHandle(request.getInstagramHandle());

        // 🔒 Check if handle exists for another user
        profileRepository
                .findByInstagramHandleIgnoreCase(normalizedHandle)
                .ifPresent(existing -> {
                    if (!existing.getUser().getId().equals(user.getId())) {
                        throw new InstagramHandleAlreadyExistsException(
                                "Instagram handle already exists"
                        );
                    }
                });

        return profileRepository.findByUserId(user.getId())
                .map(existing -> {
                    // UPDATE
                    existing.setInstagramHandle(normalizedHandle);
                    existing.setFollowers(request.getFollowers());
                    existing.setNiche(request.getNiche());
                    existing.setPricePerPost(request.getPricePerPost());
                    return profileRepository.save(existing);
                })
                .orElseGet(() ->
                        // CREATE
                        profileRepository.save(
                                InfluencerProfile.builder()
                                        .instagramHandle(normalizedHandle)
                                        .followers(request.getFollowers())
                                        .niche(request.getNiche())
                                        .pricePerPost(request.getPricePerPost())
                                        .user(user)
                                        .build()
                        )
                );
    }

    // ===============================
    // PUBLIC SEARCH
    // ===============================
    public Page<InfluencerProfile> search(
            String niche,
            Long minFollowers,
            Long maxFollowers,
            Long minPrice,
            Long maxPrice,
            Pageable pageable
    ) {

        Specification<InfluencerProfile> spec =
                Specification
                        .allOf(
                                InfluencerProfileSpecification.hasNiche(niche),
                                InfluencerProfileSpecification.minFollowers(minFollowers),
                                InfluencerProfileSpecification.maxFollowers(maxFollowers),
                                InfluencerProfileSpecification.minPrice(minPrice),
                                InfluencerProfileSpecification.maxPrice(maxPrice)
                        );

        return profileRepository.findAll(spec, pageable);
    }

    // ===============================
    // HELPERS
    // ===============================
    private String normalizeHandle(String handle) {
        return handle.trim().toLowerCase();
    }
}
