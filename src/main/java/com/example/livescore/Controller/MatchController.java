package com.example.livescore.Controller;

import com.example.livescore.Model.Match;
import com.example.livescore.Model.Sports;
import com.example.livescore.Model.TeamMember;
import com.example.livescore.Service.FixtureService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tournaments")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
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
    // START CRICKET INNINGS
    // ======================================
    @PostMapping("/{tid}/matches/{mid}/cricket/start-innings")
    public void startInnings(
            @PathVariable String tid,
            @PathVariable String mid,
            @RequestParam String strikerId,
            @RequestParam String nonStrikerId,
            @RequestParam String bowlerId
    ) throws Exception {
        fixtureService.startCricketInnings(
                tid,
                mid,
                strikerId,
                nonStrikerId,
                bowlerId
        );
    }

    // ======================================
    // SELECT NEW STRIKER (INNINGS AWARE)
    // ======================================
    @PostMapping("/{tid}/matches/{mid}/cricket/new-striker")
    public void newStriker(
            @PathVariable String tid,
            @PathVariable String mid,
            @RequestParam String strikerId
    ) throws Exception {
        fixtureService.selectNewStriker(
                tid,
                mid,
                strikerId
        );
    }

    // ======================================
    // SELECT NEW BOWLER (INNINGS AWARE)
    // ======================================
    @PostMapping("/{tid}/matches/{mid}/cricket/new-bowler")
    public void newBowler(
            @PathVariable String tid,
            @PathVariable String mid,
            @RequestParam String bowlerId
    ) throws Exception {
        fixtureService.selectNewBowler(
                tid,
                mid,
                bowlerId
        );
    }

    // ======================================
    // AVAILABLE BATSMEN (FILTERED)
    // ======================================
    @GetMapping("/{tid}/matches/{mid}/cricket/available-batsmen")
    public List<TeamMember> availableBatsmen(
            @PathVariable String tid,
            @PathVariable String mid
    ) throws Exception {
        return fixtureService.getAvailableBatsmen(
                tid,
                mid
        );
    }

    // ======================================
    // GET MATCH (FOR FLUTTER)
    // ======================================
    @GetMapping("/{tid}/matches/{mid}")
    public Match getMatch(
            @PathVariable String tid,
            @PathVariable String mid
    ) throws Exception {
        return fixtureService.getMatchById(
                tid,
                mid
        );
    }

    // ======================================
    // GET ALL MATCHES
    // ======================================
    @GetMapping("/{tournamentId}/matches")
    public List<Match> getMatches(
            @PathVariable String tournamentId
    ) throws Exception {
        return fixtureService.getMatches(tournamentId);
    }

    // ======================================
    // FOOTBALL GOAL
    // ======================================
    @PostMapping("/{tournamentId}/matches/{matchId}/football/goal")
    public String footballGoal(
            @PathVariable String tournamentId,
            @PathVariable String matchId,
            @RequestParam String team
    ) throws Exception {
        fixtureService.footballGoal(
                tournamentId,
                matchId,
                team
        );
        return "Goal updated";
    }
}