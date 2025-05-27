package com.example.controlgastos.ui.usuario

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.controlgastos.data.model.AccesoPlanFinanciero
import com.example.controlgastos.data.model.Usuario
import com.example.controlgastos.data.repository.AccesoPlanFinancieroRepository
import com.example.controlgastos.data.repository.UsuarioRepository
import com.google.firebase.auth.FirebaseAuth
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

    private val _accesos = MutableLiveData<List<AccesoPlanFinanciero>>()
    val accesos: LiveData<List<AccesoPlanFinanciero>> = _accesos

    private val _currentUserId: String
        get() = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
    val currentUserId: LiveData<String> = MutableLiveData(_currentUserId)

    private val _rolUsuarioActual = MutableLiveData<String>()
    val rolUsuarioActual: LiveData<String> = _rolUsuarioActual

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
                //Usuarios vinculados al plan
                val lista =
                    repository.obtenerUsuariosPorIds(uids) // ✅ Esta función debe ser suspendida también

                _usuarios.value = lista

                // Guardar el rol del usuario actual (para saber si puede eliminar)
                val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
                val accesoPropio = accesos.find { it.usuarioId == currentUserId }
                _rolUsuarioActual.value = accesoPropio?.rol

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
                // Buscar si ya hay un acceso existente
                val accesoExistente = accesoRepository.obtenerAcceso(planId, usuarioId)

                if (accesoExistente != null) {
                    when (accesoExistente.estado) {
                        "rechazado" -> {
                            accesoRepository.actualizarEstado(usuarioId, planId, "pendiente") { actualizado ->
                                if (actualizado) {
                                    cargarUsuariosDelPlan(planId)
                                    _mensaje.value = "Invitación reenviada correctamente."
                                } else {
                                    _error.value = "Error al reenviar la invitación."
                                }
                            }
                        }
                        "pendiente", "aceptado" -> {
                            _mensaje.value = "Este usuario ya ha sido invitado o pertenece al plan."
                        }
                        else -> {
                            _mensaje.value = "El usuario ya tiene un acceso registrado."
                        }
                    }
                    return@launch
                }

                val exito = accesoRepository.invitarUsuarioAPlan(usuarioId, planId)

                if (exito) {
                    cargarUsuariosDelPlan(planId)
                    _mensaje.value = "Usuario invitado correctamente"
                } else {
                    _mensaje.value = "Este usuario ya ha sido invitado o ya pertenece al plan."
                }

            } catch (e: Exception) {
                _error.value = e.message ?: "Error al invitar usuario al plan."
            }
        }
    }

    fun cargarAccesosDelPlan(planId: String) {
        viewModelScope.launch {
            try {
                val accesos = accesoRepository.obtenerAccesosPorPlan(planId)
                _accesos.value = accesos
            } catch (e: Exception) {
                _error.value = "Error al obtener accesos"
            }
        }
    }

    fun eliminarAcceso(usuarioId: String, planId: String) {
        viewModelScope.launch {
            try {
                accesoRepository.eliminarAcceso(usuarioId, planId)
                cargarUsuariosDelPlan(planId)
                _mensaje.value = "Acceso eliminado correctamente."
            } catch (e: Exception) {
                _error.value = "Error al eliminar acceso: ${e.message}"
            }
        }
    }

    //funciones auxiliares
    fun limpiarMensaje() {
        _mensaje.value = null
    }
}