package com.example.livescore.Controller;



import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ProfileController {

    @GetMapping("/profile")
    public String profile(org.springframework.security.core.Authentication authentication) {
        return "Authenticated UID: " + authentication.getName();
    }
}
