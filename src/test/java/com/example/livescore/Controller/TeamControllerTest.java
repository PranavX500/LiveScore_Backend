package com.example.livescore.Controller;

import com.example.livescore.Model.Sports;
import com.example.livescore.Model.Team;
import com.example.livescore.Model.TeamMember;
import com.example.livescore.Service.TeamMemberService;
import com.example.livescore.Service.TeamService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TeamControllerTest {

    @Mock
    private TeamService teamService;

    @Mock
    private TeamMemberService teamMemberService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private TeamController controller;

    @Test
    void createTeamUsesAuthenticatedUser() throws Exception {
        Team created = Team.builder()
                .id("team-1")
                .name("Warriors")
                .sports(Sports.CRICKET)
                .leaderId("user-1")
                .maxPlayers(11L)
                .build();

        when(authentication.getName()).thenReturn("user-1");
        when(teamService.createTeam("user-1", 11L, "Warriors", Sports.CRICKET)).thenReturn(created);

        Team result = controller.createTeam("Warriors", 11L, Sports.CRICKET, authentication);

        assertEquals(created, result);
        verify(teamService).createTeam("user-1", 11L, "Warriors", Sports.CRICKET);
    }

    @Test
    void getAllTeamsReturnsServiceResponse() throws Exception {
        List<Team> teams = List.of(Team.builder().id("team-1").build(), Team.builder().id("team-2").build());
        when(teamService.getAllTeams()).thenReturn(teams);

        List<Team> result = controller.getAllTeams();

        assertEquals(teams, result);
        verify(teamService).getAllTeams();
    }

    @Test
    void getTeamMembersReturnsMembersForTeam() throws Exception {
        List<TeamMember> members = List.of(TeamMember.builder().userId("user-1").build());
        when(teamMemberService.getMembersOfTeam("team-1")).thenReturn(members);

        List<TeamMember> result = controller.getTeamMembers("team-1");

        assertEquals(members, result);
        verify(teamMemberService).getMembersOfTeam("team-1");
    }
}
