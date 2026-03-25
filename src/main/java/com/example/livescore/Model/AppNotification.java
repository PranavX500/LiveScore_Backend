package com.example.livescore.Model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppNotification {

    private String id;
    private String userId;
    private String title;
    private String body;
    private String type;
    private String tournamentId;
    private String matchId;
    private Boolean read;
    private Date createdAt;
}
