package com.example.livescore.Dto;

import com.example.livescore.Model.Sports;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamLeaderStats {

    private String teamId;
    private String teamName;
    private Sports sport;
    private String teamStatus;

    private long currentPlayers;
    private long maxPlayers;
    private long availableSlots;
    private long pendingJoinRequests;

    private boolean registeredToTournament;
    private String tournamentId;
    private String tournamentName;

    private long totalMatches;
    private long upcomingMatches;
    private long liveMatches;
    private long completedMatches;
    private long wins;
    private long losses;
    private long draws;
}
