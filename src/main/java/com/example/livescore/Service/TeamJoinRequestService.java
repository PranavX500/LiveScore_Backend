package com.example.livescore.Service;

import com.example.livescore.Model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
@RequiredArgsConstructor
public class TeamJoinRequestService {
    private static final Logger log = LoggerFactory.getLogger(TeamJoinRequestService.class);
    private final TeamMemberService teamMemberService;
    private final FirebaseService firebaseService;
    private final TeamService teamService;
    private final UserService userService;

    private static final String COLLECTION = "teams";
    private static final String SUB = "requests";

    /* ---------- APPLY ---------- */
    public TeamJoinRequest createRequest(String teamId, TeamJoinRequest req) throws Exception {

        Team team = teamService.getTeam(teamId);
        if (team == null) throw new RuntimeException("Team not found");

        if (team.getCurrentPlayers() >= team.getMaxPlayers()) {
            throw new RuntimeException("Team is full");
        }

        firebaseService.saveSub(
                COLLECTION,
                teamId,
                SUB,
                req.getId(),
                req
        );

        return req;
    }

    /* ---------- APPROVE ---------- */
    public void approveRequest(String teamId, String requestId, String leaderUid) throws Exception {

        log.info("APPROVE START teamId={} requestId={} leaderUid={}", teamId, requestId, leaderUid);

        Team team = teamService.getTeam(teamId);
        if (team == null) {
            log.error("Team not found: {}", teamId);
            throw new RuntimeException("Team not found");
        }

        if (!team.getLeaderId().equals(leaderUid)) {
            log.error("Leader mismatch. teamLeader={} caller={}", team.getLeaderId(), leaderUid);
            throw new RuntimeException("Not team leader");
        }

        TeamJoinRequest req = firebaseService.getSub(
                COLLECTION, teamId, SUB, requestId, TeamJoinRequest.class);

        if (req == null) {
            log.error("Request not found: {}", requestId);
            throw new RuntimeException("Request not found");
        }

        log.info("Request loaded userId={} status={}", req.getUserId(), req.getStatus());

        if (req.getStatus() != Status.PENDING) {
            log.warn("Request already processed: {}", requestId);
            throw new RuntimeException("Request already processed");
        }

        if (team.getCurrentPlayers() >= team.getMaxPlayers()) {
            log.warn("Team full teamId={} current={} max={}",
                    teamId, team.getCurrentPlayers(), team.getMaxPlayers());
            throw new RuntimeException("Team is full");
        }

        // check existing member
        TeamMember existing = firebaseService.getSub(
                "teams", teamId, "members", req.getUserId(), TeamMember.class);

        if (existing != null) {
            log.warn("User already member userId={} teamId={}", req.getUserId(), teamId);
            throw new RuntimeException("Already member");
        }

        User user = userService.getUser(req.getUserId());

        TeamMember member = TeamMember.builder()
                .userId(req.getUserId())
                .name(user.getName())   // ✅ store snapshot
                .role("PLAYER")
                .joinedAt(Instant.now())
                .build();

        teamMemberService.addMember(teamId, member);
        // increase team count
        team.setCurrentPlayers(team.getCurrentPlayers() + 1);
        teamService.save(team);
        log.info("Team count updated teamId={} currentPlayers={}", teamId, team.getCurrentPlayers());

        // update role
        userService.updateRole(req.getUserId(), Role.PLAYER);
        log.info("User role updated userId={} -> PLAYER", req.getUserId());

        // mark approved
        req.setStatus(Status.APPROVED);
        firebaseService.saveSub(COLLECTION, teamId, SUB, requestId, req);
        log.info("Request approved requestId={}", requestId);

        log.info("APPROVE END SUCCESS teamId={} userId={}", teamId, req.getUserId());
    }
    /* ---------- REJECT ---------- */
    public void rejectRequest(String teamId, String requestId, String leaderUid) throws Exception {

        Team team = teamService.getTeam(teamId);
        if (team == null) throw new RuntimeException("Team not found");

        if (!team.getLeaderId().equals(leaderUid)) {
            throw new RuntimeException("Not team leader");
        }

        TeamJoinRequest req = firebaseService.getSub(
                COLLECTION,
                teamId,
                SUB,
                requestId,
                TeamJoinRequest.class
        );

        if (req == null) throw new RuntimeException("Request not found");

        req.setStatus(Status.REJECTED);

        firebaseService.saveSub(
                COLLECTION,
                teamId,
                SUB,
                requestId,
                req
        );
    }

    /* ---------- DELETE (optional) ---------- */
    public void deleteRequest(String teamId, String requestId) throws Exception {
        firebaseService.deleteSub(
                COLLECTION,
                teamId,
                SUB,
                requestId
        );
    }


}