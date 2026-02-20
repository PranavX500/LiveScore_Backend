package com.example.livescore.Service;

import com.example.livescore.Model.Team;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final FirebaseService firebaseService;
    private static final String COLLECTION = "teams";

    public void createTeam(Team team) throws Exception {
        firebaseService.save(COLLECTION, team.getId(), team);
    }

    public Team getTeam(String teamId) throws Exception {
        return firebaseService.get(COLLECTION, teamId, Team.class);
    }

    public void deleteTeam(String teamId) throws Exception {
        firebaseService.delete(COLLECTION, teamId);
    }
}
