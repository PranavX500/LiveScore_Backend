package com.example.livescore.Controller;



import com.example.livescore.Model.TeamJoinRequest;
import com.example.livescore.Service.TeamJoinRequestService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/teams/{teamId}/requests")
@RequiredArgsConstructor
public class TeamJoinRequestController {

    private final TeamJoinRequestService requestService;

    /* ---------- APPLY TO TEAM (CREATE REQUEST) ---------- */
    @PostMapping
    public ResponseEntity<?> applyToTeam(
            @PathVariable String teamId,
            Authentication authentication) {

        try {
            // authenticated user from Firebase filter

            String userId = authentication.getName();
            // build request object
            TeamJoinRequest req = TeamJoinRequest.builder()
                    .id(UUID.randomUUID().toString())
                    .userId(userId)
                    .status("PENDING")
                    .createdAt(Instant.now())
                    .build();

            requestService.createRequest(teamId, req);

            return ResponseEntity.ok("Request submitted");

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error creating request: " + e.getMessage());
        }
    }
}