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
import android.util.Log
import androidx.compose.material3.Text

@Composable
fun PlanesListadoEntryPoint(
    navController: NavController
) {
    val viewModel: PlanesViewModel = viewModel()

    // Conectar flows del viewModel a variables de Compose
    val planes by viewModel.planes.collectAsState()
    val accesos by viewModel.accesos.collectAsState()
    val nombresCreadores by viewModel.nombresCreadores.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Cargar datos al entrar
    LaunchedEffect(true) {
        viewModel.cargarDatos()
    }

    PlanesListadoScreen(
        viewModel = viewModel,
        planes = planes,
        accesos = accesos,
        nombresCreadores = nombresCreadores,
        isLoading = isLoading,
        onCrearNuevo = { nombre, descripcion ->
            viewModel.crearNuevoPlan(nombre, descripcion)
        },
        onActualizarPlan = { plan ->
            viewModel.actualizarPlan(plan)
        },
        onSeleccionar = { plan ->
            navController.navigate(Home(plan.id)) {
                popUpTo(PlanesListado) { inclusive = true }
            }
        },
        onEliminarPlan = { planId ->
            viewModel.eliminarPlan(planId)
        }
    )
}