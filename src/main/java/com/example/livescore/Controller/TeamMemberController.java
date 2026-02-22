package com.example.livescore.Controller;

import com.example.livescore.Model.TeamMember;
import com.example.livescore.Service.FirebaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/teams")
@RequiredArgsConstructor
public class TeamMemberController {

    private final FirebaseService firebaseService;

    @GetMapping("/{teamId}/members")
    @PreAuthorize("hasAnyRole('TEAM_LEADER','PLAYER')")
    public List<TeamMember> getMembers(@PathVariable String teamId) throws Exception {

        return firebaseService.getAllSub(
                "teams",
                teamId,
                "members",
                TeamMember.class
        );
    }
}