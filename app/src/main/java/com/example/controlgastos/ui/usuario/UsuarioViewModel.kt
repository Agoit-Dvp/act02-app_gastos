package com.example.controlgastos.ui.usuario

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.controlgastos.data.model.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UsuarioViewModel : ViewModel() {

    private val _usuario = MutableLiveData<Usuario?>()
    val usuario: LiveData<Usuario?> = _usuario

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun cargarDatosUsuario() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        FirebaseFirestore.getInstance()
            .collection("usuarios")
            .document(uid)
            .get()
            .addOnSuccessListener { document ->
                val usuario = document.toObject(Usuario::class.java)
                _usuario.value = usuario
                _error.value = null
            }
            .addOnFailureListener {
                _error.value = it.message
            }
    }
}