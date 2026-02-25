package com.example.livescore.Model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tournament {

    private String id;
    private String name;
    private String location;

    // Change the pattern to include .SSS (milliseconds) and XXX (ISO 8601 zone)
    @JsonFormat(shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
            timezone = "UTC")
    private Date startDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
            timezone = "UTC")
    private Date endDate;

    private Sports sports;
    private Long totalTeams;
    private Long registeredTeams;
    private Long requiredPlayer;

    private Registeration registeration;

    @JsonFormat(shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
            timezone = "UTC")
    private Date createdAt;
   private Long totalOvers;;
}