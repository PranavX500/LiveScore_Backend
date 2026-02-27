package com.example.livescore.Service;

import com.example.livescore.Dto.SignupRequest;
import com.example.livescore.Model.EmailOtp;
import com.example.livescore.Model.Role;
import com.example.livescore.Model.User;
import com.google.cloud.firestore.Firestore;
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
    private final Firestore firestore;

    /* ---------- SIGNUP ---------- */
    public User signup(SignupRequest request) throws Exception {

        // 1️⃣ Create Firebase Auth user
        UserRecord.CreateRequest createRequest = new UserRecord.CreateRequest()
                .setEmail(request.getEmail())
                .setPassword(request.getPassword())
                .setDisplayName(request.getName());

        UserRecord userRecord = FirebaseAuth.getInstance().createUser(createRequest);
        String uid = userRecord.getUid();

        // 2️⃣ Default role = USER (Firebase claim optional)
        FirebaseAuth.getInstance().setCustomUserClaims(uid,
                Map.of("role", "USER"));

        // 3️⃣ Firestore profile
        User user = User.builder()
                .id(uid)
                .name(request.getName())
                .email(request.getEmail())
                .photoUrl(request.getPhotoUrl())
                .role(String.valueOf(Role.USER))   // ✅ enum
                .createdAt(Date.from(Instant.now()))
                .build();

        firebaseService.save(COLLECTION, uid, user);

        return user;
    }
    public void makeAdmin(String uid) throws Exception {

        User user = getUser(uid);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        // 1️⃣ update Firestore role
        user.setRole(Role.ADMIN);
        firebaseService.save(COLLECTION, uid, user);

        // 2️⃣ update Firebase custom claim
        FirebaseAuth.getInstance().setCustomUserClaims(uid,
                Map.of("role", Role.ADMIN.name()));
    }

    /* ---------- UPDATE PROFILE ---------- */
    public User updateProfile(String uid, User request) throws Exception {

        User existing = getUser(uid);

        if (existing == null) {
            throw new RuntimeException("User does not exist");
        }

        request.setId(uid);
        request.setRole(Role.valueOf(existing.getRole()));      // preserve role
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

    /* ---------- ROLE UPDATE (TEAM LOGIC) ---------- */
    public void updateRole(String uid, Role role) throws Exception {

        User user = getUser(uid);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        user.setRole(role);

        firebaseService.save(COLLECTION, uid, user);

        // optional: sync Firebase claim
        FirebaseAuth.getInstance().setCustomUserClaims(uid,
                Map.of("role", role.name()));
    }
    public User createOrGetByEmail(String email) throws Exception {

        var doc = firestore.collection("users")
                .document(email)
                .get()
                .get();

        if (doc.exists())
            return doc.toObject(User.class);

        User user = User.builder()
                .email(email)
                .createdAt(new Date())
                .role("PLAYER")
                .build();

        firestore.collection("users")
                .document(email)
                .set(user);

        return user;
    }
    public User createAfterOtp(EmailOtp data) throws Exception {

        // 1️⃣ Firebase Auth user
        UserRecord.CreateRequest createRequest = new UserRecord.CreateRequest()
                .setEmail(data.getEmail())
                .setPassword(data.getPassword())
                .setDisplayName(data.getName());

        UserRecord userRecord =
                FirebaseAuth.getInstance().createUser(createRequest);

        String uid = userRecord.getUid();

        // 2️⃣ Default role USER
        FirebaseAuth.getInstance().setCustomUserClaims(uid,
                Map.of("role", Role.USER.name()));

        // 3️⃣ Firestore profile
        User user = User.builder()
                .id(uid)
                .name(data.getName())
                .email(data.getEmail())
                .photoUrl(data.getPhotoUrl())
                .role(Role.USER.name())   // ✅ USER initial
                .createdAt(Date.from(Instant.now()))
                .build();

        firebaseService.save(COLLECTION, uid, user);

        return user;
    }


}