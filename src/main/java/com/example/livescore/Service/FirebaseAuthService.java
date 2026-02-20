package com.example.livescore.Service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.stereotype.Service;

@Service
public class FirebaseAuthService {

    public FirebaseToken verifyToken(String idToken) {

        try {
            return FirebaseAuth.getInstance().verifyIdToken(idToken);
        } catch (Exception e) {
            throw new RuntimeException("Invalid Firebase token");
        }
    }
}
