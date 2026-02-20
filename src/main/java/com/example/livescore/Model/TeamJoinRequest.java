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
public class TeamJoinRequest {

    private String id;
    private String userId;
    private String status; // PENDING, APPROVED, REJECTED
    private Instant createdAt;
}
