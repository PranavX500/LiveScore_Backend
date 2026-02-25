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
public class BasketballLiveData {

    private Integer quarter;
    private String timeLeft;

    private Integer foulsA;
    private Integer foulsB;

    private Integer timeoutsA;
    private Integer timeoutsB;

    private List<Integer> quarterScoresA;
    private List<Integer> quarterScoresB;
}