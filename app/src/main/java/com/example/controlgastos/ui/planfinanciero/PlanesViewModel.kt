package com.example.controlgastos.ui.planfinanciero

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.controlgastos.data.model.AccesoPlanFinanciero
import com.example.controlgastos.data.model.PlanFinanciero
import com.example.controlgastos.data.repository.AccesoPlanFinancieroRepository
import com.example.controlgastos.data.repository.PlanFinancieroRepository
import com.example.controlgastos.data.repository.UsuarioRepository

class PlanesViewModel : ViewModel() {

    private val repoPlanes = PlanFinancieroRepository()
    private val repoAccesos = AccesoPlanFinancieroRepository()
    private val repoUsuarios = UsuarioRepository()

    private val _planes = MutableLiveData<List<PlanFinanciero>>(emptyList())
    val planes: LiveData<List<PlanFinanciero>> = _planes

    private val _accesos = MutableLiveData<List<AccesoPlanFinanciero>>(emptyList())
    val accesos: LiveData<List<AccesoPlanFinanciero>> = _accesos

    private val _invitaciones = MutableLiveData<List<AccesoPlanFinanciero>>(emptyList())
    val invitaciones: LiveData<List<AccesoPlanFinanciero>> = _invitaciones

    private val _nombresCreadores = MutableLiveData<Map<String, String>>(emptyMap())
    val nombresCreadores: LiveData<Map<String, String>> = _nombresCreadores

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    fun cargarDatos(usuarioId: String) {
        cargarPlanesDelUsuario(usuarioId)
        cargarInvitacionesPendientes(usuarioId)
    }

    fun cargarPlanesDelUsuario(usuarioId: String) {
        _isLoading.value = true

        repoAccesos.obtenerAccesosDeUsuario(usuarioId) { accesosList ->
            _accesos.value = accesosList

            val ids = accesosList.map { it.planId }
            if (ids.isEmpty()) {
                _planes.value = emptyList()
                _isLoading.value = false
                return@obtenerAccesosDeUsuario
            }

            repoPlanes.obtenerPlanesPorIds(ids) { planesList ->
                _planes.value = planesList
                cargarNombresCreadores(planesList)
                _isLoading.value = false
            }
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
                    _nombresCreadores.postValue(actuales)
                }
            }
        }
    }

    fun cargarInvitacionesPendientes(usuarioId: String) {
        repoAccesos.obtenerInvitacionesPendientes(usuarioId) { accesosList ->
            _invitaciones.value = accesosList
        }
    }

    fun crearNuevoPlan(plan: PlanFinanciero) {
        _isLoading.value = true
        repoPlanes.crearPlan(plan) { exito, _ ->
            if (exito) {
                cargarPlanesDelUsuario(plan.creadorId)
            } else {
                _isLoading.value = false
            }
        }
    }

    fun eliminarAcceso(usuarioId: String, planId: String) {
        _isLoading.value = true
        repoAccesos.eliminarAcceso(usuarioId, planId) { exito ->
            if (exito) cargarPlanesDelUsuario(usuarioId)
            else _isLoading.value = false
        }
    }

    fun aceptarInvitacion(planId: String, usuarioId: String) {
        repoAccesos.actualizarEstado(usuarioId, planId, "aceptado") { exito ->
            if (exito) {
                cargarPlanesDelUsuario(usuarioId)
                cargarInvitacionesPendientes(usuarioId)
            }
        }
    }

    fun rechazarInvitacion(planId: String, usuarioId: String) {
        repoAccesos.actualizarEstado(usuarioId, planId, "rechazado") { exito ->
            if (exito) cargarInvitacionesPendientes(usuarioId)
        }
    }

    //Actualizar plan
    fun actualizarPlan(plan: PlanFinanciero) {
        _isLoading.value = true
        repoPlanes.actualizarPlan(plan) { exito ->
            if (exito) {
                cargarPlanesDelUsuario(plan.creadorId)
            } else {
                _isLoading.value = false
            }
        }
    }
}
