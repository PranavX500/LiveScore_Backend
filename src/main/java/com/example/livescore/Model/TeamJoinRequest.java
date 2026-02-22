package com.example.livescore.Model;

import lombok.*;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamJoinRequest {

    private String id;
    private String teamId;
    private String userId;
    private Status status;   // PENDING, APPROVED, REJECTED
    private Instant createdAt;
}