package com.example.livescore.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    @Value("${emailKey}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    private final String URL = "https://api.brevo.com/v3/smtp/email";

    public void sendOtp(String email, String otp) {
        sendEmail(
                email,
                "Your OTP Code",
                "<h3>Your OTP is: <b>" + otp + "</b></h3><p>Valid for 5 minutes</p>"
        );
    }

    public void sendMatchStartedEmail(String email, String tournamentName, String matchLabel) {
        String subject = "Match Started: " + matchLabel;
        String htmlContent = "<h3>" + matchLabel + " has started</h3>"
                + "<p>Tournament: <b>" + tournamentName + "</b></p>"
                + "<p>Open LiveScore to follow the match live.</p>";
        sendEmail(email, subject, htmlContent);
    }

    private void sendEmail(String email, String subject, String htmlContent) {
        try {
            log.info("Email send requested to={} subject={}", email, subject);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("api-key", apiKey);

            Map<String, Object> body = Map.of(
                    "sender", Map.of(
                            "name", "LiveScore",
                            "email", "ps5840432@gmail.com"
                    ),
                    "to", List.of(
                            Map.of("email", email)
                    ),
                    "subject", subject,
                    "htmlContent", htmlContent
            );

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            ResponseEntity<String> response =
                    restTemplate.postForEntity(URL, request, String.class);

            log.info("Email send accepted to={} status={} response={}",
                    email, response.getStatusCode(), response.getBody());
        } catch (Exception e) {
            log.error("Email send failed to={} subject={}", email, subject, e);
            throw new RuntimeException("Brevo email failed: " + e.getMessage());
        }
    }
}
