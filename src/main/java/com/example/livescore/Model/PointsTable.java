package com.example.livescore.Model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointsTable {

    private String teamId;
    private String teamName;

    private Long played;
    private Long won;
    private Long lost;
    private Long runsScored;
    private Double oversFaced;

    private Long runsConceded;
    private Double oversBowled;
    private Double nrr;
    private Long points;
}