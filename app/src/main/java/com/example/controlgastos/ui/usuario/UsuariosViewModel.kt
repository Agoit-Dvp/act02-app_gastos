package com.example.controlgastos.ui.usuario

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.controlgastos.data.model.Usuario
import com.example.controlgastos.data.repository.AccesoPlanFinancieroRepository
import com.example.controlgastos.data.repository.UsuarioRepository
import kotlinx.coroutines.launch

class UsuariosViewModel(
    private val repository: UsuarioRepository = UsuarioRepository(),
    private val accesoRepository: AccesoPlanFinancieroRepository = AccesoPlanFinancieroRepository()
) : ViewModel() {

    private val _usuarios = MutableLiveData<List<Usuario>>()
    val usuarios: LiveData<List<Usuario>> = _usuarios

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _mensaje = MutableLiveData<String?>()
    val mensaje: LiveData<String?> = _mensaje

/*    fun cargarUsuarios() {
        repository.obtenerTodosLosUsuarios { lista, errorMsg ->
            if (errorMsg != null) {
                _error.value = errorMsg
            } else {
                _usuarios.value = lista ?: emptyList()
            }
        }
    }*/

    fun cargarUsuariosDelPlan(planId: String) {
        viewModelScope.launch {
            try {
                val accesos = accesoRepository.obtenerAccesosPorPlan(planId)
                val uids = accesos.map { it.usuarioId }.distinct()

                if (uids.isEmpty()) {
                    _usuarios.value = emptyList()
                    return@launch
                }

                val lista =
                    repository.obtenerUsuariosPorIds(uids) // ✅ Esta función debe ser suspendida también

                _usuarios.value = lista

            } catch (e: Exception) {
                _error.value = e.message ?: "Error al cargar usuarios del plan"
            }
        }
    }

    fun invitarUsuario(email: String, planId: String) {
        viewModelScope.launch {
            try {
                val usuarioId = accesoRepository.buscarUsuarioPorEmail(email)

                if (usuarioId == null) {
                    _error.value = "No se encontró ningún usuario con ese correo electrónico."
                    _mensaje.value = "No se encontró ningún usuario con ese correo electrónico."
                    return@launch
                }

                val exito = accesoRepository.invitarUsuarioAPlan(usuarioId, planId)

                if (exito) {
                    cargarUsuariosDelPlan(planId)
                    _mensaje.value = "Usuario invitado correctamente"
                } else {
                    _error.value = "Este usuario ya ha sido invitado o ya pertenece al plan."
                    _mensaje.value = "Usuario invitado correctamente"
                }

            } catch (e: Exception) {
                _error.value = e.message ?: "Error al invitar usuario al plan."
            }
        }
    }

    //funciones auxiliares
    fun limpiarMensaje() {
        _mensaje.value = null
    }
}