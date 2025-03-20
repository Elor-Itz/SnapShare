package com.example.snapshare.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class UserRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    fun registerUser(email: String, password: String, callback: (FirebaseUser?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(auth.currentUser)
                } else {
                    callback(null)
                }
            }
    }

    fun loginUser(email: String, password: String, callback: (FirebaseUser?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(auth.currentUser)
                } else {
                    callback(null)
                }
            }
    }

    fun logoutUser() {
        auth.signOut()
    }
}