package com.example.controlgastos.data.repository

import android.util.Log
import com.example.controlgastos.data.model.Usuario
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UsuarioRepository {

    private val db = FirebaseFirestore.getInstance()
    private val usuariosCollection = db.collection("usuarios")

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

    fun obtenerTodosLosUsuarios(onResult: (List<Usuario>?, String?) -> Unit) {
        usuariosCollection.get()
            .addOnSuccessListener { result ->
                val usuarios = result.toObjects(Usuario::class.java)
                onResult(usuarios, null)
            }
            .addOnFailureListener { e ->
                onResult(null, e.message)
            }
    }

    //Obtener usuario por id
    fun obtenerNombrePorUid(uid: String, onResult: (String?) -> Unit) {
        usuariosCollection
            .document(uid)
            .get()
            .addOnSuccessListener { doc ->
                val nombre = doc.getString("nombre")
                onResult(nombre)
            }
            .addOnFailureListener {
                Log.e("Firestore", "Error al obtener nombre del usuario", it)
                onResult(null)
            }
    }

    //Funciones suspendidas
    suspend fun guardarUsuarioSuspendido(usuario: Usuario) {
        usuariosCollection
            .document(usuario.uid)
            .set(usuario)
            .await()
    }

    suspend fun obtenerUsuarioSuspendido(uid: String): Usuario? {
        val snapshot = usuariosCollection
            .document(uid)
            .get()
            .await()

        return snapshot.toObject(Usuario::class.java)
    }

    suspend fun obtenerTodosLosUsuariosSuspendido(): List<Usuario> {
        val snapshot = usuariosCollection
            .get()
            .await()

        return snapshot.toObjects(Usuario::class.java)
    }

    suspend fun obtenerNombrePorUidSuspendido(uid: String): String? {
        val snapshot = usuariosCollection
            .document(uid)
            .get()
            .await()

        return snapshot.getString("nombre")
    }

    suspend fun obtenerUsuariosPorIds(uids: List<String>): List<Usuario> {
        return try {
            if (uids.isEmpty()) return emptyList()

            val snapshot = usuariosCollection
                .whereIn("uid", uids)
                .get()
                .await()

            snapshot.toObjects(Usuario::class.java)
        } catch (e: Exception) {
            Log.e("Firestore", "Error al obtener usuarios por IDs", e)
            emptyList()
        }
    }
}