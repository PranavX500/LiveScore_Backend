package com.example.livescore.Controller;

import com.example.livescore.Dto.CricketScoreboard;
import com.example.livescore.Service.FixtureService;
import com.example.livescore.Service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/score")
@RequiredArgsConstructor
public class ScoreCard {
    private final FixtureService fixtureService;

    @GetMapping("/{tournamentId}/matches/{matchId}/scoreboard")
    public CricketScoreboard getScoreboard(
            @PathVariable String tournamentId,
            @PathVariable String matchId
    ) throws Exception {

        return fixtureService.getCricketScoreboard(
                tournamentId,
                matchId
        );
    }
}
