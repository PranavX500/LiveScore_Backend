package com.example.livescore.Controller;

import com.example.livescore.Dto.SignupRequest;
import com.example.livescore.Model.AppNotification;
import com.example.livescore.Model.PlayerCareerStats;
import com.example.livescore.Model.User;
import com.example.livescore.Service.EmailOtpService;
import com.example.livescore.Service.NotificationService;
import com.example.livescore.Service.PlayerStatsService;
import com.example.livescore.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final PlayerStatsService playerStatsService;
    private final EmailOtpService otpService;
    private final NotificationService notificationService;

    // ================= SIGNUP =================

    /* STEP 1 → SEND OTP */
    @PostMapping("/signup")
    public String signup(@RequestBody SignupRequest request) throws Exception {
        return otpService.createSignupOtp(request);
    }

    /* STEP 2 → VERIFY OTP */
    @PostMapping("/verify-signup")
    public User verifySignup(
            @RequestParam String email,
            @RequestParam String otp
    ) throws Exception {

        var data = otpService.verifySignupOtp(email, otp);
        return userService.createAfterOtp(data);
    }

    /* RESEND OTP */
    @PostMapping("/resend-signup-otp")
    public String resendSignupOtp(@RequestParam String email) throws Exception {
        return otpService.resendSignupOtp(email);
    }

    // ================= LOGIN / EMAIL OTP =================

    @PostMapping("/send-email-otp")
    public String sendOtp(@RequestParam String email) throws Exception {
        return otpService.createOtp(email);
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

    // ================= USER =================

    @GetMapping("/me")
    public User me(Authentication authentication) throws Exception {

        if (authentication == null)
            throw new RuntimeException("Unauthenticated");

        String uid = authentication.getName();
        return userService.getUser(uid);
    }

    @PostMapping("/me/device-token")
    public ResponseEntity<Map<String, String>> saveDeviceToken(
            Authentication authentication,
            @RequestParam String token
    ) throws Exception {

        if (authentication == null)
            throw new RuntimeException("Unauthenticated");

        notificationService.saveDeviceToken(authentication.getName(), token);
        return ResponseEntity.ok(Map.of("message", "Device token saved"));
    }

    @GetMapping("/me/notifications")
    public List<AppNotification> myNotifications(
            Authentication authentication
    ) throws Exception {

        if (authentication == null)
            throw new RuntimeException("Unauthenticated");

        return notificationService.getNotifications(authentication.getName());
    }

    @PatchMapping("/me/notifications/{notificationId}/read")
    public ResponseEntity<Map<String, String>> markNotificationRead(
            Authentication authentication,
            @PathVariable String notificationId
    ) throws Exception {

        if (authentication == null)
            throw new RuntimeException("Unauthenticated");

        notificationService.markAsRead(authentication.getName(), notificationId);
        return ResponseEntity.ok(Map.of("message", "Notification marked as read"));
    }

    @PostMapping("/make-admin/{uid}")
    public String makeAdmin(@PathVariable String uid) throws Exception {
        userService.makeAdmin(uid);
        return "User promoted to ADMIN";
    }

    // ================= PLAYER =================

    @PreAuthorize("hasRole('PLAYER')")
    @GetMapping("/player/{userId}/cricket-stats")
    public PlayerCareerStats getPlayerStats(@PathVariable String userId) throws Exception {
        return playerStatsService.getCricketStats(userId);
    }

    @GetMapping("/count/players")
    public ResponseEntity<Map<String, Long>> countPlayers() throws Exception {
        return ResponseEntity.ok(Map.of(
                "playerCount", userService.countPlayers()
        ));
    }
}
