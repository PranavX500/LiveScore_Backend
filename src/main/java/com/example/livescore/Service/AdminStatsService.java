package com.example.livescore.Service;

import com.example.livescore.Dto.AdminStats;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminStatsService {

    private final FirebaseService firebaseService;

    public AdminStats getStats() throws Exception {

        List<QueryDocumentSnapshot> userDocs = firebaseService.getFirestore()
                .collection("users")
                .get()
                .get()
                .getDocuments();

        long totalUsers = userDocs.size();
        long totalAdmins = countByField(userDocs, "role", "ADMIN");
        long totalTeamLeaders = countByField(userDocs, "role", "TEAM_LEADER");
        long totalPlayers = countByField(userDocs, "role", "PLAYER");
        long totalNormalUsers = countByField(userDocs, "role", "USER");

        List<QueryDocumentSnapshot> teamDocs = firebaseService.getFirestore()
                .collection("teams")
                .get()
                .get()
                .getDocuments();
        long totalTeams = teamDocs.size();
        long openTeams = countByField(teamDocs, "status", "OPEN");

        List<QueryDocumentSnapshot> tournamentDocs = firebaseService.getFirestore()
                .collection("tournaments")
                .get()
                .get()
                .getDocuments();
        long totalTournaments = tournamentDocs.size();
        long openTournaments = countByField(tournamentDocs, "registeration", "OPEN");

        List<QueryDocumentSnapshot> matchDocs = firebaseService.getFirestore()
                .collectionGroup("matches")
                .get()
                .get()
                .getDocuments();
        long totalMatches = matchDocs.size();
        long upcomingMatches = countByField(matchDocs, "status", "UPCOMING");
        long liveMatches = countByField(matchDocs, "status", "LIVE");
        long completedMatches = countByField(matchDocs, "status", "COMPLETED");
        long drawMatches = countByField(matchDocs, "status", "DRAW");

        List<QueryDocumentSnapshot> requestDocs = firebaseService.getFirestore()
                .collectionGroup("requests")
                .get()
                .get()
                .getDocuments();
        long pendingJoinRequests = countByField(requestDocs, "status", "PENDING");

        List<QueryDocumentSnapshot> notificationDocs = firebaseService.getFirestore()
                .collectionGroup("notifications")
                .get()
                .get()
                .getDocuments();
        long totalNotifications = notificationDocs.size();

        return AdminStats.builder()
                .totalUsers(totalUsers)
                .totalAdmins(totalAdmins)
                .totalTeamLeaders(totalTeamLeaders)
                .totalPlayers(totalPlayers)
                .totalNormalUsers(totalNormalUsers)
                .totalTeams(totalTeams)
                .openTeams(openTeams)
                .totalTournaments(totalTournaments)
                .openTournaments(openTournaments)
                .totalMatches(totalMatches)
                .upcomingMatches(upcomingMatches)
                .liveMatches(liveMatches)
                .completedMatches(completedMatches)
                .drawMatches(drawMatches)
                .pendingJoinRequests(pendingJoinRequests)
                .totalNotifications(totalNotifications)
                .build();
    }

    private long countByField(List<QueryDocumentSnapshot> docs, String field, String expectedValue) {
        return docs.stream()
                .map(QueryDocumentSnapshot::getData)
                .filter(data -> matchesValue(data, field, expectedValue))
                .count();
    }

    private boolean matchesValue(Map<String, Object> data, String field, String expectedValue) {
        if (data == null) {
            return false;
        }
        Object value = data.get(field);
        return value != null && expectedValue.equalsIgnoreCase(String.valueOf(value));
    }
}
