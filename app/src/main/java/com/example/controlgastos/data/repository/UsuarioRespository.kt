package com.example.controlgastos.data.repository

import com.example.controlgastos.data.model.Usuario
import com.google.firebase.firestore.FirebaseFirestore

class UsuarioRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val usuariosCollection = firestore.collection("usuarios")

    fun obtenerUsuario(uid: String, onResult: (Usuario?, String?) -> Unit) {
        usuariosCollection.document(uid)
            .get()
            .addOnSuccessListener { document ->
                val usuario = document.toObject(Usuario::class.java)
                onResult(usuario, null)
            }
            .addOnFailureListener { e ->
                onResult(null, e.message)
            }
    }

    fun guardarUsuario(usuario: Usuario, onResult: (Boolean, String?) -> Unit) {
        usuariosCollection.document(usuario.uid)
            .set(usuario)
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { e -> onResult(false, e.message) }
    }
}