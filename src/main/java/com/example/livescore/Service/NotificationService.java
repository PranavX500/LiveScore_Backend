package com.example.livescore.Service;

import com.example.livescore.Model.AppNotification;
import com.example.livescore.Model.Match;
import com.example.livescore.Model.Tournament;
import com.example.livescore.Model.User;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private static final String USERS = "users";
    private static final String SUB_NOTIFICATIONS = "notifications";

    private final FirebaseService firebaseService;

    public void saveDeviceToken(String uid, String token) throws Exception {

        String normalizedToken = token == null ? null : token.trim();
        if (normalizedToken == null || normalizedToken.isEmpty()) {
            throw new IllegalArgumentException("token is required");
        }

        User user = firebaseService.get(USERS, uid, User.class);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        List<String> fcmTokens = user.getFcmTokens();
        if (fcmTokens == null) {
            fcmTokens = new ArrayList<>();
        }

        if (!fcmTokens.contains(normalizedToken)) {
            fcmTokens.add(normalizedToken);
            user.setFcmTokens(fcmTokens);
            firebaseService.save(USERS, uid, user);
        }
    }

    public List<AppNotification> getNotifications(String uid) throws Exception {

        List<QueryDocumentSnapshot> docs = firebaseService.getFirestore()
                .collection(USERS)
                .document(uid)
                .collection(SUB_NOTIFICATIONS)
                .orderBy("createdAt")
                .get()
                .get()
                .getDocuments();

        List<AppNotification> notifications = new ArrayList<>();
        for (int i = docs.size() - 1; i >= 0; i--) {
            QueryDocumentSnapshot doc = docs.get(i);
            AppNotification notification = doc.toObject(AppNotification.class);
            if (notification.getId() == null) {
                notification.setId(doc.getId());
            }
            if (notification.getUserId() == null) {
                notification.setUserId(uid);
            }
            notifications.add(notification);
        }

        return notifications;
    }

    public void markAsRead(String uid, String notificationId) throws Exception {

        firebaseService.getFirestore()
                .collection(USERS)
                .document(uid)
                .collection(SUB_NOTIFICATIONS)
                .document(notificationId)
                .update("read", true)
                .get();
    }

    public void notifyMatchStarted(Tournament tournament, Match match) throws Exception {

        List<User> users = firebaseService.getAll(USERS, User.class);
        if (users.isEmpty()) {
            return;
        }

        String title = "Match started";
        String tournamentName = tournament != null ? tournament.getName() : "Tournament";
        String body = match.getTeamAName() + " vs " + match.getTeamBName()
                + " has started in " + tournamentName + ".";

        Set<String> tokens = new LinkedHashSet<>();

        for (User user : users) {
            if (user == null || user.getId() == null) {
                continue;
            }

            AppNotification notification = AppNotification.builder()
                    .id(UUID.randomUUID().toString())
                    .userId(user.getId())
                    .title(title)
                    .body(body)
                    .type("MATCH_STARTED")
                    .tournamentId(match.getTournamentId())
                    .matchId(match.getId())
                    .read(false)
                    .createdAt(Date.from(Instant.now()))
                    .build();

            firebaseService.saveSub(
                    USERS,
                    user.getId(),
                    SUB_NOTIFICATIONS,
                    notification.getId(),
                    notification
            );

            if (user.getFcmTokens() != null) {
                for (String token : user.getFcmTokens()) {
                    if (token != null && !token.isBlank()) {
                        tokens.add(token);
                    }
                }
            }
        }

        if (tokens.isEmpty()) {
            return;
        }

        try {
            MulticastMessage message = MulticastMessage.builder()
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .putData("type", "MATCH_STARTED")
                    .putData("tournamentId", safe(match.getTournamentId()))
                    .putData("matchId", safe(match.getId()))
                    .addAllTokens(tokens)
                    .build();

            FirebaseMessaging.getInstance().sendEachForMulticast(message);
        } catch (Exception e) {
            log.error("Failed to send match started push notification", e);
        }
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}
