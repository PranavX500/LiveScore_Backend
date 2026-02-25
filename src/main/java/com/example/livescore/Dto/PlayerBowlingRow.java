package com.example.livescore.Dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlayerBowlingRow {

    private String playerId;
    private Integer balls;
    private Integer runs;
    private Integer wickets;
    private String playerName;

}