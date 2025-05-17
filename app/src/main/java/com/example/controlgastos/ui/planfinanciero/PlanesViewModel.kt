package com.example.controlgastos.ui.planfinanciero

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.controlgastos.data.model.AccesoPlanFinanciero
import com.example.controlgastos.data.model.PlanFinanciero
import com.example.controlgastos.data.repository.AccesoPlanFinancieroRepository
import com.example.controlgastos.data.repository.PlanFinancieroRepository
import com.example.controlgastos.data.repository.UsuarioRepository

class PlanesViewModel : ViewModel() {

    private val repoPlanes = PlanFinancieroRepository()
    private val repoAccesos = AccesoPlanFinancieroRepository()

    val planesUsuario = mutableStateListOf<PlanFinanciero>()
    val accesosUsuario = mutableStateListOf<AccesoPlanFinanciero>()
    val invitacionesPendientes = mutableStateListOf<AccesoPlanFinanciero>()
    val planesInvitados = mutableStateMapOf<String, PlanFinanciero>()
    val nombresCreadores = mutableStateMapOf<String, String>() // uid -> nombre
    val isLoading = mutableStateOf(false)


    //Obtener el nombre del plan desde el mapa (para UI)
    fun getNombrePlan(planId: String): String {
        return planesInvitados[planId]?.nombre ?: "Plan ID: $planId"
    }


    //Cargar invitaciones pendientes y planes
    fun cargarInvitacionesPendientes(usuarioId: String) {
        repoAccesos.obtenerInvitacionesPendientes(usuarioId) { accesos ->
            invitacionesPendientes.clear()
            invitacionesPendientes.addAll(accesos)

            val planIds = accesos.map { it.planId }.distinct()
            if (planIds.isEmpty()) return@obtenerInvitacionesPendientes

            repoPlanes.obtenerPlanesPorIds(planIds) { planes ->
                planesInvitados.clear()
                planes.forEach { plan ->
                    planesInvitados[plan.id] = plan
                }
            }
        }
    }


    // Cargar accesos y los planes aceptados
    fun cargarPlanesDelUsuario(usuarioId: String) {
        isLoading.value = true
        repoAccesos.obtenerAccesosDeUsuario(usuarioId) { accesos ->
            accesosUsuario.clear()
            accesosUsuario.addAll(accesos)

            val ids = accesos.map { it.planId }
            Log.d("Planes", "Accesos encontrados: ${ids.size}")
            if (ids.isEmpty()) {
                planesUsuario.clear()
                return@obtenerAccesosDeUsuario
            }

            repoPlanes.obtenerPlanesPorIds(ids) { planes ->
                Log.d("Planes", "Accesos encontrados: ${ids.size}")
                planesUsuario.clear()
                planesUsuario.addAll(planes)
                cargarNombresCreadores()
            }
        }
    }

    fun cargarNombresCreadores() {
        val uids = planesUsuario.map { it.creadorId }.distinct().filter { it.isNotBlank() }

        uids.forEach { uid ->
            if (!nombresCreadores.containsKey(uid)) {
                UsuarioRepository().obtenerNombrePorUid(uid) { nombre ->
                    if (nombre != null) {
                        nombresCreadores[uid] = nombre
                    }
                }
            }
        }
    }

    fun cargarDatos(usuarioId: String) {
        cargarPlanesDelUsuario(usuarioId)
        // talvez carga de invitaciones también:
        // cargarInvitacionesPendientes(usuarioId)
    }

    fun crearNuevoPlan(plan: PlanFinanciero) {
        repoPlanes.crearPlan(plan) { exito ->
            if (exito) cargarPlanesDelUsuario(plan.creadorId)
        }
    }

    fun eliminarAcceso(usuarioId: String, planId: String) {
        repoAccesos.eliminarAcceso(usuarioId, planId) { exito ->
            if (exito) cargarPlanesDelUsuario(usuarioId)
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
            if (exito) {
                cargarInvitacionesPendientes(usuarioId)
            }
        }
    }
}
