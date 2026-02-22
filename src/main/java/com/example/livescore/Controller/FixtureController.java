package com.example.livescore.Controller;

import com.example.livescore.Model.Match;
import com.example.livescore.Service.FixtureService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tournaments")
@RequiredArgsConstructor
public class FixtureController {

    private final FixtureService fixtureService;

    @PostMapping("/{tournamentId}/fixtures/shuffle")
    public String shuffle(@PathVariable String tournamentId) throws Exception {

        fixtureService.generateFixtures(tournamentId);
        return "Fixtures shuffled and created";
    }

    @GetMapping("/{tournamentId}/matches")
    public List<Match> getMatches(@PathVariable String tournamentId) throws Exception {
        return fixtureService.getMatches(tournamentId);
    }
}