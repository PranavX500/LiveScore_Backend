package com.example.livescore.Service;

import com.example.livescore.Model.Match;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MatchService {

    private final FirebaseService firebaseService;
    private static final String COLLECTION = "matches";

    public void createMatch(Match match) throws Exception {
        firebaseService.save(COLLECTION, match.getId(), match);
    }

    public Match getMatch(String id) throws Exception {
        return firebaseService.get(COLLECTION, id, Match.class);
    }

    public void deleteMatch(String id) throws Exception {
        firebaseService.delete(COLLECTION, id);
    }
}
