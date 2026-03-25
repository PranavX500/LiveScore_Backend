package com.example.livescore.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserStats {

    private String userId;
    private String name;
    private String email;

    private long availableTeams;
    private long openTournaments;
    private long upcomingMatches;

    private long pendingTeamRequests;
    private long unreadNotifications;
    private long totalNotifications;
}
