package com.example.livescore.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    @Bean
    public Firestore firestore() {
        try {
            String base64Config = System.getenv("FIREBASE_CONFIG");

            if (base64Config == null || base64Config.isEmpty()) {
                throw new RuntimeException("FIREBASE_CONFIG env not set");
            }

            // decode base64 → JSON text
            byte[] decoded = java.util.Base64.getDecoder().decode(base64Config);
            String json = new String(decoded);

            // 🔥 fix escaped newlines in private key
            json = json.replace("\\n", "\n");

            InputStream serviceAccount =
                    new ByteArrayInputStream(json.getBytes());

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }

            return FirestoreClient.getFirestore();

        } catch (Exception e) {
            throw new RuntimeException("Firestore initialization failed", e);
        }
    }

}
