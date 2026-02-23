package com.example.livescore.Service;

import com.example.livescore.Model.Match;
import com.example.livescore.Model.TournamentTeam;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
@Service
@RequiredArgsConstructor
@Slf4j
public class FixtureService {

    private final FirebaseService firebaseService;

    public void generateFixtures(String tournamentId) throws Exception {

        log.info("SHUFFLE tournamentId={}", tournamentId);

        List<TournamentTeam> teams =
                firebaseService.getAllSub(
                        "tournaments",
                        tournamentId,
                        "teams",
                        TournamentTeam.class
                );

        log.info("TEAM COUNT={}", teams.size());

        if (teams.size() < 2)
            throw new RuntimeException("Not enough teams");

        if (teams.size() % 2 != 0)
            throw new RuntimeException("Teams must be even");

        Collections.shuffle(teams);

        for (int i = 0; i < teams.size(); i += 2) {

            TournamentTeam a = teams.get(i);
            TournamentTeam b = teams.get(i + 1);

            Match match = Match.builder()
                    .id(UUID.randomUUID().toString())
                    .tournamentId(tournamentId)
                    .teamAId(a.getTeamId())
                    .teamAName(a.getTeamName())
                    .teamBId(b.getTeamId())
                    .teamBName(b.getTeamName())
                    .round(1L)
                    .scoreA(0)
                    .scoreB(0)
                    .status("UPCOMING")
                    .scheduledAt(Instant.now())
                    .build();

            firebaseService.saveSub(
                    "tournaments",
                    tournamentId,
                    "matches",
                    match.getId(),
                    match
            );
        }
    }
    public List<Match> getMatches(String tournamentId) throws Exception {

        return firebaseService.getAllSub(
                "tournaments",
                tournamentId,
                "matches",
                Match.class
        );
    }
}