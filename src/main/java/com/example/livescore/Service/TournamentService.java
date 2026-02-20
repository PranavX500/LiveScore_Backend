package com.example.livescore.Service;

import com.example.livescore.Model.Tournament;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TournamentService {

    private final FirebaseService firebaseService;
    private static final String COLLECTION = "tournaments";

    public void createTournament(Tournament tournament) throws Exception {
        firebaseService.save(COLLECTION, tournament.getId(), tournament);
    }

    public Tournament getTournament(String id) throws Exception {
        return firebaseService.get(COLLECTION, id, Tournament.class);
    }

    public void deleteTournament(String id) throws Exception {
        firebaseService.delete(COLLECTION, id);
    }
}
