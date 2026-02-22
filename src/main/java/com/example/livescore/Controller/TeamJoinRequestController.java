package com.example.livescore.Controller;

import com.example.livescore.Model.Status;
import com.example.livescore.Model.TeamJoinRequest;
import com.example.livescore.Service.TeamJoinRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/teams/{teamId}/requests")
@RequiredArgsConstructor
public class TeamJoinRequestController {

    private final TeamJoinRequestService service;

    /* ---------- APPLY TO TEAM ---------- */
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> applyToTeam(
            @PathVariable String teamId,
            Authentication authentication) {

        try {
            String userId = authentication.getName();

            TeamJoinRequest req = TeamJoinRequest.builder()
                    .id(UUID.randomUUID().toString())
                    .teamId(teamId)                 // ✅ important
                    .userId(userId)
                    .status(Status.PENDING)
                    .createdAt(Instant.now())
                    .build();

            service.createRequest(teamId, req);

            return ResponseEntity.ok("Request submitted");

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error creating request: " + e.getMessage());
        }
    }

    /* ---------- APPROVE ---------- */
    @PostMapping("/{requestId}/approve")
    @PreAuthorize("hasRole('TEAM_LEADER')")
    public ResponseEntity<String> approve(
            @PathVariable String teamId,
            @PathVariable String requestId,
            Authentication auth) throws Exception {

        service.approveRequest(teamId, requestId, auth.getName());
        return ResponseEntity.ok("Request approved");
    }

    /* ---------- REJECT ---------- */
    @PostMapping("/{requestId}/reject")
    @PreAuthorize("hasRole('TEAM_LEADER')")
    public String reject(
            @PathVariable String teamId,
            @PathVariable String requestId,
            Authentication auth) throws Exception {

        service.rejectRequest(teamId, requestId, auth.getName());
        return "Request rejected";
    }
}