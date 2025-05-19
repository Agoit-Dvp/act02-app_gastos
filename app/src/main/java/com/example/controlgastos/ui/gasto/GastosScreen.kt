package com.example.controlgastos.ui.gasto

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.controlgastos.data.model.Gasto
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import com.example.controlgastos.ui.gasto.components.AddGastoSheet
import com.example.controlgastos.ui.gasto.components.EditGastoSheet
import com.example.controlgastos.ui.gasto.components.GastosContent
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GastosScreen(viewModel: GastosViewModel = viewModel(), planId: String) {
    val gastos by viewModel.gastos.observeAsState(initial = emptyList())
    val error by viewModel.error.observeAsState()

    val sheetState = rememberModalBottomSheetState()
    var showSheet by remember { mutableStateOf(false) }
    var gastoSeleccionado by remember { mutableStateOf<Gasto?>(null) }

    LaunchedEffect(Unit) {
        viewModel.cargarGastosDePlan(planId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gastos") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showSheet = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar gasto")
            }
        }

    ) { padding ->
        GastosContent(
            gastos = gastos,
            error = error,
            onGastoClick = { gastoSeleccionado = it },
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        )
    }
    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = sheetState
        ) {
            AddGastoSheet(
                planId = planId,
                onGastoGuardado = {
                    showSheet = false
                    viewModel.cargarGastosDePlan(planId)
                }
            )
        }
    }

    //Editar desd la misma pantalla con Modal:
    if (gastoSeleccionado != null) {
        ModalBottomSheet(
            onDismissRequest = { gastoSeleccionado = null },
            sheetState = sheetState
        ) {
            EditGastoSheet(
                gasto = gastoSeleccionado!!,
                onDismiss = { gastoSeleccionado = null },
                onGastoActualizado = {
                    gastoSeleccionado = null
                    viewModel.cargarGastosDePlan(planId)
                },
                onGastoEliminado = {
                    gastoSeleccionado = null
                    viewModel.cargarGastosDePlan(planId)
                }
            )
        }
    }
}
