package com.example.livescore.Dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlayerBattingRow {

    private String playerId;
    private Integer runs;
    private Integer balls;
    private Integer fours;
    private Integer sixes;
    private Boolean out;
    private String playerName;
}