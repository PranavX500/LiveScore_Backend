package com.example.livescore.Service;

import com.example.livescore.Model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TournamentService {

    private final FirebaseService firebaseService;
    private static final String COLLECTION = "tournaments";

    @Autowired
    TeamService teamService;

    public void createTournament(Tournament tournament) throws Exception {

        // auto id (optional but recommended)
        if (tournament.getId() == null || tournament.getId().isEmpty()) {
            tournament.setId(UUID.randomUUID().toString());
        }

        // auto createdAt
        tournament.setCreatedAt(new Date());

        firebaseService.save(COLLECTION, tournament.getId(), tournament);
    }

    public Tournament getTournament(String id) throws Exception {
        return firebaseService.get(COLLECTION, id, Tournament.class);
    }

    public void deleteTournament(String id) throws Exception {
        firebaseService.delete(COLLECTION, id);
    }
    public void registerTeam(String teamId, String tournamentId) throws Exception {

        Tournament tournament = getTournament(tournamentId);
        Team team = teamService.getTeam(teamId);

        if (tournament == null) {
            throw new RuntimeException("Tournament not found");
        }

        if (team == null) {
            throw new RuntimeException("Team not found");
        }

        // 🔒 registration closed
        if (tournament.getRegisteration() == Registeration.CLOSED) {
            throw new RuntimeException("Tournament registration closed");
        }

        // 🔒 team already registered
        if (team.getTournamentId() != null) {
            throw new RuntimeException("Team already registered in a tournament");
        }

        // 🔥 REQUIRED PLAYER RULE
//        Long required = tournament.getRequiredPlayer();
//        if (required != null && team.getCurrentPlayers() < required) {
//            throw new RuntimeException(
//                    "Team does not meet minimum player requirement (" + required + ")"
//            );
//        }

        // increment registered teams
        Long registered = tournament.getRegisteredTeams();
        if (registered == null) registered = 0L;

        registered++;
        tournament.setRegisteredTeams(registered);

        // assign tournament to team
        team.setTournamentId(tournamentId);
        teamService.save(team);
        TournamentTeam tt = TournamentTeam.builder()
                .teamId(team.getId())
                .teamName(team.getName())
                .build();
        firebaseService.saveSub(
                "tournaments",
                tournamentId,
                "teams",
                team.getId(),
                tt
        );

        // 🔥 AUTO CLOSE
        if (registered >= tournament.getTotalTeams()) {
            tournament.setRegisteration(Registeration.CLOSED);
        }

        firebaseService.save("tournaments", tournamentId, tournament);
    }
    public List<Tournament> getAllTournaments() throws Exception {
        return firebaseService.getAll(COLLECTION, Tournament.class);
    }
    public List<Match> getMatches(String tournamentId) throws Exception {

        return firebaseService.getAllSub(
                "tournaments",
                tournamentId,
                "matches",
                Match.class
        );
    }
    public List<TournamentTeam> getRegisteredTeams(String tournamentId) throws Exception {

        Tournament tournament = getTournament(tournamentId);

        if (tournament == null) {
            throw new RuntimeException("Tournament not found");
        }

        return firebaseService.getAllSub(
                "tournaments",
                tournamentId,
                "teams",
                TournamentTeam.class
        );
    }
}

