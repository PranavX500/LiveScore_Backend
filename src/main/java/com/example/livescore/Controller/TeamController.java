package com.example.livescore.Controller;


import com.example.livescore.Model.Sports;
import com.example.livescore.Model.Team;
import com.example.livescore.Service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
            @RequestParam Sports sports,
            Authentication authentication) throws Exception {

        String creatorUid = authentication.getName();

        return teamService.createTeam(creatorUid, maxPlayers, name, sports);
    }

    @GetMapping("/teams")

    public List<Team> getAllTeams() throws Exception {
        return teamService.getAllTeams();
    }
    @GetMapping("/get/{teamId}")
    public Team getTeamDetails(@PathVariable String teamId) throws Exception {
        return teamService.getTeam(teamId);
    }
}
