package com.example.aichatassistant

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

object FirebaseUtils {
    var firebaseFireStore: FirebaseFirestore = FirebaseFirestore.getInstance()
    var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    var firebaseUser: FirebaseUser? = firebaseAuth.currentUser
}