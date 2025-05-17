package com.example.controlgastos.ui.planfinanciero

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.controlgastos.data.model.PlanFinanciero

//import com.example.controlgastos.viewmodel.PlanesViewModel

@Composable
fun PlanesListadoEntryPoint(usuarioId: String) {
    val viewModel: PlanesViewModel = viewModel()

    // Cargar datos solo una vez al entrar con este usuarioId
    LaunchedEffect(usuarioId) {
        viewModel.cargarDatos(usuarioId)
    }

    PlanesListadoScreen(
        planes = viewModel.planesUsuario,
        accesos = viewModel.accesosUsuario,
        nombresCreadores = viewModel.nombresCreadores,
        isLoading = viewModel.isLoading.value, // ✅
        onCrearNuevo = { nombre, descripcion ->
            val plan = PlanFinanciero(
                nombre = nombre,
                descripcion = descripcion,
                creadorId = usuarioId
            )
            viewModel.crearNuevoPlan(plan)

            // Fuerza recarga tras crear
            viewModel.cargarDatos(usuarioId)
        },
        onSeleccionar = { plan ->
            // TODO: navegar a pantalla de detalles, usuarios invitados, etc.
        }
    )
}