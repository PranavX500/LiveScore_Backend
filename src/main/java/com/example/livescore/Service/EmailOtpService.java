package com.example.livescore.Service;

import com.example.livescore.Dto.SignupRequest;
import com.example.livescore.Model.EmailOtp;
import com.google.cloud.firestore.Firestore;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailOtpService {

    private final EmailService emailService;
    private final Firestore firestore;
    private final FirebaseService firebaseService;
    private final PasswordEncoder passwordEncoder;

    private String generateOtp() {
        int otp = 100000 + new Random().nextInt(900000);
        return String.valueOf(otp);
    }

    // ================= NORMAL OTP =================

    public String createOtp(String email) throws Exception {

        String otp = generateOtp();

        EmailOtp emailOtp = EmailOtp.builder()
                .email(email)
                .otp(otp)
                .expiresAt(Instant.now().plusSeconds(300))
                .build();

        // save and wait for completion
        firestore.collection("email_otps")
                .document(email)
                .set(emailOtp)
                .get();

        try {
            emailService.sendOtp(email, otp);
        } catch (Exception e) {
            // rollback if email fails
            firestore.collection("email_otps")
                    .document(email)
                    .delete();
            throw new RuntimeException("Failed to send OTP");
        }

        return "OTP sent successfully";
    }

    public boolean verifyOtp(String email, String otp) throws Exception {

        var doc = firestore.collection("email_otps")
                .document(email)
                .get()
                .get();

        if (!doc.exists()) return false;

        EmailOtp stored = doc.toObject(EmailOtp.class);

        if (stored == null) return false;

        if (Instant.now().isAfter(stored.getExpiresAt()))
            return false;

        if (!stored.getOtp().equals(otp))
            return false;

        // delete after success
        firestore.collection("email_otps")
                .document(email)
                .delete();

        return true;
    }

    // ================= SIGNUP OTP =================

    public String createSignupOtp(SignupRequest req) throws Exception {

        String otp = generateOtp();

        EmailOtp data = EmailOtp.builder()
                .email(req.getEmail())
                .name(req.getName())
                .password(passwordEncoder.encode(req.getPassword())) // 🔐 FIXED
                .photoUrl(req.getPhotoUrl())
                .otp(otp)
                .expiresAt(Instant.now().plusSeconds(300))
                .lastSentAt(Instant.now())
                .build();

        firebaseService.save("signup_otps", req.getEmail(), data);

        try {
            emailService.sendOtp(req.getEmail(), otp);
        } catch (Exception e) {
            firebaseService.delete("signup_otps", req.getEmail());
            e.printStackTrace(); // 🔥 shows real error
            throw new RuntimeException("Failed to send OTP: " + e.getMessage());
        }
        return "Signup OTP sent successfully";
    }

    public EmailOtp verifySignupOtp(String email, String otp) throws Exception {

        EmailOtp stored =
                firebaseService.get("signup_otps", email, EmailOtp.class);

        if (stored == null)
            throw new RuntimeException("OTP not found");

        if (Instant.now().isAfter(stored.getExpiresAt()))
            throw new RuntimeException("OTP expired");

        if (!stored.getOtp().equals(otp))
            throw new RuntimeException("Invalid OTP");

        firebaseService.delete("signup_otps", email);

        return stored;
    }

    public String resendSignupOtp(String email) throws Exception {

        EmailOtp existing =
                firebaseService.get("signup_otps", email, EmailOtp.class);

        if (existing == null)
            throw new RuntimeException("Signup not initiated");

        // ⏱ resend cooldown 30s
        if (existing.getLastSentAt() != null &&
                Instant.now().isBefore(existing.getLastSentAt().plusSeconds(30))) {

            throw new RuntimeException("Please wait before resending OTP");
        }

        String newOtp = generateOtp();

        existing.setOtp(newOtp);
        existing.setExpiresAt(Instant.now().plusSeconds(300));
        existing.setLastSentAt(Instant.now());

        firebaseService.save("signup_otps", email, existing);

        try {
            emailService.sendOtp(email, newOtp);
        } catch (Exception e) {
            throw new RuntimeException("Failed to resend OTP");
        }

        return "OTP resent successfully";
    }
}
