package com.reelance.service;

import com.reelance.dto.CampaignApplicationResponse;
import com.reelance.entity.*;
import com.reelance.repository.CampaignApplicationRepository;
import com.reelance.repository.CampaignParticipantRepository;
import com.reelance.repository.CampaignRepository;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CampaignApplicationServiceTest {

    private final CampaignApplicationRepository repository = mock(CampaignApplicationRepository.class);
    private final CampaignRepository campaignRepository = mock(CampaignRepository.class);
    private final CampaignParticipantRepository participantRepository = mock(CampaignParticipantRepository.class);
    private final CampaignApplicationService service =
            new CampaignApplicationService(repository, campaignRepository, participantRepository);

    @Test
    void shortlistApplication_updatesStatusWithoutCreatingParticipant() {
        User brand = user(1L, Role.BRAND);
        User influencer = user(2L, Role.INFLUENCER);
        Campaign campaign = campaign(10L, brand, CampaignStatus.OPEN);
        CampaignApplication application = application(99L, campaign, influencer, ApplicationStatus.PENDING);

        when(repository.findById(99L)).thenReturn(Optional.of(application));
        when(repository.save(any(CampaignApplication.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CampaignApplicationResponse response = service.updateStatus(99L, ApplicationStatus.SHORTLISTED, brand);

        assertEquals(ApplicationStatus.SHORTLISTED, response.status());
        assertEquals(ApplicationStatus.SHORTLISTED, application.getStatus());
        verify(participantRepository, never()).save(any(CampaignParticipant.class));
    }

    @Test
    void unauthorizedBrandCannotUpdateApplicationStatus() {
        User brand = user(1L, Role.BRAND);
        User otherBrand = user(3L, Role.BRAND);
        User influencer = user(2L, Role.INFLUENCER);
        Campaign campaign = campaign(10L, brand, CampaignStatus.OPEN);
        CampaignApplication application = application(99L, campaign, influencer, ApplicationStatus.PENDING);

        when(repository.findById(99L)).thenReturn(Optional.of(application));

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> service.updateStatus(99L, ApplicationStatus.SHORTLISTED, otherBrand)
        );

        assertEquals(403, ex.getStatusCode().value());
        assertEquals("Unauthorized", ex.getReason());
    }

    @Test
    void missingApplicationThrowsNotFound() {
        User brand = user(1L, Role.BRAND);
        when(repository.findById(404L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> service.updateStatus(404L, ApplicationStatus.SHORTLISTED, brand)
        );

        assertEquals(404, ex.getStatusCode().value());
        assertEquals("Application not found", ex.getReason());
    }

    @Test
    void shortlistedApplicationCanBeAcceptedOnceAndCreatesParticipant() {
        User brand = user(1L, Role.BRAND);
        User influencer = user(2L, Role.INFLUENCER);
        Campaign campaign = campaign(10L, brand, CampaignStatus.OPEN);
        CampaignApplication application = application(99L, campaign, influencer, ApplicationStatus.SHORTLISTED);

        when(repository.findById(99L)).thenReturn(Optional.of(application));
        when(repository.save(any(CampaignApplication.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(participantRepository.existsByCampaign_IdAndInfluencer_Id(10L, 2L)).thenReturn(false);

        CampaignApplicationResponse response = service.updateStatus(99L, ApplicationStatus.ACCEPTED, brand);

        verify(participantRepository, times(1)).save(any(CampaignParticipant.class));
        assertEquals(ApplicationStatus.ACCEPTED, response.status());
        assertEquals(ApplicationStatus.ACCEPTED, application.getStatus());
    }

    @Test
    void shortlistedApplicationCanBeRejectedWithoutCreatingParticipant() {
        User brand = user(1L, Role.BRAND);
        User influencer = user(2L, Role.INFLUENCER);
        Campaign campaign = campaign(10L, brand, CampaignStatus.OPEN);
        CampaignApplication application = application(99L, campaign, influencer, ApplicationStatus.SHORTLISTED);

        when(repository.findById(99L)).thenReturn(Optional.of(application));
        when(repository.save(any(CampaignApplication.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CampaignApplicationResponse response = service.updateStatus(99L, ApplicationStatus.REJECTED, brand);

        assertEquals(ApplicationStatus.REJECTED, response.status());
        assertEquals(ApplicationStatus.REJECTED, application.getStatus());
        verify(participantRepository, never()).save(any(CampaignParticipant.class));
    }

    @Test
    void closedCampaignRejectsStatusChangeToAccepted() {
        User brand = user(1L, Role.BRAND);
        User influencer = user(2L, Role.INFLUENCER);
        Campaign campaign = campaign(10L, brand, CampaignStatus.CLOSED);
        CampaignApplication application = application(99L, campaign, influencer, ApplicationStatus.PENDING);

        when(repository.findById(99L)).thenReturn(Optional.of(application));

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> service.updateStatus(99L, ApplicationStatus.ACCEPTED, brand)
        );

        assertEquals(400, ex.getStatusCode().value());
        assertTrue(ex.getReason().contains("open"));
    }

    private User user(Long id, Role role) {
        User user = new User();
        user.setId(id);
        user.setRole(role);
        user.setUsername("user-" + id);
        user.setEmail("user-" + id + "@example.com");
        user.setPassword("password");
        user.setCreatedAt(LocalDateTime.now());
        return user;
    }

    private Campaign campaign(Long id, User brand, CampaignStatus status) {
        Campaign campaign = new Campaign();
        campaign.setId(id);
        campaign.setBrand(brand);
        campaign.setStatus(status);
        campaign.setTitle("Campaign " + id);
        campaign.setBudget(1000L);
        campaign.setDescription("Campaign description");
        campaign.setNiche("Fashion");
        campaign.setCreatedAt(LocalDateTime.now());
        return campaign;
    }

    private CampaignApplication application(Long id, Campaign campaign, User influencer, ApplicationStatus status) {
        CampaignApplication app = new CampaignApplication();
        app.setId(id);
        app.setCampaign(campaign);
        app.setInfluencer(influencer);
        app.setProposal("proposal");
        app.setExpectedPrice(250L);
        app.setStatus(status);
        app.setCreatedAt(LocalDateTime.now());
        return app;
    }
}
