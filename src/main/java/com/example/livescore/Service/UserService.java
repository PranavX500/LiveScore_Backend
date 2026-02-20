package com.example.livescore.Service;

import com.example.livescore.Dto.SignupRequest;
import com.example.livescore.Model.Role;
import com.example.livescore.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {

    private final FirebaseService firebaseService;
    private static final String COLLECTION = "users";

    /* ---------- SIGNUP (CREATE AUTH + PROFILE) ---------- */
    public User signup(SignupRequest request) throws Exception {

        // 1️⃣ Create Firebase Auth user
        UserRecord.CreateRequest createRequest = new UserRecord.CreateRequest()
                .setEmail(request.getEmail())
                .setPassword(request.getPassword())
                .setDisplayName(request.getName());

        UserRecord userRecord = FirebaseAuth.getInstance().createUser(createRequest);
        String uid = userRecord.getUid();

        // 🔥 2️⃣ ADD ROLE INTO FIREBASE TOKEN (CUSTOM CLAIM)
        FirebaseAuth.getInstance().setCustomUserClaims(uid, Map.of(
                "role", "USER"
        ));

        // 3️⃣ Create Firestore profile
        User user = User.builder()
                .id(uid)
                .name(request.getName())
                .email(request.getEmail())
                .photoUrl(request.getPhotoUrl())
                .role(String.valueOf(Role.USER))        // <-- store enum, not string
                .createdAt(Date.from(Instant.now()))
                .build();

        firebaseService.save(COLLECTION, uid, user);

        return user;
    }


    /* ---------- UPDATE PROFILE ---------- */
    public User updateProfile(String uid, User request) throws Exception {

        User existing = firebaseService.get(COLLECTION, uid, User.class);

        if (existing == null) {
            throw new RuntimeException("User does not exist");
        }

        // preserve protected fields
        request.setId(uid);
        request.setRole(Role.valueOf(existing.getRole()));
        request.setCreatedAt(existing.getCreatedAt());
        request.setEmail(existing.getEmail());

        firebaseService.save(COLLECTION, uid, request);

        return request;
    }

    /* ---------- GET USER ---------- */
    public User getUser(String uid) throws Exception {
        return firebaseService.get(COLLECTION, uid, User.class);
    }

    /* ---------- DELETE USER ---------- */
    public void deleteUser(String uid) throws Exception {
        firebaseService.delete(COLLECTION, uid);
        FirebaseAuth.getInstance().deleteUser(uid);
    }
}
