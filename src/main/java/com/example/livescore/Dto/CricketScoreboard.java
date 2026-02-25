package com.example.livescore.Dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class CricketScoreboard {

    private String teamAName;
    private String teamBName;

    private Integer scoreA;
    private Integer scoreB;

    private Double oversA;
    private Double oversB;

    private String winnerTeamId;

    private List<PlayerBattingRow> battingA;
    private List<PlayerBattingRow> battingB;

    private List<PlayerBowlingRow> bowlingA;
    private List<PlayerBowlingRow> bowlingB;
    private String winnerTeamName;
}