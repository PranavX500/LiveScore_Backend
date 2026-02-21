package com.example.livescore.Controller;


import com.example.livescore.Model.Team;
import com.example.livescore.Service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/team")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;

    /* ---------- CREATE TEAM ---------- */
    @PostMapping("/create")
    @PreAuthorize("hasRole('USER')")
    public Team createTeam(
            @RequestParam String name,
            @RequestParam Long maxPlayers,
            Authentication authentication) throws Exception {

        String creatorUid = authentication.getName(); // Firebase UID

        return teamService.createTeam(creatorUid, maxPlayers, name);
    }
}