package com.example.livescore.Service;

import com.example.livescore.Model.BattingStat;
import com.example.livescore.Model.BowlingStat;
import com.example.livescore.Model.PlayerCareerStats;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlayerStatsService {

    private final FirebaseService firebaseService;

    private static final String USERS = "users";
    private static final String STATS = "stats";
    private static final String CRICKET = "cricket";

    // =====================================================
    // UPDATE CAREER STATS AFTER MATCH
    // =====================================================
    public void updateCricketStats(
            String userId,
            BattingStat bat,
            BowlingStat bowl
    ) throws Exception {

        PlayerCareerStats career =
                firebaseService.getSub(
                        USERS, userId, STATS, CRICKET,
                        PlayerCareerStats.class
                );

        // ✅ create if not exists
        if (career == null) {
            career = new PlayerCareerStats();
            career.setMatches(0);
            career.setRuns(0);
            career.setBallsFaced(0);
            career.setFours(0);
            career.setSixes(0);
            career.setBallsBowled(0);
            career.setRunsConceded(0);
            career.setWickets(0);
        }

        // ✅ increment match only if played
        if (bat != null || bowl != null) {
            career.setMatches(career.getMatches() + 1);
        }

        // ===== BATTING =====
        if (bat != null) {
            career.setRuns(career.getRuns() + safe(bat.getRuns()));
            career.setBallsFaced(career.getBallsFaced() + safe(bat.getBalls()));
            career.setFours(career.getFours() + safe(bat.getFours()));
            career.setSixes(career.getSixes() + safe(bat.getSixes()));
        }

        // ===== BOWLING =====
        if (bowl != null) {
            career.setBallsBowled(career.getBallsBowled() + safe(bowl.getBalls()));
            career.setRunsConceded(career.getRunsConceded() + safe(bowl.getRuns()));
            career.setWickets(career.getWickets() + safe(bowl.getWickets()));
        }

        firebaseService.saveSub(
                USERS, userId, STATS, CRICKET, career
        );
    }

    // =====================================================
    // GET PLAYER STATS (LOGIN DASHBOARD)
    // =====================================================
    public PlayerCareerStats getCricketStats(String userId) throws Exception {

        PlayerCareerStats stats =
                firebaseService.getSub(
                        USERS,
                        userId,
                        STATS,
                        CRICKET,
                        PlayerCareerStats.class
                );

        if (stats == null) {
            stats = new PlayerCareerStats();
            stats.setMatches(0);
            stats.setRuns(0);
            stats.setBallsFaced(0);
            stats.setFours(0);
            stats.setSixes(0);
            stats.setBallsBowled(0);
            stats.setRunsConceded(0);
            stats.setWickets(0);
        }

        return stats;
    }

    // =====================================================
    // SAFE NULL INT
    // =====================================================
    private int safe(Integer v) {
        return v == null ? 0 : v;
    }
}