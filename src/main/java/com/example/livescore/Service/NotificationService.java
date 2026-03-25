package com.example.livescore.Service;

import com.example.livescore.Model.AppNotification;
import com.example.livescore.Model.Match;
import com.example.livescore.Model.Tournament;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.firebase.auth.ExportedUserRecord;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.ListUsersPage;
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
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private static final String USERS = "users";
    private static final String SUB_NOTIFICATIONS = "notifications";

    private final FirebaseService firebaseService;
    private final EmailService emailService;

    public void saveDeviceToken(String uid, String token) throws Exception {

        String normalizedToken = token == null ? null : token.trim();
        if (normalizedToken == null || normalizedToken.isEmpty()) {
            throw new IllegalArgumentException("token is required");
        }

        DocumentSnapshot doc = firebaseService.getFirestore()
                .collection(USERS)
                .document(uid)
                .get()
                .get();

        if (!doc.exists()) {
            throw new RuntimeException("User not found");
        }

        List<String> fcmTokens = extractTokens(doc.getData());

        if (!fcmTokens.contains(normalizedToken)) {
            fcmTokens.add(normalizedToken);
            firebaseService.getFirestore()
                    .collection(USERS)
                    .document(uid)
                    .update("fcmTokens", fcmTokens)
                    .get();
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

        List<QueryDocumentSnapshot> userDocs = firebaseService.getFirestore()
                .collection(USERS)
                .get()
                .get()
                .getDocuments();

        if (userDocs.isEmpty()) {
            return;
        }

        String title = "Match started";
        String tournamentName = tournament != null ? tournament.getName() : "Tournament";
        String matchLabel = match.getTeamAName() + " vs " + match.getTeamBName();
        String body = matchLabel
                + " has started in " + tournamentName + ".";

        Set<String> tokens = new LinkedHashSet<>();
        Set<String> emails = new LinkedHashSet<>();

        for (QueryDocumentSnapshot userDoc : userDocs) {
            String userId = userDoc.getId();

            AppNotification notification = AppNotification.builder()
                    .id(UUID.randomUUID().toString())
                    .userId(userId)
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
                    userId,
                    SUB_NOTIFICATIONS,
                    notification.getId(),
                    notification
            );

            for (String token : extractTokens(userDoc.getData())) {
                if (token != null && !token.isBlank()) {
                    tokens.add(token);
                }
            }

            String email = extractEmail(userDoc.getData());
            if (email != null) {
                emails.add(email);
            }
        }

        addFirebaseAuthEmails(emails);
        log.info("Match start email recipients count={} tournamentId={} matchId={}",
                emails.size(), match.getTournamentId(), match.getId());

        for (String email : emails) {
            try {
                log.info("Sending match start email to={} tournamentId={} matchId={}",
                        email, match.getTournamentId(), match.getId());
                emailService.sendMatchStartedEmail(email, tournamentName, matchLabel);
            } catch (Exception e) {
                log.error("Failed to send match started email to {}", email, e);
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

    private List<String> extractTokens(Map<String, Object> data) {
        List<String> tokens = new ArrayList<>();
        if (data == null) {
            return tokens;
        }

        Object rawTokens = data.get("fcmTokens");
        if (!(rawTokens instanceof List<?> tokenList)) {
            return tokens;
        }

        for (Object token : tokenList) {
            if (token instanceof String value && !value.isBlank()) {
                tokens.add(value);
            }
        }

        return tokens;
    }

    private String extractEmail(Map<String, Object> data) {
        if (data == null) {
            return null;
        }

        Object rawEmail = data.get("email");
        if (rawEmail instanceof String email && !email.isBlank()) {
            return email.trim();
        }

        return null;
    }

    private void addFirebaseAuthEmails(Set<String> emails) {
        try {
            ListUsersPage page = FirebaseAuth.getInstance().listUsers(null);
            while (page != null) {
                for (ExportedUserRecord user : page.getValues()) {
                    String email = user.getEmail();
                    if (email != null && !email.isBlank()) {
                        emails.add(email.trim());
                    }
                }
                page = page.getNextPage();
            }
        } catch (Exception e) {
            log.error("Failed to load Firebase Auth emails for match start notification", e);
        }
    }
}
