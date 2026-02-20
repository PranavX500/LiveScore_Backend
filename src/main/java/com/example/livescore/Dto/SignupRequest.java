package com.example.livescore.Dto;

import lombok.Data;

@Data
public class SignupRequest {
    private String name;
    private String email;
    private String password;
    private String photoUrl;
}