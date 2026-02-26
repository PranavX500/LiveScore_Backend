package com.example.livescore.Controller;

import com.example.livescore.Dto.LiveScoreEvent;
import com.example.livescore.Model.Match;
import com.example.livescore.Service.FixtureService;
import com.example.livescore.Service.LiveScorePublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/live")
public class LiveScoreController {

    private final FixtureService fixtureService;
    private final LiveScorePublisher liveScoreService;
    private final SimpMessagingTemplate messagingTemplate;

    @GetMapping("/{tournamentId}")
    public List<LiveScoreEvent> liveMatches(
            @PathVariable String tournamentId
    ) throws Exception {
        return fixtureService.getLiveMatches(tournamentId);
    }


    // client sends: /app/live-score/{matchId}
    @MessageMapping("/live-score/{tournamentId}/{matchId}")
    public void getLiveScore(
            @DestinationVariable String tournamentId,
            @DestinationVariable String matchId
    ) throws Exception {

        Match match = fixtureService.getMatchById(tournamentId, matchId);

        LiveScoreEvent event =
                fixtureService.buildLiveScoreEvent(tournamentId, match);

        liveScoreService.publish(event);
    }

}

