package com.example.controlgastos.ui.planfinanciero

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.controlgastos.data.model.AccesoPlanFinanciero
import com.example.controlgastos.data.model.PlanFinanciero
import com.example.controlgastos.data.repository.AccesoPlanFinancieroRepository
import com.example.controlgastos.data.repository.PlanFinancieroRepository
import com.example.controlgastos.data.repository.UsuarioRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Date

class PlanesViewModel : ViewModel() {

    private val repoPlanes = PlanFinancieroRepository()
    private val repoAccesos = AccesoPlanFinancieroRepository()
    private val repoUsuarios = UsuarioRepository()

    private val _planes = MutableStateFlow<List<PlanFinanciero>>(emptyList())
    val planes: StateFlow<List<PlanFinanciero>> = _planes

    private val _accesos = MutableStateFlow<List<AccesoPlanFinanciero>>(emptyList())
    val accesos: StateFlow<List<AccesoPlanFinanciero>> = _accesos

    private val _invitaciones = MutableStateFlow<List<AccesoPlanFinanciero>>(emptyList())
    val invitaciones: StateFlow<List<AccesoPlanFinanciero>> = _invitaciones

    private val _nombresCreadores = MutableStateFlow<Map<String, String>>(emptyMap())
    val nombresCreadores: StateFlow<Map<String, String>> = _nombresCreadores

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val currentUserId: String
        get() = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()

    fun cargarDatos() {
        cargarPlanesDelUsuario()
        cargarInvitacionesPendientes()
    }

    fun cargarPlanesDelUsuario() {
        viewModelScope.launch {
            _isLoading.value = true
            Log.d("PlanesVM", "Planes cargados:")
            val planesList = repoPlanes.obtenerPlanesDeUsuario(currentUserId) // ✅ Nuevo uso de función suspend
            _planes.value = planesList

            val accesos = repoAccesos.obtenerAccesosDeUsuario(currentUserId)
            _accesos.value = accesos // ✅ Puedes mantener esto si necesitas saber qué accesos tiene

            cargarNombresCreadores(planesList)

            _isLoading.value = false
        }
    }

    private fun cargarNombresCreadores(planes: List<PlanFinanciero>) {
        val actuales = _nombresCreadores.value?.toMutableMap() ?: mutableMapOf()

        val uidsFaltantes = planes.map { it.creadorId }
            .distinct()
            .filter { !actuales.containsKey(it) && it.isNotBlank() }

        if (uidsFaltantes.isEmpty()) return

        uidsFaltantes.forEach { uid ->
            repoUsuarios.obtenerNombrePorUid(uid) { nombre ->
                if (nombre != null) {
                    actuales[uid] = nombre
                    _nombresCreadores.value = actuales
                }
            }
        }
    }

    fun cargarInvitacionesPendientes() {
        repoAccesos.obtenerInvitacionesPendientes(currentUserId) { accesosList ->
            _invitaciones.value = accesosList
        }
    }

    fun crearNuevoPlan(nombre: String, descripcion: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val nuevoPlan = PlanFinanciero(
                    nombre = nombre,
                    descripcion = descripcion,
                    creadorId = currentUserId,
                    fechaCreacion = Date()
                )

                // Usamos la versión suspendida aquí
                repoPlanes.crearPlanSuspendido(nuevoPlan)

                // Recargar planes tras crear
                cargarPlanesDelUsuario()

            } catch (e: Exception) {
                Log.e("PlanesVM", "Error al crear plan", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun eliminarAcceso(usuarioId: String, planId: String) {
        _isLoading.value = true
        repoAccesos.eliminarAcceso(usuarioId, planId) { exito ->
            if (exito) cargarPlanesDelUsuario()
            else _isLoading.value = false
        }
    }

    fun aceptarInvitacion(planId: String) {
        repoAccesos.actualizarEstado(currentUserId, planId, "aceptado") { exito ->
            if (exito) {
                cargarPlanesDelUsuario()
                cargarInvitacionesPendientes()
            }
        }
    }

    fun rechazarInvitacion(planId: String) {
        repoAccesos.actualizarEstado(currentUserId, planId, "rechazado") { exito ->
            if (exito) cargarInvitacionesPendientes()
        }
    }

    //Actualizar plan
    fun actualizarPlan(plan: PlanFinanciero) {
        _isLoading.value = true
        repoPlanes.actualizarPlan(plan) { exito ->
            if (exito) {
                cargarPlanesDelUsuario()
            } else {
                _isLoading.value = false
            }
        }
    }

    //Limpiar estados
    fun limpiar() {
        _planes.value = emptyList()
        _accesos.value = emptyList()
        _invitaciones.value = emptyList()
        _nombresCreadores.value = emptyMap()
        _isLoading.value = false
    }
}
