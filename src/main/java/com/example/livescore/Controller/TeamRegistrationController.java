package com.example.livescore.Controller;

import com.example.livescore.Service.TournamentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/team")
@RequiredArgsConstructor
public class TeamRegistrationController {

    private final TournamentService tournamentService;

    /* ---------- REGISTER TEAM TO TOURNAMENT ---------- */
    @PostMapping("/{teamId}/register/{tournamentId}")
    @PreAuthorize("hasRole('TEAM_LEADER')")
    public String registerTeam(
            @PathVariable String teamId,
            @PathVariable String tournamentId) throws Exception {

        tournamentService.registerTeam(teamId, tournamentId);

        return "Team successfully registered to tournament";
    }
}