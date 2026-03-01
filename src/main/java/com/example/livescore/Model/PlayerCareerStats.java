package com.example.livescore.Model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlayerCareerStats {

    private int matches;
    private int runs;
    private int ballsFaced;
    private int fours;
    private int sixes;

    private int ballsBowled;
    private int runsConceded;
    private int wickets;

}