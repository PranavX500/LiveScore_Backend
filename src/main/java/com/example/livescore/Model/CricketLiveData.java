package com.example.livescore.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CricketLiveData {

    // ==============================
    // CURRENT PLAYERS
    // ==============================
    private String strikerId;
    private String nonStrikerId;
    private String bowlerId;

    // ==============================
    // OVERS / BALLS
    // ==============================
    private Double oversA;
    private Double oversB;

    private Integer ballsA;      // ⭐ total balls team A
    private Integer ballsB;      // ⭐ total balls team B
    private Integer ballsInOver; // ⭐ 0-5 counter

    // ==============================
    // WICKETS
    // ==============================
    private Integer wicketsA;
    private Integer wicketsB;

    // ==============================
    // RATES / TARGET
    // ==============================
    private Double runRateA;
    private Double requiredRateB;
    private Integer target;

    // ==============================
    // OVER TRACKING
    // ==============================
    private List<String> thisOver;
    private String lastBall;

    // ==============================
    // MATCH STATE
    // ==============================
    private Integer innings;

    // ==============================
    // SCORE (optional mirror)
    // ==============================
    private Integer scoreA;
    private Integer scoreB;

    // ==============================
    // PLAYER STATS
    // ==============================
    @Builder.Default
    private Map<String, BattingStat> battingStats = new HashMap<>();

    @Builder.Default
    private Map<String, BowlingStat> bowlingStats = new HashMap<>();
}