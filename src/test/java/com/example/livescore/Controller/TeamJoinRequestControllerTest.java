package com.example.livescore.Controller;

import com.example.livescore.Model.Status;
import com.example.livescore.Model.TeamJoinRequest;
import com.example.livescore.Service.TeamJoinRequestService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TeamJoinRequestControllerTest {

    @Mock
    private TeamJoinRequestService service;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private TeamJoinRequestController controller;

    @Test
    void applyToTeamCreatesPendingRequestForAuthenticatedUser() throws Exception {
        when(authentication.getName()).thenReturn("user-1");

        ResponseEntity<?> response = controller.applyToTeam("team-1", authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Request submitted", response.getBody());

        ArgumentCaptor<TeamJoinRequest> captor = ArgumentCaptor.forClass(TeamJoinRequest.class);
        verify(service).createRequest(eq("team-1"), captor.capture());

        TeamJoinRequest saved = captor.getValue();
        assertNotNull(saved.getId());
        assertEquals("team-1", saved.getTeamId());
        assertEquals("user-1", saved.getUserId());
        assertEquals(Status.PENDING, saved.getStatus());
        assertNotNull(saved.getCreatedAt());
    }

    @Test
    void applyToTeamReturnsInternalServerErrorWhenServiceFails() throws Exception {
        when(authentication.getName()).thenReturn("user-1");
        org.mockito.Mockito.doThrow(new RuntimeException("Team is full"))
                .when(service).createRequest(eq("team-1"), org.mockito.ArgumentMatchers.any(TeamJoinRequest.class));

        ResponseEntity<?> response = controller.applyToTeam("team-1", authentication);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(String.valueOf(response.getBody()).contains("Team is full"));
    }

    @Test
    void approveDelegatesToServiceAndReturnsMessage() throws Exception {
        when(authentication.getName()).thenReturn("leader-1");

        ResponseEntity<String> response = controller.approve("team-1", "req-1", authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Request approved", response.getBody());
        verify(service).approveRequest("team-1", "req-1", "leader-1");
    }

    @Test
    void rejectDelegatesToServiceAndReturnsMessage() throws Exception {
        when(authentication.getName()).thenReturn("leader-1");

        String response = controller.reject("team-1", "req-1", authentication);

        assertEquals("Request rejected", response);
        verify(service).rejectRequest("team-1", "req-1", "leader-1");
    }

    @Test
    void getRequestsReturnsRequestsForTeam() throws Exception {
        List<TeamJoinRequest> requests = List.of(TeamJoinRequest.builder().id("req-1").build());
        when(service.getRequestsForTeam("team-1")).thenReturn(requests);

        ResponseEntity<?> response = controller.getRequests("team-1", authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(requests, response.getBody());
        verify(service).getRequestsForTeam("team-1");
    }
}
