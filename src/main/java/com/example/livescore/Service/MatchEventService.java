package com.example.livescore.Service;

import com.example.livescore.Model.MatchEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MatchEventService {

    private final FirebaseService firebaseService;

    private static final String COLLECTION = "matches";
    private static final String SUB = "events";

    public void addEvent(String matchId, MatchEvent event) throws Exception {

        firebaseService.saveSub(
                COLLECTION,
                matchId,
                SUB,
                event.getId(),
                event
        );
    }

    public void deleteEvent(String matchId, String eventId) throws Exception {

        firebaseService.deleteSub(
                COLLECTION,
                matchId,
                SUB,
                eventId
        );
    }
}
