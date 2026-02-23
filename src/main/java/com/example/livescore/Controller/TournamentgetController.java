package com.example.livescore.Controller;


import com.example.livescore.Model.TournamentTeam;
import com.example.livescore.Service.TournamentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tournament")
@RequiredArgsConstructor
public class TournamentgetController {

    private final TournamentService tournamentService;

    // ✅ GET registered teams of tournament
    @GetMapping("/{tournamentId}/teams")
    public List<TournamentTeam> getRegisteredTeams(
            @PathVariable String tournamentId
    ) throws Exception {

        return tournamentService.getRegisteredTeams(tournamentId);
    }
}