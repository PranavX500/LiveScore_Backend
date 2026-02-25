package com.example.livescore.Model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Match {

    private String id;
    private String tournamentId;

    private Sports sport;

    private String teamAId;
    private String teamAName;
    private String teamBId;
    private String teamBName;

    private Long round;

    private int scoreA;
    private int scoreB;

    private String status;

    private Date scheduledAt;   // ✅ FIX
    private Date updatedAt;     // ✅ FIX

    private Integer totalOvers;

    private Object liveData;

    private Map<String, BattingStat> battingStats;
    private Map<String, BowlingStat> bowlingStats;

    private String winnerTeamId;
}