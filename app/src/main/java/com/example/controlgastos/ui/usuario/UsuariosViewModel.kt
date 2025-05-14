package com.example.controlgastos.ui.usuario

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.controlgastos.data.model.Usuario
import com.example.controlgastos.data.repository.UsuarioRepository

class UsuariosViewModel(
    private val repository: UsuarioRepository = UsuarioRepository()
) : ViewModel() {

    private val _usuarios = MutableLiveData<List<Usuario>>()
    val usuarios: LiveData<List<Usuario>> = _usuarios

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun cargarUsuarios() {
        repository.obtenerTodosLosUsuarios { lista, errorMsg ->
            if (errorMsg != null) {
                _error.value = errorMsg
            } else {
                _usuarios.value = lista ?: emptyList()
            }
        }
    }
}