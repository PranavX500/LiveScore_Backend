package com.example.livescore.Service;

import com.example.livescore.Model.TeamMember;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TeamMemberService {

    private final FirebaseService firebaseService;

    private static final String COLLECTION = "teams";
    private static final String SUB = "members";

    public void addMember(String teamId, TeamMember member) throws Exception {

        firebaseService.saveSub(
                COLLECTION,
                teamId,
                SUB,
                member.getUserId(),
                member
        );
    }

    public void removeMember(String teamId, String userId) throws Exception {

        firebaseService.deleteSub(
                COLLECTION,
                teamId,
                SUB,
                userId
        );
    }
}
