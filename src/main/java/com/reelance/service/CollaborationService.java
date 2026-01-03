package com.reelance.service;

import com.reelance.dto.CollaborationRequestDto;
import com.reelance.dto.CollaborationResponse;
import com.reelance.entity.*;
import com.reelance.repository.CollaborationRequestRepository;
import com.reelance.repository.InfluencerProfileRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CollaborationService {

    private final CollaborationRequestRepository repository;
    private final InfluencerProfileRepository influencerRepo;

    public CollaborationService(
            CollaborationRequestRepository repository,
            InfluencerProfileRepository influencerRepo) {
        this.repository = repository;
        this.influencerRepo = influencerRepo;
    }

    // BRAND → SEND REQUEST
    public CollaborationResponse sendRequest(
            User brand,
            CollaborationRequestDto dto) {

        InfluencerProfile profile = influencerRepo.findById(dto.getInfluencerProfileId())
                .orElseThrow(() -> new RuntimeException("Influencer not found"));

        boolean exists = repository.existsByBrand_IdAndInfluencerProfile_IdAndStatus(
                brand.getId(),
                profile.getId(),
                CollaborationStatus.PENDING
        );

        if (exists) {
            throw new RuntimeException("Request already sent");
        }

        CollaborationRequest request = repository.save(
                CollaborationRequest.builder()
                        .brand(brand)
                        .influencerProfile(profile)
                        .message(dto.getMessage())
                        .status(CollaborationStatus.PENDING)
                        .createdAt(LocalDateTime.now())
                        .build()
        );

        return mapToResponse(request);
    }

    // INFLUENCER → VIEW REQUESTS
    public List<CollaborationResponse> getInfluencerRequests(User influencer) {

        return repository.findByInfluencerProfile_User_Id(influencer.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // INFLUENCER → ACCEPT / REJECT
    public CollaborationResponse updateStatus(
            Long requestId,
            CollaborationStatus status,
            User influencer) {

        CollaborationRequest request = repository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (!request.getInfluencerProfile().getUser().getId()
                .equals(influencer.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        request.setStatus(status);
        return mapToResponse(repository.save(request));
    }

    private CollaborationResponse mapToResponse(CollaborationRequest r) {
        return new CollaborationResponse(
                r.getId(),
                r.getBrand().getUsername(),
                r.getInfluencerProfile().getUser().getUsername(),
                r.getMessage(),
                r.getStatus()
        );
    }
}
