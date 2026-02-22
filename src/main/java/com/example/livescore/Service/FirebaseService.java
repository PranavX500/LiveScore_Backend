package com.example.livescore.Service;

import com.example.livescore.Model.TeamJoinRequest;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    public <T> List<T> getAll(String collection, Class<T> clazz) throws Exception {

        return firestore.collection(collection)
                .get()
                .get()
                .getDocuments()
                .stream()
                .map(doc -> doc.toObject(clazz))
                .collect(Collectors.toList());
    }
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




}
