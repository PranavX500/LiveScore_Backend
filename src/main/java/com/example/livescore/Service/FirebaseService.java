package com.example.livescore.Service;

import com.google.cloud.firestore.FieldPath;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FirebaseService {

    private final Firestore firestore;

    // ✅ added for Map ↔ Object conversion
    private final ObjectMapper objectMapper = new ObjectMapper();

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

    /* ---------- GET SUB DOCUMENT ---------- */

    public <T> T getSub(
            String collection,
            String documentId,
            String subcollection,
            String subId,
            Class<T> clazz
    ) throws Exception {

        return firestore.collection(collection)
                .document(documentId)
                .collection(subcollection)
                .document(subId)
                .get()
                .get()
                .toObject(clazz);
    }

    /* ---------- GET COLLECTION ---------- */

    public <T> List<T> getAll(String collection, Class<T> clazz) throws Exception {

        return firestore.collection(collection)
                .get()
                .get()
                .getDocuments()
                .stream()
                .map(doc -> doc.toObject(clazz))
                .collect(Collectors.toList());
    }

    /* ---------- GET SUBCOLLECTION ---------- */

    public <T> List<T> getAllSub(
            String collection,
            String documentId,
            String subcollection,
            Class<T> clazz
    ) throws Exception {

        List<QueryDocumentSnapshot> docs =
                firestore.collection(collection)
                        .document(documentId)
                        .collection(subcollection)
                        .get()
                        .get()
                        .getDocuments();

        List<T> result = new ArrayList<>();
        for (QueryDocumentSnapshot doc : docs) {
            result.add(doc.toObject(clazz));
        }
        return result;
    }

    // =====================================================
    // 🔥 CONVERT FIRESTORE MAP → JAVA CLASS
    // =====================================================
    public <T> T convert(Object source, Class<T> clazz) {
        return objectMapper.convertValue(source, clazz);
    }
    public <T> T getCollectionGroupByDocId(
            String collection,
            String docId,
            Class<T> clazz
    ) throws Exception {

        var query = firestore.collectionGroup(collection)
                .whereEqualTo(FieldPath.documentId(), docId)
                .get()
                .get();

        if (query.isEmpty())
            throw new RuntimeException("Document not found");

        return query.getDocuments().get(0).toObject(clazz);
    }
    public Firestore getFirestore() {
        return firestore;
    }
}