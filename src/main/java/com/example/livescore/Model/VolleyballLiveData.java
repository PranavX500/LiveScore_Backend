package com.example.livescore.Model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VolleyballLiveData {

    private Integer currentSet;

    private Integer setsWonA;
    private Integer setsWonB;

    private List<Integer> setScoresA;
    private List<Integer> setScoresB;

    private Integer timeoutsA;
    private Integer timeoutsB;
}