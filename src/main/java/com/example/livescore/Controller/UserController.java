package com.example.livescore.Controller;

import com.example.livescore.Dto.SignupRequest;
import com.example.livescore.Model.PlayerCareerStats;
import com.example.livescore.Model.Tournament;
import com.example.livescore.Model.User;
import com.example.livescore.Service.EmailOtpService;
import com.example.livescore.Service.EmailService;
import com.example.livescore.Service.PlayerStatsService;
import com.example.livescore.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final PlayerStatsService playerStatsService;

    /* ---------- SIGNUP ---------- */
    /* ---------- SIGNUP STEP 1 → SEND OTP ---------- */
    @PostMapping("/signup")
    public String signup(@RequestBody SignupRequest request) throws Exception {

        String otp = otpService.createSignupOtp(request);

        emailService.sendOtp(request.getEmail(), otp);

        return "OTP sent";
    }

    /* ---------- SIGNUP STEP 2 → VERIFY OTP ---------- */
    @PostMapping("/verify-signup")
    public User verifySignup(
            @RequestParam String email,
            @RequestParam String otp
    ) throws Exception {

        var data = otpService.verifySignupOtp(email, otp);

        return userService.createAfterOtp(data);
    }

    /* ---------- ME (GET CURRENT USER) ---------- */
    @GetMapping("/me")
    public User me(Authentication authentication) throws Exception {

        if (authentication == null)
            throw new RuntimeException("Unauthenticated");

        String uid = authentication.getName();
        return userService.getUser(uid);
    }
    /* ---------- MAKE ADMIN ---------- */
    @PostMapping("/make-admin/{uid}")

    public String makeAdmin(@PathVariable String uid) throws Exception {
        userService.makeAdmin(uid);
        return "User promoted to ADMIN";
    }
    private final EmailOtpService otpService;
    private final EmailService emailService;


    @PostMapping("/send-email-otp")
    public String sendOtp(@RequestParam String email) throws Exception {

        String otp = otpService.createOtp(email);
        emailService.sendOtp(email, otp);

        return "OTP sent";
    }

    @PostMapping("/verify-email-otp")
    public User verifyOtp(
            @RequestParam String email,
            @RequestParam String otp
    ) throws Exception {

        boolean valid = otpService.verifyOtp(email, otp);

        if (!valid)
            throw new RuntimeException("Invalid OTP");

        return userService.createOrGetByEmail(email);
    }
    @PostMapping("/resend-signup-otp")
    public String resendSignupOtp(@RequestParam String email) throws Exception {

        String otp = otpService.resendSignupOtp(email);

        emailService.sendOtp(email, otp);

        return "OTP resent";
    }
    @PreAuthorize("hasRole('PLAYER')")
    @GetMapping("/player/{userId}/cricket-stats")
    public PlayerCareerStats getPlayerStats(
            @PathVariable String userId
    ) throws Exception {

        return playerStatsService.getCricketStats(userId);
    }
}

