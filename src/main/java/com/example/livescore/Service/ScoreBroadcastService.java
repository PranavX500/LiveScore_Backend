package com.example.livescore.Service;



import com.example.livescore.Dto.CricketScoreboard;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScoreBroadcastService {

    private final SimpMessagingTemplate messagingTemplate;

    public void broadcastScore(String matchId, CricketScoreboard board) {

        // topic per match
        String destination = "/topic/match/" + matchId;

        messagingTemplate.convertAndSend(destination, board);
    }
}