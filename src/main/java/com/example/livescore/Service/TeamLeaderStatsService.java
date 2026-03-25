package com.example.livescore.Service;

import com.example.livescore.Dto.TeamLeaderStats;
import com.example.livescore.Model.Match;
import com.example.livescore.Model.Status;
import com.example.livescore.Model.Team;
import com.example.livescore.Model.TeamJoinRequest;
import com.example.livescore.Model.Tournament;
import com.example.livescore.Model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamLeaderStatsService {

    private final UserService userService;
    private final TeamService teamService;
    private final TeamJoinRequestService teamJoinRequestService;
    private final TournamentService tournamentService;

    public TeamLeaderStats getStats(String leaderUid) throws Exception {

        User leader = userService.getUser(leaderUid);
        if (leader == null) {
            throw new RuntimeException("User not found");
        }
        if (leader.getTeamId() == null || leader.getTeamId().isBlank()) {
            throw new RuntimeException("Team leader has no team");
        }

        Team team = teamService.getTeam(leader.getTeamId());
        if (team == null) {
            throw new RuntimeException("Team not found");
        }
        if (!leaderUid.equals(team.getLeaderId())) {
            throw new RuntimeException("Not team leader");
        }

        long currentPlayers = team.getCurrentPlayers() == null ? 0L : team.getCurrentPlayers();
        long maxPlayers = team.getMaxPlayers() == null ? 0L : team.getMaxPlayers();
        long availableSlots = Math.max(maxPlayers - currentPlayers, 0L);

        List<TeamJoinRequest> requests = teamJoinRequestService.getRequestsForTeam(team.getId());
        long pendingJoinRequests = requests.stream()
                .filter(request -> request != null && request.getStatus() == Status.PENDING)
                .count();

        Tournament tournament = null;
        List<Match> matches = Collections.emptyList();
        if (team.getTournamentId() != null && !team.getTournamentId().isBlank()) {
            tournament = tournamentService.getTournament(team.getTournamentId());
            matches = tournamentService.getMatches(team.getTournamentId());
        }

        long totalMatches = 0L;
        long upcomingMatches = 0L;
        long liveMatches = 0L;
        long completedMatches = 0L;
        long wins = 0L;
        long losses = 0L;
        long draws = 0L;

        for (Match match : matches) {
            if (match == null) {
                continue;
            }

            boolean involvesTeam = team.getId().equals(match.getTeamAId())
                    || team.getId().equals(match.getTeamBId());
            if (!involvesTeam) {
                continue;
            }

            totalMatches++;
            String status = match.getStatus();

            if ("UPCOMING".equalsIgnoreCase(status)) {
                upcomingMatches++;
                continue;
            }
            if ("LIVE".equalsIgnoreCase(status)) {
                liveMatches++;
                continue;
            }

            if ("COMPLETED".equalsIgnoreCase(status) || "DRAW".equalsIgnoreCase(status)) {
                completedMatches++;
            }

            if ("DRAW".equalsIgnoreCase(status)) {
                draws++;
            } else if (team.getId().equals(match.getWinnerTeamId())) {
                wins++;
            } else if ("COMPLETED".equalsIgnoreCase(status)) {
                losses++;
            }
        }

        return TeamLeaderStats.builder()
                .teamId(team.getId())
                .teamName(team.getName())
                .sport(team.getSports())
                .teamStatus(team.getStatus())
                .currentPlayers(currentPlayers)
                .maxPlayers(maxPlayers)
                .availableSlots(availableSlots)
                .pendingJoinRequests(pendingJoinRequests)
                .registeredToTournament(team.getTournamentId() != null && !team.getTournamentId().isBlank())
                .tournamentId(team.getTournamentId())
                .tournamentName(tournament == null ? null : tournament.getName())
                .totalMatches(totalMatches)
                .upcomingMatches(upcomingMatches)
                .liveMatches(liveMatches)
                .completedMatches(completedMatches)
                .wins(wins)
                .losses(losses)
                .draws(draws)
                .build();
    }
}
