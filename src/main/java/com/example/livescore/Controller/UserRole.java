package com.example.livescore.Controller;

import com.example.livescore.Model.Tournament;
import com.example.livescore.Service.TournamentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/get/tournament")
@RequiredArgsConstructor

public class UserRole {

    private final TournamentService tournamentService;

    @GetMapping
    public ResponseEntity<List<Tournament>> getAllTournaments() throws Exception {
        return ResponseEntity.ok(tournamentService.getAllTournaments());
    }
}