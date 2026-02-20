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
            InputStream serviceAccount =
                    getClass().getClassLoader()
                            .getResourceAsStream("livescore-8b5ec-firebase-adminsdk-fbsvc-724558165f.json");

            if (serviceAccount == null) {
                throw new RuntimeException("Firebase JSON not found in resources");
            }

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
