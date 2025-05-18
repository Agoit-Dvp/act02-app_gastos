package com.example.controlgastos.ui.planfinanciero

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.controlgastos.data.model.PlanFinanciero
import androidx.compose.runtime.getValue

//import com.example.controlgastos.viewmodel.PlanesViewModel

@Composable
fun PlanesListadoEntryPoint(usuarioId: String) {
    val viewModel: PlanesViewModel = viewModel()

    // Recolectar LiveData como estado Compose
    val planes by viewModel.planes.observeAsState(emptyList())
    val accesos by viewModel.accesos.observeAsState(emptyList())
    val nombresCreadores by viewModel.nombresCreadores.observeAsState(emptyMap())
    val isLoading by viewModel.isLoading.observeAsState(false)

    // Cargar datos al entrar
    LaunchedEffect(usuarioId) {
        viewModel.cargarDatos(usuarioId)
    }

    PlanesListadoScreen(
        planes = planes,
        accesos = accesos,
        nombresCreadores = nombresCreadores,
        isLoading = isLoading,
        onCrearNuevo = { nombre, descripcion ->
            val plan = PlanFinanciero(
                nombre = nombre,
                descripcion = descripcion,
                creadorId = usuarioId
            )
            viewModel.crearNuevoPlan(plan)
        },
        onActualizarPlan = { plan ->
            viewModel.actualizarPlan(plan)
        },
        onSeleccionar = { plan ->
            // TODO: navegar a detalles o usuarios invitados
        }
    )
}