package com.example.livescore.Model;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BattingStat {
    private int runs;
    private int balls;
    private int fours;
    private int sixes;
    private Boolean out;
    private Integer innings;

}