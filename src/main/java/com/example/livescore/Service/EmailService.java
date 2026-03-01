package com.example.livescore.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final String API_KEY = System.getenv("RESEND_API_KEY");

    public void sendOtp(String email, String otp) throws Exception {

        String html = """
            <div style="font-family:Arial;padding:20px">
              <h2>LiveScore OTP</h2>
              <p>Your verification code is:</p>
              <h1 style="letter-spacing:4px">%s</h1>
              <p>This code expires in 5 minutes.</p>
            </div>
        """.formatted(otp);

        String body = """
        {
          "from": "LiveScore <onboarding@resend.dev>",
          "to": "%s",
          "subject": "Your LiveScore OTP",
          "html": "%s"
        }
        """.formatted(email, html.replace("\"", "\\\""));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.resend.com/emails"))
                .header("Authorization", "Bearer " + API_KEY)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());
    }
}
