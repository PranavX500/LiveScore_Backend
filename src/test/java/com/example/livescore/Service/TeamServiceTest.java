package com.example.livescore.Service;

import com.example.livescore.Model.Role;
import com.example.livescore.Model.Sports;
import com.example.livescore.Model.Team;
import com.example.livescore.Model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TeamServiceTest {

    @Mock
    private FirebaseService firebaseService;

    @Mock
    private UserService userService;

    @InjectMocks
    private TeamService teamService;

    @Test
    void createTeamSavesTeamAndPromotesCreatorToLeader() throws Exception {
        User user = User.builder().id("user-1").name("Alex").role(Role.USER.name()).build();
        when(userService.getUser("user-1")).thenReturn(user);

        Team result = teamService.createTeam("user-1", 11L, "Warriors", Sports.CRICKET);

        assertNotNull(result.getId());
        assertEquals("Warriors", result.getName());
        assertEquals("user-1", result.getLeaderId());
        assertEquals(Long.valueOf(0), result.getCurrentPlayers());
        assertEquals("OPEN", result.getStatus());
        assertNotNull(result.getCreatedAt());

        verify(firebaseService).save("teams", result.getId(), result);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(firebaseService).save(eq("users"), eq("user-1"), userCaptor.capture());
        assertEquals(Role.TEAM_LEADER.name(), userCaptor.getValue().getRole());
        assertEquals(result.getId(), userCaptor.getValue().getTeamId());
    }

    @Test
    void createTeamRejectsInvalidMaxPlayers() {
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> teamService.createTeam("user-1", 0L, "Warriors", Sports.CRICKET));

        assertEquals("maxPlayers must be >= 1", exception.getMessage());
    }

    @Test
    void getAllTeamsFiltersTeamsWithoutIdsAndSortsNewestFirst() throws Exception {
        Team older = Team.builder().id("team-1").createdAt(java.time.Instant.parse("2024-01-01T00:00:00Z")).build();
        Team missingId = Team.builder().createdAt(java.time.Instant.parse("2024-02-01T00:00:00Z")).build();
        Team newer = Team.builder().id("team-2").createdAt(java.time.Instant.parse("2024-03-01T00:00:00Z")).build();

        when(firebaseService.getAll("teams", Team.class)).thenReturn(List.of(older, missingId, newer));

        List<Team> teams = teamService.getAllTeams();

        assertEquals(2, teams.size());
        assertEquals("team-2", teams.get(0).getId());
        assertEquals("team-1", teams.get(1).getId());
        assertTrue(teams.stream().noneMatch(team -> team.getId() == null));
    }
}
