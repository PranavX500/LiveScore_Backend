package com.example.livescore.Service;

import com.google.cloud.firestore.Firestore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class FirebaseService {

    private final Firestore firestore;

    /* ---------- CREATE / UPDATE ---------- */

    public void save(String collection, String documentId, Object data) throws Exception {
        firestore.collection(collection)
                .document(documentId)
                .set(data)
                .get();
    }

    /* ---------- GET DOCUMENT ---------- */

    public <T> T get(String collection, String documentId, Class<T> clazz) throws Exception {
        return firestore.collection(collection)
                .document(documentId)
                .get()
                .get()
                .toObject(clazz);
    }

    /* ---------- DELETE ---------- */

    public void delete(String collection, String documentId) throws Exception {
        firestore.collection(collection)
                .document(documentId)
                .delete()
                .get();
    }

    /* ---------- SUBCOLLECTION SAVE ---------- */

    public void saveSub(
            String collection,
            String documentId,
            String subcollection,
            String subId,
            Object data
    ) throws Exception {

        firestore.collection(collection)
                .document(documentId)
                .collection(subcollection)
                .document(subId)
                .set(data)
                .get();
    }

    /* ---------- SUBCOLLECTION DELETE ---------- */

    public void deleteSub(
            String collection,
            String documentId,
            String subcollection,
            String subId
    ) throws Exception {

        firestore.collection(collection)
                .document(documentId)
                .collection(subcollection)
                .document(subId)
                .delete()
                .get();
    }
}
