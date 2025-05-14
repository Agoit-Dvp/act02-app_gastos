package com.example.controlgastos.data

import com.google.firebase.auth.FirebaseAuth

class AuthRepository {
    private val auth = FirebaseAuth.getInstance()

    fun loginUser(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) onResult(true, null)
                else onResult(false, it.exception?.message)
            }
    }

    fun register(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) onResult(true, null)
                else onResult(false, it.exception?.message)
            }
    }

    fun currentUser() = auth.currentUser
    fun logout() = auth.signOut()
}