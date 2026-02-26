package com.example.livescore.Service;

import com.example.livescore.Dto.CricketScoreboard;
import com.example.livescore.Dto.LiveScoreEvent;
import com.example.livescore.Dto.PlayerBattingRow;
import com.example.livescore.Dto.PlayerBowlingRow;
import com.example.livescore.Model.*;
import com.google.cloud.firestore.FieldPath;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class FixtureService {

    private final FirebaseService firebaseService;
    private final UserService userService;
    private final TeamService teamService;
    private final ScoreBroadcastService scoreBroadcastService;
    private final LiveScorePublisher  liveScorePublisher;

    private static final String COL = "tournaments";
    private static final String SUB_MATCH = "matches";

    // =====================================================
    // GENERATE FIXTURES (MULTI-SPORT)
    // =====================================================
    public void generateFixtures(String tournamentId, Sports sport) throws Exception {

        log.info("GENERATE FIXTURES tournamentId={} sport={}", tournamentId, sport);

        List<TournamentTeam> teams =
                firebaseService.getAllSub(
                        COL,
                        tournamentId,
                        "teams",
                        TournamentTeam.class
                );

        if (teams.size() < 2)
            throw new RuntimeException("Not enough teams");

        if (teams.size() % 2 != 0)
            throw new RuntimeException("Teams must be even");

        Collections.shuffle(teams);

        for (int i = 0; i < teams.size(); i += 2) {

            TournamentTeam a = teams.get(i);
            TournamentTeam b = teams.get(i + 1);

            Match match = Match.builder()
                    .id(UUID.randomUUID().toString())
                    .tournamentId(tournamentId)
                    .sport(sport)   // ✅ enum
                    .teamAId(a.getTeamId())
                    .teamAName(a.getTeamName())
                    .teamBId(b.getTeamId())
                    .teamBName(b.getTeamName())
                    .round(1L)
                    .scoreA(0)
                    .scoreB(0)
                    .status("UPCOMING")
                    .scheduledAt(Date.from(Instant.now()))
                    .updatedAt(Date.from(Instant.now()))
                    .liveData(initLiveData(sport))
                    .build();

            firebaseService.saveSub(
                    COL,
                    tournamentId,
                    SUB_MATCH,
                    match.getId(),
                    match
            );
        }
    }

    // =====================================================
    // INIT LIVE DATA PER SPORT
    // =====================================================
    private Object initLiveData(Sports sport) {

        switch (sport) {

            case CRICKET:
                return CricketLiveData.builder()
                        .innings(1)
                        .oversA(0.0)
                        .wicketsA(0)
                        .oversB(0.0)
                        .wicketsB(0)
                        .thisOver(new ArrayList<>())
                        .build();

            case FOOTBALL:
                return FootballLiveData.builder()
                        .minute(0)
                        .second(0)
                        .half(1)
                        .build();

            case BASKETBALL:
                return BasketballLiveData.builder()
                        .quarter(1)
                        .timeLeft("10:00")
                        .build();

            case VOLLEYBALL:
                return VolleyballLiveData.builder()
                        .currentSet(1)
                        .setsWonA(0)
                        .setsWonB(0)
                        .build();

            default:
                return new HashMap<>();
        }
    }

    // =====================================================
    // START MATCH
    // =====================================================
    public void startMatch(String tournamentId, String matchId) throws Exception {

        Match match = firebaseService.getSub(
                COL,
                tournamentId,
                SUB_MATCH,
                matchId,
                Match.class
        );

        match.setStatus("LIVE");
        match.setUpdatedAt(Date.from(Instant.now()));

        firebaseService.saveSub(
                COL,
                tournamentId,
                SUB_MATCH,
                matchId,
                match
        );
    }
    public Match getMatch(
            String tournamentId,
            String matchId
    ) throws Exception {

        return firebaseService.getSub(
                COL,
                tournamentId,
                SUB_MATCH,
                matchId,
                Match.class
        );
    }
    // =====================================================
    // CRICKET BALL UPDATE
    // =====================================================
    public void updateCricketBall(
            String tournamentId,
            String matchId,
            int runs,
            boolean wicket
    ) throws Exception {

        Match match = firebaseService.getSub(
                COL, tournamentId, SUB_MATCH, matchId, Match.class
        );

        Tournament tournament = firebaseService.get(
                COL, tournamentId, Tournament.class
        );

        if (tournament == null)
            throw new RuntimeException("Tournament not found");

        if (tournament.getTotalOvers() == null)
            throw new RuntimeException("Tournament totalOvers missing");

        int maxBalls = Math.toIntExact(tournament.getTotalOvers() * 6);

        CricketLiveData live =
                firebaseService.convert(match.getLiveData(), CricketLiveData.class);

        // ===== SAFE INIT =====
        if (live.getBattingStats() == null)
            live.setBattingStats(new HashMap<>());

        if (live.getBowlingStats() == null)
            live.setBowlingStats(new HashMap<>());

        if (live.getThisOver() == null)
            live.setThisOver(new ArrayList<>());

        if (live.getBallsInOver() == null)
            live.setBallsInOver(0);

        if (live.getBallsA() == null)
            live.setBallsA(0);

        if (live.getBallsB() == null)
            live.setBallsB(0);

        if (live.getWicketsA() == null)
            live.setWicketsA(0);

        if (live.getWicketsB() == null)
            live.setWicketsB(0);

        int innings = live.getInnings() == null ? 1 : live.getInnings();

        String striker = live.getStrikerId();
        String nonStriker = live.getNonStrikerId();
        String bowler = live.getBowlerId();

        if (striker == null || bowler == null)
            throw new RuntimeException("Players not selected");

        // ===== FETCH STATS =====
        BattingStat bat = live.getBattingStats().getOrDefault(
                striker,
                BattingStat.builder()
                        .runs(0).balls(0).fours(0).sixes(0)
                        .out(false).innings(innings)
                        .build()
        );

        BowlingStat bowl = live.getBowlingStats().getOrDefault(
                bowler,
                BowlingStat.builder()
                        .balls(0).runs(0).wickets(0)
                        .innings(innings)
                        .build()
        );

        // ===== APPLY BALL =====
        bat.setBalls(bat.getBalls() + 1);
        bowl.setBalls(bowl.getBalls() + 1);

        if (innings == 1)
            match.setScoreA(match.getScoreA() + runs);
        else
            match.setScoreB(match.getScoreB() + runs);

        if (wicket) {

            bat.setOut(true);
            bowl.setWickets(bowl.getWickets() + 1);

            if (innings == 1)
                live.setWicketsA(live.getWicketsA() + 1);
            else
                live.setWicketsB(live.getWicketsB() + 1);

            live.getThisOver().add("W");

        } else {

            bat.setRuns(bat.getRuns() + runs);
            bowl.setRuns(bowl.getRuns() + runs);

            if (runs == 4) bat.setFours(bat.getFours() + 1);
            if (runs == 6) bat.setSixes(bat.getSixes() + 1);

            live.getThisOver().add(String.valueOf(runs));
        }

        // ===== SAVE STATS =====
        live.getBattingStats().put(striker, bat);
        live.getBowlingStats().put(bowler, bowl);

        // ===== BALL COUNT =====
        live.setBallsInOver(live.getBallsInOver() + 1);

        if (innings == 1)
            live.setBallsA(live.getBallsA() + 1);
        else
            live.setBallsB(live.getBallsB() + 1);

        // ===== UPDATE OVERS EVERY BALL =====
        if (innings == 1)
            live.setOversA(calcOvers(live.getBallsA()));
        else
            live.setOversB(calcOvers(live.getBallsB()));

        // ===== STRIKE ROTATE (runs) =====
        if (!wicket && runs % 2 == 1) {
            String tmp = striker;
            striker = nonStriker;
            nonStriker = tmp;
        }

        // ===== OVER COMPLETE =====
        if (live.getBallsInOver() == 6) {

            live.setBallsInOver(0);
            live.setThisOver(new ArrayList<>());

            // swap strike at over end
            String tmp = striker;
            striker = nonStriker;
            nonStriker = tmp;
        }

        // ===== WICKET HANDLING =====
        if (wicket)
            striker = null; // new batsman will come

        live.setStrikerId(striker);
        live.setNonStrikerId(nonStriker);
        live.setBowlerId(bowler);
        live.setLastBall(wicket ? "W" : String.valueOf(runs));

        // ===== INNINGS LOGIC =====
        if (innings == 1) {

            if (live.getBallsA() >= maxBalls || live.getWicketsA() >= 10) {

                live.setInnings(2);
                live.setTarget(match.getScoreA() + 1);

                live.setBallsB(0);
                live.setOversB(0.0);
                live.setWicketsB(0);

                match.setScoreB(0);

                live.setStrikerId(null);
                live.setNonStrikerId(null);
                live.setBowlerId(null);
                live.setBallsInOver(0);
                live.setThisOver(new ArrayList<>());
            }

        } else {

            if (match.getScoreB() >= live.getTarget()) {
                finishMatch(match, match.getTeamBId());
            }
            else if (live.getBallsB() >= maxBalls || live.getWicketsB() >= 10) {
                decideWinner(match);
            }
        }

        match.setLiveData(live);
        match.setUpdatedAt(new Date());

        firebaseService.saveSub(
                COL, tournamentId, SUB_MATCH, matchId, match
        );

        // ===== BROADCAST =====
        CricketScoreboard board = getCricketScoreboard(tournamentId, matchId);
        scoreBroadcastService.broadcastScore(matchId, board);

        LiveScoreEvent event = buildLiveScoreEvent(tournamentId, match);
        liveScorePublisher.publish(event);
    }
    // =====================================================
    // FOOTBALL GOAL UPDATE
    // =====================================================
    public void footballGoal(
            String tournamentId,
            String matchId,
            String team // "A" or "B"
    ) throws Exception {

        Match match = firebaseService.getSub(
                COL,
                tournamentId,
                SUB_MATCH,
                matchId,
                Match.class
        );

        if (match.getSport() != Sports.FOOTBALL)
            throw new RuntimeException("Not a football match");

        if ("A".equalsIgnoreCase(team))
            match.setScoreA(match.getScoreA() + 1);
        else
            match.setScoreB(match.getScoreB() + 1);

        match.setUpdatedAt(Date.from(Instant.now()));

        firebaseService.saveSub(
                COL,
                tournamentId,
                SUB_MATCH,
                matchId,
                match
        );
    }

    // =====================================================
    // GET MATCHES
    // =====================================================
    public List<Match> getMatches(String tournamentId) throws Exception {

        return firebaseService.getAllSub(
                COL,
                tournamentId,
                SUB_MATCH,
                Match.class
        );
    }
    // =====================================================
// START CRICKET INNINGS
// =====================================================
    public void startCricketInnings(
            String tournamentId,
            String matchId,
            String strikerId,
            String nonStrikerId,
            String bowlerId
    ) throws Exception {


        Match match = firebaseService.getSub(
                COL,
                tournamentId,
                SUB_MATCH,
                matchId,
                Match.class
        );

        if (match.getSport() != Sports.CRICKET)
            throw new RuntimeException("Not a cricket match");

        CricketLiveData live =
                firebaseService.convert(match.getLiveData(), CricketLiveData.class);
        BattingStat s = live.getBattingStats().get(strikerId);
        if (s != null && Boolean.TRUE.equals(s.getOut()))
            throw new RuntimeException("Player already out");

        // detect innings
        int innings = (live.getInnings() == null || live.getInnings() == 0)
                ? 1
                : live.getInnings();

        // if innings already 1 and scoreA exists → start 2nd innings
        if (innings == 1 && match.getScoreA() > 0) {
            innings = 2;

            // set target
            live.setTarget(match.getScoreA() + 1);

            // reset team B stats
            match.setScoreB(0);
            live.setOversB(0.0);
            live.setWicketsB(0);
        }

        live.setInnings(innings);

        // set players
        live.setStrikerId(strikerId);
        live.setNonStrikerId(nonStrikerId);
        live.setBowlerId(bowlerId);

        // reset over
        live.setThisOver(new ArrayList<>());
        live.setLastBall(null);

        // reset overs/wickets for current innings
        if (innings == 1) {
            live.setOversA(0.0);
            live.setWicketsA(0);
            match.setScoreA(0);
        } else {
            live.setOversB(0.0);
            live.setWicketsB(0);
        }

        match.setStatus("LIVE");
        match.setLiveData(live);
        match.setUpdatedAt(Date.from(Instant.now()));


        firebaseService.saveSub(
                COL,
                tournamentId,
                SUB_MATCH,
                matchId,
                match
        );
    }
    private void swapTeams(Match match) {

        String tId = match.getTeamAId();
        String tName = match.getTeamAName();

        match.setTeamAId(match.getTeamBId());
        match.setTeamAName(match.getTeamBName());

        match.setTeamBId(tId);
        match.setTeamBName(tName);
    }
    private void startSecondInnings(Match match, CricketLiveData live) {

        live.setInnings(2);
        live.setTarget(match.getScoreA() + 1);

        live.setOversB(0.0);
        live.setWicketsB(0);

        match.setScoreB(0);
    }
    private void finishMatch(Match match, String winnerTeamId) {

        match.setStatus("COMPLETED");
        match.setWinnerTeamId(winnerTeamId);
    }
    private void decideWinner(Match match) {

        if (match.getScoreA() > match.getScoreB()) {
            finishMatch(match, match.getTeamAId());
        } else if (match.getScoreB() > match.getScoreA()) {
            finishMatch(match, match.getTeamBId());
        } else {
            match.setStatus("DRAW");
        }
    }
    private double addOver(double overs) {
        int whole = (int) overs;
        int balls = (int) Math.round((overs - whole) * 10);

        balls++;

        if (balls == 6) {
            return whole + 1;
        }

        return whole + balls / 10.0;
    }
    private double calcOvers(int balls) {
        int over = balls / 6;
        int ball = balls % 6;
        return Double.parseDouble(over + "." + ball);
    }
    public CricketScoreboard getCricketScoreboard(
            String tournamentId,
            String matchId
    ) throws Exception {

        Match match = firebaseService.getSub(
                COL, tournamentId, SUB_MATCH, matchId, Match.class
        );

        CricketLiveData live =
                firebaseService.convert(match.getLiveData(), CricketLiveData.class);

        Map<String,BattingStat> batting =
                live.getBattingStats() == null ? new HashMap<>() : live.getBattingStats();

        Map<String,BowlingStat> bowling =
                live.getBowlingStats() == null ? new HashMap<>() : live.getBowlingStats();

        // ===== TEAM MEMBERS =====
        List<TeamMember> teamA =
                firebaseService.getAllSub("teams", match.getTeamAId(), "members", TeamMember.class);

        List<TeamMember> teamB =
                firebaseService.getAllSub("teams", match.getTeamBId(), "members", TeamMember.class);

        Set<String> teamAIds = new HashSet<>();
        Set<String> teamBIds = new HashSet<>();

        for (TeamMember m : teamA) teamAIds.add(m.getUserId());
        for (TeamMember m : teamB) teamBIds.add(m.getUserId());

        List<PlayerBattingRow> battingA = new ArrayList<>();
        List<PlayerBattingRow> battingB = new ArrayList<>();
        List<PlayerBowlingRow> bowlingA = new ArrayList<>();
        List<PlayerBowlingRow> bowlingB = new ArrayList<>();

        // ===== BATTING =====
        for (Map.Entry<String,BattingStat> e : batting.entrySet()) {

            String pid = e.getKey();
            BattingStat s = e.getValue();

            PlayerBattingRow row = PlayerBattingRow.builder()
                    .playerId(pid)
                    .playerName(getPlayerName(pid))
                    .runs(s.getRuns())
                    .balls(s.getBalls())
                    .fours(s.getFours())
                    .sixes(s.getSixes())
                    .out(Boolean.TRUE.equals(s.getOut()))
                    .build();

            if (teamAIds.contains(pid))
                battingA.add(row);
            else if (teamBIds.contains(pid))
                battingB.add(row);
        }

        // ===== BOWLING =====
        for (Map.Entry<String,BowlingStat> e : bowling.entrySet()) {

            String pid = e.getKey();
            BowlingStat s = e.getValue();

            PlayerBowlingRow row = PlayerBowlingRow.builder()
                    .playerId(pid)
                    .playerName(getPlayerName(pid))
                    .balls(s.getBalls())
                    .runs(s.getRuns())
                    .wickets(s.getWickets())
                    .build();

            if (teamAIds.contains(pid))
                bowlingA.add(row);
            else if (teamBIds.contains(pid))
                bowlingB.add(row);
        }

        // ===== WINNER NAME =====
        String winnerTeamName = null;
        if (match.getWinnerTeamId() != null)
            winnerTeamName = getTeamName(match.getWinnerTeamId());

        return CricketScoreboard.builder()
                .teamAName(match.getTeamAName())
                .teamBName(match.getTeamBName())
                .scoreA(match.getScoreA())
                .scoreB(match.getScoreB())
                .oversA(live.getOversA())
                .oversB(live.getOversB())
                .battingA(battingA)
                .battingB(battingB)
                .bowlingA(bowlingA)
                .bowlingB(bowlingB)
                .winnerTeamId(match.getWinnerTeamId())
                .winnerTeamName(winnerTeamName)
                .build();
    }
    private String getPlayerName(String userId) {
        try {
            User u = userService.getUser(userId);
            return u != null ? u.getName() : "Player";
        } catch (Exception e) {
            return "Player";
        }
    }

    private String getTeamName(String teamId) {
        try {
            Team t = teamService.getTeam(teamId);
            return t != null ? t.getName() : "";
        } catch (Exception e) {
            return "";
        }
    }
    public LiveScoreEvent buildLiveScoreEvent(
            String tournamentId,
            Match match
    ) throws Exception {

        CricketLiveData live =
                firebaseService.convert(match.getLiveData(), CricketLiveData.class);

        if (live == null)
            live = new CricketLiveData();

        // ===== SAFE BALLS =====
        int ballsA = live.getBallsA() == null ? 0 : live.getBallsA();
        int ballsB = live.getBallsB() == null ? 0 : live.getBallsB();

        // ===== SAFE WICKETS =====
        int wicketsA = live.getWicketsA() == null ? 0 : live.getWicketsA();
        int wicketsB = live.getWicketsB() == null ? 0 : live.getWicketsB();

        Map<String, BattingStat> batMap =
                live.getBattingStats() == null ? new HashMap<>() : live.getBattingStats();

        Map<String, BowlingStat> bowlMap =
                live.getBowlingStats() == null ? new HashMap<>() : live.getBowlingStats();

        List<PlayerBattingRow> battingA = new ArrayList<>();
        List<PlayerBattingRow> battingB = new ArrayList<>();
        List<PlayerBowlingRow> bowlingA = new ArrayList<>();
        List<PlayerBowlingRow> bowlingB = new ArrayList<>();

        // ===== TEAM MEMBERS =====
        List<TeamMember> teamA =
                firebaseService.getAllSub("teams", match.getTeamAId(), "members", TeamMember.class);

        List<TeamMember> teamB =
                firebaseService.getAllSub("teams", match.getTeamBId(), "members", TeamMember.class);

        Set<String> teamAIds = new HashSet<>();
        Set<String> teamBIds = new HashSet<>();

        for (TeamMember m : teamA) teamAIds.add(m.getUserId());
        for (TeamMember m : teamB) teamBIds.add(m.getUserId());

        // ===== BATTING =====
        for (Map.Entry<String, BattingStat> e : batMap.entrySet()) {

            String pid = e.getKey();
            BattingStat s = e.getValue();

            PlayerBattingRow row = PlayerBattingRow.builder()
                    .playerId(pid)
                    .playerName(getPlayerName(pid))
                    .runs(s.getRuns())
                    .balls(s.getBalls())
                    .fours(s.getFours())
                    .sixes(s.getSixes())
                    .out(Boolean.TRUE.equals(s.getOut()))
                    .build();

            if (teamAIds.contains(pid))
                battingA.add(row);
            else if (teamBIds.contains(pid))
                battingB.add(row);
        }

        // ===== BOWLING =====
        for (Map.Entry<String, BowlingStat> e : bowlMap.entrySet()) {

            String pid = e.getKey();
            BowlingStat s = e.getValue();

            PlayerBowlingRow row = PlayerBowlingRow.builder()
                    .playerId(pid)
                    .playerName(getPlayerName(pid))
                    .balls(s.getBalls())
                    .runs(s.getRuns())
                    .wickets(s.getWickets())
                    .build();

            if (teamAIds.contains(pid))
                bowlingA.add(row);
            else if (teamBIds.contains(pid))
                bowlingB.add(row);
        }

        // ===== TEAM PLAYER LISTS (for UI) =====
        List<String> teamAPlayers = teamA.stream()
                .map(m -> getPlayerName(m.getUserId()))
                .toList();

        List<String> teamBPlayers = teamB.stream()
                .map(m -> getPlayerName(m.getUserId()))
                .toList();

        // ===== BUILD EVENT =====
        return LiveScoreEvent.builder()
                .matchId(match.getId())
                .teamAName(match.getTeamAName())
                .teamBName(match.getTeamBName())
                .scoreA(match.getScoreA())
                .scoreB(match.getScoreB())

                // ⭐ FIX: overs derived from balls
                .oversA(calcOvers(ballsA))
                .oversB(calcOvers(ballsB))

                .wicketsA(wicketsA)
                .wicketsB(wicketsB)

                .battingA(battingA)
                .battingB(battingB)
                .bowlingA(bowlingA)
                .bowlingB(bowlingB)

                .teamAPlayers(teamAPlayers)
                .teamBPlayers(teamBPlayers)

                .strikerName(getPlayerName(live.getStrikerId()))
                .nonStrikerName(getPlayerName(live.getNonStrikerId()))
                .bowlerName(getPlayerName(live.getBowlerId()))

                .status(match.getStatus())
                .build();
    }

    public List<LiveScoreEvent> getLiveMatches(String tournamentId) throws Exception {

        List<Match> matches = firebaseService.getAllSub(
                "tournaments",
                tournamentId,
                "matches",
                Match.class
        );

        List<LiveScoreEvent> live = new ArrayList<>();

        for (Match m : matches) {
            if ("LIVE".equals(m.getStatus())) {
                live.add(buildLiveScoreEvent(tournamentId, m));
            }
        }

        return live;
    }
    public Match getMatchById(String tournamentId, String matchId) throws Exception {

        var doc = firebaseService.getFirestore()
                .collection("tournaments")
                .document(tournamentId)
                .collection("matches")
                .document(matchId)
                .get()
                .get();

        if (!doc.exists())
            throw new RuntimeException("Match not found: " + matchId);

        Match match = doc.toObject(Match.class);
        match.setId(matchId);
        match.setTournamentId(tournamentId);

        return match;
    }

}
