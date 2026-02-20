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
public class TeamMember {

    private String userId;
    private String role; // LEADER, PLAYER
    private Instant joinedAt;
}
