package com.example.livescore.Service;

import com.example.livescore.Model.Role;
import com.example.livescore.Model.Team;
import com.example.livescore.Model.Tournament;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final FirebaseService firebaseService;
    private final UserService userService;

    private static final String COLLECTION = "teams";

    public Team createTeam(String creatorUid, Long maxPlayers, String name) throws Exception {

        if (maxPlayers == null || maxPlayers < 1) {
            throw new RuntimeException("maxPlayers must be >= 1");
        }

        Team team = Team.builder()
                .id(UUID.randomUUID().toString())
                .name(name)
                .leaderId(creatorUid)
                .maxPlayers(maxPlayers)
                .currentPlayers(1L)
                .status("OPEN")
                .tournamentId(null)
                .createdAt(Instant.now())
                .build();

        firebaseService.save(COLLECTION, team.getId(), team);

        userService.updateRole(creatorUid, Role.TEAM_LEADER);

        return team;
    }

    public Team getTeam(String teamId) throws Exception {
        return firebaseService.get(COLLECTION, teamId, Team.class);
    }

    public void save(Team team) throws Exception {
        firebaseService.save(COLLECTION, team.getId(), team);
    }

    public void deleteTeam(String teamId) throws Exception {
        firebaseService.delete(COLLECTION, teamId);
    }
}