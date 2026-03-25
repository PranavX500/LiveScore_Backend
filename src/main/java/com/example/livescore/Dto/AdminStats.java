package com.example.livescore.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminStats {

    private long totalUsers;
    private long totalAdmins;
    private long totalTeamLeaders;
    private long totalPlayers;
    private long totalNormalUsers;

    private long totalTeams;
    private long openTeams;
    private long totalTournaments;
    private long openTournaments;

    private long totalMatches;
    private long upcomingMatches;
    private long liveMatches;
    private long completedMatches;
    private long drawMatches;

    private long pendingJoinRequests;
    private long totalNotifications;
}
