package com.example.controlgastos.ui.planfinanciero

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.controlgastos.data.model.AccesoPlanFinanciero
import com.example.controlgastos.data.model.PlanFinanciero
import com.example.controlgastos.data.repository.AccesoPlanFinancieroRepository
import com.example.controlgastos.data.repository.PlanFinancieroRepository

class PlanesViewModel : ViewModel() {

    private val repoPlanes = PlanFinancieroRepository()
    private val repoAccesos = AccesoPlanFinancieroRepository()

    val planesUsuario = mutableStateListOf<PlanFinanciero>()
    val accesosUsuario = mutableStateListOf<AccesoPlanFinanciero>()
    val invitacionesPendientes = mutableStateListOf<AccesoPlanFinanciero>()

    //Cargar invitaciones pendientes
    fun cargarInvitacionesPendientes(usuarioId: String) {
        repoAccesos.obtenerInvitacionesPendientes(usuarioId) {
            invitacionesPendientes.clear()
            invitacionesPendientes.addAll(it)
        }
    }

    // Cargar los accesos y luego los planes
    fun cargarPlanesDelUsuario(usuarioId: String) {
        repoAccesos.obtenerAccesosDeUsuario(usuarioId) { accesos ->
            accesosUsuario.clear()
            accesosUsuario.addAll(accesos)

            val ids = accesos.map { it.planId }
            if (ids.isEmpty()) {
                planesUsuario.clear()
                return@obtenerAccesosDeUsuario
            }

            repoPlanes.obtenerPlanesPorIds(ids) { planes ->
                planesUsuario.clear()
                planesUsuario.addAll(planes)
            }
        }
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
