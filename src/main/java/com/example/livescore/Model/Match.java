package com.example.livescore.Model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Match {

    private String id;
    private String tournamentId;
    private String teamAId;
    private String teamBId;
    private int scoreA;
    private int scoreB;
    private String status; // UPCOMING, LIVE, COMPLETED
    private Instant scheduledAt;
}
