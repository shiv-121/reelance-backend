package com.reelance.service;

import com.reelance.dto.InfluencerProfileResponse;
import com.reelance.entity.InfluencerProfile;
import com.reelance.repository.InfluencerProfileRepository;
import com.reelance.specification.InfluencerProfileSpecification;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class InfluencerPublicService {

    private final InfluencerProfileRepository repository;

    public InfluencerPublicService(InfluencerProfileRepository repository) {
        this.repository = repository;
    }

    public Page<InfluencerProfileResponse> searchInfluencers(
            String niche,
            Long minFollowers,
            Long maxFollowers,
            Long minPrice,
            Long maxPrice,
            int page,
            int size,
            String sort
    ) {

        // sort example: "followers,desc"
        String[] sortParams = sort.split(",");
        Sort.Direction direction =
                sortParams.length > 1 && sortParams[1].equalsIgnoreCase("asc")
                        ? Sort.Direction.ASC
                        : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(direction, sortParams[0])
        );

        Specification<InfluencerProfile> spec =
                Specification.allOf(
                        InfluencerProfileSpecification.hasNiche(niche),
                        InfluencerProfileSpecification.minFollowers(minFollowers),
                        InfluencerProfileSpecification.maxFollowers(maxFollowers),
                        InfluencerProfileSpecification.minPrice(minPrice),
                        InfluencerProfileSpecification.maxPrice(maxPrice)
                );

        return repository.findAll(spec, pageable)
                .map(this::toResponse);
    }

    private InfluencerProfileResponse toResponse(InfluencerProfile profile) {
        return new InfluencerProfileResponse(
                profile.getId(),
                profile.getInstagramHandle(),
                profile.getFollowers(),
                profile.getNiche(),
                profile.getPricePerPost(),
                profile.getUser().getUsername()
        );
    }

    public InfluencerProfileResponse getInfluencerById(Long id) {

        InfluencerProfile profile = repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Influencer not found"));

        return new InfluencerProfileResponse(
                profile.getId(),
                profile.getInstagramHandle(),
                profile.getFollowers(),
                profile.getNiche(),
                profile.getPricePerPost(),
                profile.getUser().getUsername()
        );
    }

}
