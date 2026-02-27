package com.example.livescore.Service;

import com.example.livescore.Dto.SignupRequest;
import com.example.livescore.Model.EmailOtp;
import com.google.cloud.firestore.Firestore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailOtpService {

    private final Firestore firestore;
    private final FirebaseService firebaseService;
    private String generateOtp() {
        int otp = 100000 + new Random().nextInt(900000);
        return String.valueOf(otp);
    }

    public String createOtp(String email) throws Exception {

        String otp = generateOtp();

        EmailOtp emailOtp = EmailOtp.builder()
                .email(email)
                .otp(otp)
                .expiresAt(Instant.now().plusSeconds(300)) // 5 min
                .build();

        firestore.collection("email_otps")
                .document(email)
                .set(emailOtp);

        return otp;
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
    public String createSignupOtp(SignupRequest req) throws Exception {

        String otp = generateOtp();

        EmailOtp data = EmailOtp.builder()
                .email(req.getEmail())
                .name(req.getName())
                .password(req.getPassword())
                .photoUrl(req.getPhotoUrl())
                .otp(otp)
                .expiresAt(Instant.now().plusSeconds(300))
                .lastSentAt(Instant.now())
                .build();

        firebaseService.save("signup_otps", req.getEmail(), data);

        return otp;
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

        return newOtp;
    }


}