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
public class MatchEvent {

    private String id;
    private String matchId;
    private String teamId;
    private String playerId;
    private String type; // RUN, GOAL, POINT
    private int value;
    private Instant timestamp;
}
