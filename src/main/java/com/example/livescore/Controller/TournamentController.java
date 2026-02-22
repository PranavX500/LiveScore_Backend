package com.example.livescore.Controller;

import com.example.livescore.Model.Tournament;
import com.example.livescore.Service.TournamentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/tournaments")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class TournamentController {

    private final TournamentService tournamentService;

    @PostMapping
    public ResponseEntity<String> createTournament(@RequestBody Tournament tournament) throws Exception {
        tournamentService.createTournament(tournament);
        return ResponseEntity.ok("Tournament created");
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tournament> getTournament(@PathVariable String id) throws Exception {
        return ResponseEntity.ok(tournamentService.getTournament(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTournament(@PathVariable String id) throws Exception {
        tournamentService.deleteTournament(id);
        return ResponseEntity.ok("Deleted");
    }

}