package com.example.livescore.Controller;

import com.example.livescore.Service.FixtureService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@RestController
@RequestMapping("/PointTable")
@AllArgsConstructor
public class PointsTable {
  private final FixtureService fixtureService;

    @GetMapping("/{tournamentId}/points-table")
    public ResponseEntity<List<com.example.livescore.Model.PointsTable>> getPointsTable(
            @PathVariable String tournamentId
    ) throws Exception {

        return ResponseEntity.ok(
                fixtureService.getPointsTable(tournamentId)
        );
    }
}
