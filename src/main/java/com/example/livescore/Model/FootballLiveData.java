package com.example.livescore.Model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FootballLiveData {

    private Integer minute;
    private Integer second;
    private Integer half;

    private Integer possessionA;
    private Integer possessionB;

    private Integer shotsOnTargetA;
    private Integer shotsOnTargetB;

    private Integer foulsA;
    private Integer foulsB;

    private Integer yellowA;
    private Integer yellowB;

    private Integer redA;
    private Integer redB;
}
