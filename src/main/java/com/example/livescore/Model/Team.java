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
public class Team {

    private String id;
    private String name;
    private Sports sports;
    private String leaderId;
    private Long maxPlayers;
    private Long currentPlayers;
    private String status;
    private String tournamentId;
    private Instant createdAt;
}
