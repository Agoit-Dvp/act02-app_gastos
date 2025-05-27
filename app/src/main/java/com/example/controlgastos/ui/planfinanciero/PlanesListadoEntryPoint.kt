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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.example.controlgastos.data.preferences.PlanPreferences
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@Composable
fun PlanesListadoEntryPoint(
    navController: NavController
) {
    val viewModel: PlanesViewModel = viewModel()

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Conectar flows del viewModel a variables de Compose
    val planes by viewModel.planes.collectAsState()
    val accesos by viewModel.accesos.collectAsState()
    val nombresCreadores by viewModel.nombresCreadores.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    //Controlar usuario actual
    val currentUser = FirebaseAuth.getInstance().currentUser
    val currentUserId = currentUser?.uid.orEmpty()

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
        onCrearNuevo = { nombre, descripcion, presupuesto ->
            viewModel.crearNuevoPlan(nombre, descripcion, presupuesto)
        },
        onActualizarPlan = { plan ->
            viewModel.actualizarPlan(plan)
        },
        onSeleccionar = { plan ->
            coroutineScope.launch {
                // Guardar el nuevo planId como el último seleccionado
                PlanPreferences.guardarUltimoPlan(context, currentUserId, plan.id)

                // Navegar a Home con ese plan
                navController.navigate(Home(plan.id)) {
                    popUpTo(PlanesListado) { inclusive = true }
                    launchSingleTop = true
                }
            }
        },
        onEliminarPlan = { planId ->
            coroutineScope.launch {
                // Recuperar el plan actualmente guardado
                val planGuardado = PlanPreferences.obtenerUltimoPlan(context, currentUserId)

                // Si el plan eliminado es el que estaba guardado, se borra de DataStore
                if (planGuardado == planId) {
                    PlanPreferences.borrarUltimoPlan(context, currentUserId)
                }
                // Llamamos al ViewModel para eliminar el plan
                viewModel.eliminarPlan(planId)
            }
        }
    )
}