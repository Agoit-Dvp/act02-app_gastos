package com.example.controlgastos.ui.usuario

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.controlgastos.data.model.Usuario
import com.example.controlgastos.data.repository.UsuarioRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class UsuarioViewModel(
    private val repository: UsuarioRepository = UsuarioRepository()
) : ViewModel() {

    private val _usuario = MutableLiveData<Usuario?>()
    val usuario: LiveData<Usuario?> = _usuario

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun cargarDatosUsuario() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        repository.obtenerUsuario(uid) { usuario, errorMsg ->
            if (errorMsg != null) {
                _error.value = errorMsg
            } else {
                _usuario.value = usuario
                _error.value = null
            }
        }
    }

    fun guardarUsuario(usuario: Usuario) {
        repository.guardarUsuario(usuario) { success, errorMsg ->
            if (success) {
                _usuario.value = usuario
            } else {
                _error.value = errorMsg
            }
        }
    }
}