package com.example.controlgastos.ui.planfinanciero

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.controlgastos.data.model.PlanFinanciero
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import com.example.controlgastos.data.model.AccesoPlanFinanciero
import com.example.controlgastos.ui.planfinanciero.components.AddPlanSheet
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanesListadoScreen(
    planes: List<PlanFinanciero>,
    accesos: List<AccesoPlanFinanciero> = emptyList(),
    nombresCreadores: Map<String, String> = emptyMap(),
    isLoading: Boolean = false,
    onCrearNuevo: (String, String) -> Unit = { _, _ -> },  // ✅ nuevo
    onSeleccionar: (PlanFinanciero) -> Unit = {}
) {
    var mostrarSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Planes financieros") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { mostrarSheet = true }) {
                Icon(Icons.Default.Add, contentDescription = "Nuevo plan")
            }
        }
    ) { padding ->
        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            planes.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No tienes planes por ahora.")
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .padding(padding)
                        .padding(16.dp)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(planes) { plan ->
                        val acceso = accesos.find { it.planId == plan.id }
                        val creadorNombre = nombresCreadores[plan.creadorId]
                        PlanItem(
                            plan = plan,
                            acceso = acceso,
                            creadorNombre = creadorNombre,
                            onClick = { onSeleccionar(plan) }
                        )
                    }
                }
            }
        }
    }
    //Mostrar pantalla de añadir planes
    if (mostrarSheet) {
        ModalBottomSheet(
            onDismissRequest = { mostrarSheet = false },
            sheetState = sheetState
        ) {
            AddPlanSheet(
                onCrear = { nombre, descripcion ->
                    mostrarSheet = false
                    onCrearNuevo(nombre, descripcion)
                },
                onCancelar = { mostrarSheet = false }
            )
        }
    }
}

@Composable
fun PlanItem(
    plan: PlanFinanciero,
    acceso: AccesoPlanFinanciero?,
    creadorNombre: String?,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        tonalElevation = 2.dp,
        shadowElevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(plan.nombre, style = MaterialTheme.typography.titleMedium)

            if (plan.descripcion.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(plan.descripcion, style = MaterialTheme.typography.bodySmall)
            }

            acceso?.let {
                Spacer(modifier = Modifier.height(6.dp))
                val rolText = if (it.esPropietario) "(Propietario)" else "(Invitado)"
                Text(rolText, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            }

            creadorNombre?.let {
                Spacer(modifier = Modifier.height(4.dp))
                Text("Creado por: $it", style = MaterialTheme.typography.labelSmall, color = Color.DarkGray)
            }
            plan.fechaCreacion?.let { fecha ->
                Spacer(modifier = Modifier.height(4.dp))
                val formato = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                val fechaTexto = formato.format(fecha)
                Text("Creado el: $fechaTexto", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            }
        }
    }

}