package com.reelance.service;

import com.reelance.dto.InfluencerProfileRequest;
import com.reelance.entity.InfluencerProfile;
import com.reelance.entity.User;
import com.reelance.repository.InfluencerProfileRepository;

import com.reelance.specification.InfluencerProfileSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import org.springframework.stereotype.Service;

@Service
public class InfluencerProfileService {

    private final InfluencerProfileRepository profileRepository;

    public InfluencerProfileService(InfluencerProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    public InfluencerProfile createOrUpdate(
            User user,
            InfluencerProfileRequest request) {

        return profileRepository.findByUserId(user.getId())
                .map(existing -> {
                    existing.setInstagramHandle(request.getInstagramHandle());
                    existing.setFollowers(request.getFollowers());
                    existing.setNiche(request.getNiche());
                    existing.setPricePerPost(request.getPricePerPost());
                    return profileRepository.save(existing);
                })
                .orElseGet(() ->
                        profileRepository.save(
                                InfluencerProfile.builder()
                                        .instagramHandle(request.getInstagramHandle())
                                        .followers(request.getFollowers())
                                        .niche(request.getNiche())
                                        .pricePerPost(request.getPricePerPost())
                                        .user(user)
                                        .build()
                        )
                );
    }

    public Page<InfluencerProfile> search(
            String niche,
            Long minFollowers,
            Long maxFollowers,
            Long minPrice,
            Long maxPrice,
            Pageable pageable
    ) {
        Specification<InfluencerProfile> spec =
                Specification.allOf(InfluencerProfileSpecification.hasNiche(niche))
                        .and(InfluencerProfileSpecification.minFollowers(minFollowers))
                        .and(InfluencerProfileSpecification.maxFollowers(maxFollowers))
                        .and(InfluencerProfileSpecification.minPrice(minPrice))
                        .and(InfluencerProfileSpecification.maxPrice(maxPrice));

        return profileRepository.findAll(spec, pageable);
    }

}
