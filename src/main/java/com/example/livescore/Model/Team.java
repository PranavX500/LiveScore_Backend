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
    private String leaderId;
    private int maxPlayers;
    private int currentPlayers;
    private String status; // OPEN, FULL, INACTIVE
    private Instant createdAt;
}
