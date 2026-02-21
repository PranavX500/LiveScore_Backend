package com.example.livescore.Controller;

import com.example.livescore.Dto.SignupRequest;
import com.example.livescore.Model.User;
import com.example.livescore.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /* ---------- SIGNUP ---------- */
    @PostMapping("/signup")
    public User signup(@RequestBody SignupRequest request) throws Exception {
        return userService.signup(request);
    }

    /* ---------- ME (GET CURRENT USER) ---------- */
    @GetMapping("/me")
    public User me(Authentication authentication) throws Exception {
        String uid = authentication.getName(); // Firebase UID
        return userService.getUser(uid);
    }

    /* ---------- MAKE ADMIN ---------- */
    @PostMapping("/make-admin/{uid}")

    public String makeAdmin(@PathVariable String uid) throws Exception {
        userService.makeAdmin(uid);
        return "User promoted to ADMIN";
    }
}