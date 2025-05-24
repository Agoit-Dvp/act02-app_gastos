package com.example.controlgastos.ui.planfinanciero

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.controlgastos.data.model.PlanFinanciero
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import com.example.controlgastos.navigation.Home
import com.example.controlgastos.navigation.PlanesListado

@Composable
fun PlanesListadoEntryPoint(
    usuarioId: String,
    navController: NavController
) {
    val viewModel: PlanesViewModel = viewModel()

    // Conectar flows del viewModel a variables de Compose
    val planes by viewModel.planes.collectAsState()
    val accesos by viewModel.accesos.collectAsState()
    val nombresCreadores by viewModel.nombresCreadores.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

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
            navController.navigate(Home(plan.id)) {
                popUpTo(PlanesListado) { inclusive = true }
            }
        }
    )
}