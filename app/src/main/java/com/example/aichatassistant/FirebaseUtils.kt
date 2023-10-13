package com.example.aichatassistant

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
object FirebaseUtils {
    var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    var firebaseUser: FirebaseUser? = firebaseAuth.currentUser
}