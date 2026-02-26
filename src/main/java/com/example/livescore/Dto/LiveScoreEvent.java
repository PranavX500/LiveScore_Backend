package com.example.livescore.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LiveScoreEvent {

    private String matchId;
    private String teamAName;
    private String teamBName;

    private Integer scoreA;
    private Integer scoreB;
    private Double oversA;
    private Double oversB;
    private Integer wicketsA;
    private Integer wicketsB;

    private List<PlayerBattingRow> battingA;
    private List<PlayerBattingRow> battingB;

    private List<PlayerBowlingRow> bowlingA;
    private List<PlayerBowlingRow> bowlingB;

    private String strikerName;
    private String nonStrikerName;
    private String bowlerName;
    private List<String> teamAPlayers;
    private List<String> teamBPlayers;

    private String status; // LIVE / COMPLETED
}