package com.example.livescore.Model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tournament {

    private String id;
    private String name;
    private String location;
    private Instant startDate;
    private Instant endDate;
    private String createdBy;
    private Instant createdAt;
}
