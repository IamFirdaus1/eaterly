package com.dausinvestama.eaterly.utils

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.FirebaseFirestoreException

object FirestoreUtils {
    fun getOneOrFail(
        collection: String,
        field: String,
        value: String,
        onSuccess: (DocumentSnapshot) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val firestore = FirebaseFirestore.getInstance()

        firestore.collection(collection)
            .whereEqualTo(field, value)
            .limit(2) // Limit to 2 to check for multiple documents
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.size() == 1) {
                    // Exactly one document found
                    onSuccess(querySnapshot.documents.first())
                } else if (querySnapshot.isEmpty) {
                    // No documents found
                    onFailure(Exception("Document not found"))
                } else {
                    // Multiple documents found when only one was expected
                    onFailure(Exception("More than one document found"))
                }
            }
            .addOnFailureListener { e ->
                // Error occurred while fetching document
                onFailure(e)
            }
    }

// Usage

}