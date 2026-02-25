package com.example.livescore.Service;

import com.example.livescore.Dto.LiveScoreEvent;
import com.example.livescore.Model.Match;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LiveScorePublisher {

    private final SimpMessagingTemplate messagingTemplate;
    protected final MatchService matchService;
    public void publish(LiveScoreEvent event) {

        messagingTemplate.convertAndSend(
                "/topic/live-score/" + event.getMatchId(),
                event
        );
    }

}