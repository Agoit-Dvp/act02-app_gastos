package com.example.controlgastos.data.repository

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class AuthRepository {
    private val auth = FirebaseAuth.getInstance()

    suspend fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .await()
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