package com.example.livescore.Model;

import lombok.*;
import java.time.Instant;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailOtp {


    private String email;
    private String name;
    private String password;
    private String photoUrl;
    private String otp;
    private Instant expiresAt;
    private Instant lastSentAt;
}