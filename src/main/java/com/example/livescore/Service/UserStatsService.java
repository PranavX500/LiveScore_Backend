package com.example.livescore.Service;

import com.example.livescore.Dto.UserStats;
import com.example.livescore.Model.Team;
import com.example.livescore.Model.TeamJoinRequest;
import com.example.livescore.Model.Tournament;
import com.example.livescore.Model.User;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserStatsService {

    private final UserService userService;
    private final TeamService teamService;
    private final TournamentService tournamentService;
    private final FixtureService fixtureService;
    private final FirebaseService firebaseService;

    public UserStats getStats(String userId) throws Exception {

        User user = userService.getUser(userId);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        List<Team> teams = teamService.getAllTeams();
        long availableTeams = teams.stream()
                .filter(team -> team != null && "OPEN".equalsIgnoreCase(team.getStatus()))
                .count();

        List<Tournament> tournaments = tournamentService.getAllTournaments();
        long openTournaments = tournaments.stream()
                .filter(tournament -> tournament != null
                        && tournament.getRegisteration() != null
                        && "OPEN".equalsIgnoreCase(tournament.getRegisteration().name()))
                .count();

        long upcomingMatches = fixtureService.getUpcomingMatches().size();

        List<QueryDocumentSnapshot> requestDocs = firebaseService.getFirestore()
                .collectionGroup("requests")
                .get()
                .get()
                .getDocuments();

        long pendingTeamRequests = requestDocs.stream()
                .map(doc -> doc.toObject(TeamJoinRequest.class))
                .filter(request -> request != null
                        && userId.equals(request.getUserId())
                        && request.getStatus() != null
                        && "PENDING".equalsIgnoreCase(request.getStatus().name()))
                .count();

        List<QueryDocumentSnapshot> notificationDocs = firebaseService.getFirestore()
                .collection("users")
                .document(userId)
                .collection("notifications")
                .get()
                .get()
                .getDocuments();

        long totalNotifications = notificationDocs.size();
        long unreadNotifications = notificationDocs.stream()
                .filter(doc -> !Boolean.TRUE.equals(doc.getBoolean("read")))
                .count();

        return UserStats.builder()
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .availableTeams(availableTeams)
                .openTournaments(openTournaments)
                .upcomingMatches(upcomingMatches)
                .pendingTeamRequests(pendingTeamRequests)
                .unreadNotifications(unreadNotifications)
                .totalNotifications(totalNotifications)
                .build();
    }
}
