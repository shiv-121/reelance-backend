package com.reelance.controller;

import com.reelance.dto.CampaignParticipantResponse;
import com.reelance.dto.CampaignRequest;
import com.reelance.dto.CampaignResponse;
import com.reelance.dto.CampaignWorkspaceResponse;
import com.reelance.entity.CampaignStatus;
import com.reelance.entity.User;
import com.reelance.service.CampaignParticipantService;
import com.reelance.service.CampaignService;
import com.reelance.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/campaigns")
@Tag(name = "Campaigns", description = "Campaign management endpoints")
@SecurityRequirement(name = "Bearer")
public class CampaignController {

    private final CampaignService service;
    private final UserService userService;
    private final CampaignParticipantService participantService;

    public CampaignController(
            CampaignService service,
            UserService userService,
            CampaignParticipantService participantService) {

        this.service = service;
        this.userService = userService;
        this.participantService = participantService;
    }

    // BRAND → CREATE CAMPAIGN
    @PostMapping
    @Operation(summary = "Create Campaign", description = "Brand creates a new campaign")
    public CampaignResponse createCampaign(
            Authentication authentication,
            @Valid @RequestBody CampaignRequest request) {

        User brand =
                userService.findByEmail(
                        authentication.getName());

        return service.createCampaign(
                brand,
                request);
    }

    // BRAND → MY CAMPAIGNS (LEGACY - returns List)
    @GetMapping("/brand")
    @Operation(summary = "Get My Campaigns", description = "Retrieve all campaigns created by the logged-in brand")
    public List<CampaignResponse> myCampaigns(
            Authentication authentication) {

        User brand =
                userService.findByEmail(
                        authentication.getName());

        return service.getBrandCampaigns(
                brand);
    }

    // BRAND → CAMPAIGN WORKSPACE WITH SEARCH, FILTER, SORT, PAGINATION
    @GetMapping("/brand/workspace")
    @Operation(summary = "Campaign Workspace",
            description = "Get brand campaigns with search, filters, sorting, and pagination. Supports filtering by status, niche, budget range, and deadline range.")
    public CampaignWorkspaceResponse workspaceCampaigns(
            Authentication authentication,
            @Parameter(description = "Search in title and description (case-insensitive)")
            @RequestParam(required = false) String search,
            @Parameter(description = "Filter by campaign status")
            @RequestParam(required = false) CampaignStatus status,
            @Parameter(description = "Filter by niche")
            @RequestParam(required = false) String niche,
            @Parameter(description = "Minimum budget")
            @RequestParam(required = false) Long budgetMin,
            @Parameter(description = "Maximum budget")
            @RequestParam(required = false) Long budgetMax,
            @Parameter(description = "Deadline must be on or before this date (YYYY-MM-DD)")
            @RequestParam(required = false) LocalDate deadlineBefore,
            @Parameter(description = "Deadline must be on or after this date (YYYY-MM-DD)")
            @RequestParam(required = false) LocalDate deadlineAfter,
            @Parameter(description = "Page number (0-indexed)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field and direction (e.g., 'createdAt,desc' or 'budget,asc')")
            @RequestParam(defaultValue = "createdAt,desc") String sort) {

        User brand =
                userService.findByEmail(
                        authentication.getName());

        // Parse sort parameter
        Pageable pageable = parseSortAndCreatePageable(sort, page, size);

        return service.getBrandCampaignsWorkspace(
                brand,
                search,
                status,
                niche,
                budgetMin,
                budgetMax,
                deadlineBefore,
                deadlineAfter,
                pageable);
    }

    // INFLUENCER → BROWSE CAMPAIGNS
    @GetMapping
    @Operation(summary = "Browse Open Campaigns", description = "Get all open campaigns for browsing")
    public List<CampaignResponse> browseCampaigns() {

        return service.getOpenCampaigns();
    }

    // BRAND → CLOSE CAMPAIGN
    @PatchMapping("/{id}/close")
    @Operation(summary = "Close Campaign", description = "Brand closes a campaign (changes status to CLOSED)")
    public CampaignResponse closeCampaign(
            @PathVariable Long id,
            Authentication authentication) {

        User brand =
                userService.findByEmail(
                        authentication.getName());

        return service.closeCampaign(
                id,
                brand);
    }

    // BRAND → COMPLETE CAMPAIGN
    @PatchMapping("/{id}/complete")
    @Operation(summary = "Complete Campaign", description = "Brand completes a campaign (changes status to COMPLETED)")
    public CampaignResponse completeCampaign(
            @PathVariable Long id,
            Authentication authentication) {

        User brand =
                userService.findByEmail(
                        authentication.getName());

        return service.completeCampaign(
                id,
                brand);
    }

    // BRAND → VIEW PARTICIPANTS
    @GetMapping("/{campaignId}/participants")
    @Operation(summary = "Get Campaign Participants", description = "View all participants (accepted influencers) for a campaign")
    public List<CampaignParticipantResponse>
    participants(
            @PathVariable Long campaignId) {

        return participantService
                .getCampaignParticipants(
                        campaignId);
    }

    // Helper method to parse sort parameter and create Pageable
    private Pageable parseSortAndCreatePageable(String sort, int page, int size) {
        try {
            String[] sortParts = sort.split(",");
            if (sortParts.length != 2) {
                throw new IllegalArgumentException("Invalid sort format");
            }

            String sortField = sortParts[0].trim();
            String sortDirection = sortParts[1].trim().toLowerCase();

            // Validate sort field
            if (!isValidSortField(sortField)) {
                throw new IllegalArgumentException("Invalid sort field: " + sortField);
            }

            Sort.Direction direction = sortDirection.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
            return PageRequest.of(page, size, Sort.by(direction, sortField));
        } catch (Exception e) {
            // Default sort
            return PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        }
    }

    // Validate sort fields to prevent SQL injection
    private boolean isValidSortField(String field) {
        return switch (field) {
            case "createdAt", "title", "budget", "deadline", "status", "niche" -> true;
            default -> false;
        };
    }
}

