package com.example.livescore.Service;

import com.example.livescore.Model.TeamJoinRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TeamJoinRequestService {

    private final FirebaseService firebaseService;

    private static final String COLLECTION = "teams";
    private static final String SUB = "requests";

    public void createRequest(String teamId, TeamJoinRequest req) throws Exception {

        firebaseService.saveSub(
                COLLECTION,
                teamId,
                SUB,
                req.getId(),
                req
        );
    }

    public void deleteRequest(String teamId, String requestId) throws Exception {

        firebaseService.deleteSub(
                COLLECTION,
                teamId,
                SUB,
                requestId
        );
    }
}
