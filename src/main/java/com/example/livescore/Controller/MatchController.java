package com.example.livescore.Controller;

import com.example.livescore.Model.Match;
import com.example.livescore.Model.Sports;
import com.example.livescore.Service.FixtureService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tournaments")
@RequiredArgsConstructor
public class MatchController {

    private final FixtureService fixtureService;

    // ======================================
    // GENERATE FIXTURES (SHUFFLE)
    // ======================================
    @PostMapping("/{tournamentId}/fixtures/shuffle")
    public String shuffle(
            @PathVariable String tournamentId,
            @RequestParam Sports sport
    ) throws Exception {

        fixtureService.generateFixtures(tournamentId, sport);
        return "Fixtures shuffled and created for " + sport;
    }

    // ======================================
    // START MATCH
    // ======================================
    @PostMapping("/{tournamentId}/matches/{matchId}/start")
    public String startMatch(
            @PathVariable String tournamentId,
            @PathVariable String matchId
    ) throws Exception {

        fixtureService.startMatch(tournamentId, matchId);
        return "Match started";
    }

    // ======================================
    // CRICKET BALL UPDATE
    // ======================================
    @PostMapping("/{tournamentId}/matches/{matchId}/cricket/ball")
    public String cricketBall(
            @PathVariable String tournamentId,
            @PathVariable String matchId,
            @RequestParam int runs,
            @RequestParam boolean wicket
    ) throws Exception {

        fixtureService.updateCricketBall(
                tournamentId,
                matchId,
                runs,
                wicket
        );

        return "Ball updated";
    }

    // ======================================
    // FOOTBALL GOAL
    // ======================================
    @PostMapping("/{tournamentId}/matches/{matchId}/football/goal")
    public String footballGoal(
            @PathVariable String tournamentId,
            @PathVariable String matchId,
            @RequestParam String team   // A or B
    ) throws Exception {

        fixtureService.footballGoal(
                tournamentId,
                matchId,
                team
        );

        return "Goal updated";
    }

    // ======================================
    // GET MATCHES
    // ======================================
    @GetMapping("/{tournamentId}/matches")
    public List<Match> getMatches(
            @PathVariable String tournamentId
    ) throws Exception {

        return fixtureService.getMatches(tournamentId);
    }

    @PostMapping("/{tid}/matches/{mid}/cricket/start-innings")
    public void startInnings(
            @PathVariable String tid,
            @PathVariable String mid,
            @RequestParam String strikerId,
            @RequestParam String nonStrikerId,
            @RequestParam String bowlerId
    ) throws Exception {

        fixtureService.startCricketInnings(
                tid, mid, strikerId, nonStrikerId, bowlerId
        );

    }
}