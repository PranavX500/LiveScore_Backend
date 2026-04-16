package com.example.livescore.Service;

import com.example.livescore.Model.Role;
import com.example.livescore.Model.Status;
import com.example.livescore.Model.Team;
import com.example.livescore.Model.TeamJoinRequest;
import com.example.livescore.Model.TeamMember;
import com.example.livescore.Model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TeamJoinRequestServiceTest {

    @Mock
    private TeamMemberService teamMemberService;

    @Mock
    private FirebaseService firebaseService;

    @Mock
    private TeamService teamService;

    @Mock
    private UserService userService;

    @InjectMocks
    private TeamJoinRequestService teamJoinRequestService;

    @Test
    void createRequestSavesPendingRequestWhenTeamHasCapacity() throws Exception {
        Team team = Team.builder().id("team-1").currentPlayers(5L).maxPlayers(11L).build();
        TeamJoinRequest request = TeamJoinRequest.builder()
                .id("req-1")
                .teamId("team-1")
                .userId("user-1")
                .status(Status.PENDING)
                .createdAt(Instant.now())
                .build();
        when(teamService.getTeam("team-1")).thenReturn(team);

        TeamJoinRequest saved = teamJoinRequestService.createRequest("team-1", request);

        assertSame(request, saved);
        verify(firebaseService).saveSub("teams", "team-1", "requests", "req-1", request);
    }

    @Test
    void createRequestRejectsWhenTeamIsFull() throws Exception {
        Team team = Team.builder().id("team-1").currentPlayers(11L).maxPlayers(11L).build();
        when(teamService.getTeam("team-1")).thenReturn(team);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> teamJoinRequestService.createRequest("team-1", TeamJoinRequest.builder().id("req-1").build()));

        assertEquals("Team is full", exception.getMessage());
        verify(firebaseService, never()).saveSub(eq("teams"), eq("team-1"), eq("requests"), eq("req-1"), org.mockito.ArgumentMatchers.any());
    }

    @Test
    void approveRequestAddsMemberUpdatesCountsRoleAndRequestStatus() throws Exception {
        Team team = Team.builder()
                .id("team-1")
                .leaderId("leader-1")
                .currentPlayers(5L)
                .maxPlayers(11L)
                .build();
        TeamJoinRequest request = TeamJoinRequest.builder()
                .id("req-1")
                .userId("user-1")
                .status(Status.PENDING)
                .build();
        User user = User.builder().id("user-1").name("Player One").build();

        when(teamService.getTeam("team-1")).thenReturn(team);
        when(firebaseService.getSub("teams", "team-1", "requests", "req-1", TeamJoinRequest.class)).thenReturn(request);
        when(firebaseService.getSub("teams", "team-1", "members", "user-1", TeamMember.class)).thenReturn(null);
        when(userService.getUser("user-1")).thenReturn(user);

        teamJoinRequestService.approveRequest("team-1", "req-1", "leader-1");

        ArgumentCaptor<TeamMember> memberCaptor = ArgumentCaptor.forClass(TeamMember.class);
        verify(teamMemberService).addMember(eq("team-1"), memberCaptor.capture());
        assertEquals("user-1", memberCaptor.getValue().getUserId());
        assertEquals("Player One", memberCaptor.getValue().getName());
        assertEquals("PLAYER", memberCaptor.getValue().getRole());

        assertEquals(Long.valueOf(6), team.getCurrentPlayers());
        verify(teamService).save(team);
        verify(userService).updateRole("user-1", Role.PLAYER);

        assertEquals(Status.APPROVED, request.getStatus());
        verify(firebaseService).saveSub("teams", "team-1", "requests", "req-1", request);
    }

    @Test
    void approveRequestRejectsWhenCallerIsNotLeader() throws Exception {
        Team team = Team.builder().id("team-1").leaderId("leader-1").currentPlayers(1L).maxPlayers(11L).build();
        when(teamService.getTeam("team-1")).thenReturn(team);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> teamJoinRequestService.approveRequest("team-1", "req-1", "other-user"));

        assertEquals("Not team leader", exception.getMessage());
        verify(teamMemberService, never()).addMember(eq("team-1"), org.mockito.ArgumentMatchers.any());
    }

    @Test
    void getRequestsForTeamReturnsStoredRequests() throws Exception {
        Team team = Team.builder().id("team-1").build();
        List<TeamJoinRequest> requests = List.of(TeamJoinRequest.builder().id("req-1").build());
        when(teamService.getTeam("team-1")).thenReturn(team);
        when(firebaseService.getAllSub("teams", "team-1", "requests", TeamJoinRequest.class)).thenReturn(requests);

        List<TeamJoinRequest> result = teamJoinRequestService.getRequestsForTeam("team-1");

        assertSame(requests, result);
        verify(firebaseService).getAllSub("teams", "team-1", "requests", TeamJoinRequest.class);
    }
}
