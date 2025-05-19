package com.example.controlgastos.ui.gasto

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.controlgastos.data.model.Gasto
import com.example.controlgastos.ui.gasto.components.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GastosScreen(viewModel: GastosViewModel = viewModel()) {
    val gastos by viewModel.gastos.observeAsState(initial = emptyList())
    val error by viewModel.error.observeAsState()

    var showSheet by remember { mutableStateOf(false) }
    var gastoSeleccionado by remember { mutableStateOf<Gasto?>(null) }
    val sheetState = rememberModalBottomSheetState()

    LaunchedEffect(Unit) {
        viewModel.cargarGastosUsuario()
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
        )
    }

    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = sheetState,
            modifier = Modifier.fillMaxHeight(0.85f)
        ) {
            AddGastoSheet(
                onGastoGuardado = {
                    showSheet = false
                    viewModel.cargarGastosUsuario()
                }
            )
        }
    }

    if (gastoSeleccionado != null) {
        ModalBottomSheet(
            onDismissRequest = { gastoSeleccionado = null },
            sheetState = sheetState,
            modifier = Modifier.fillMaxHeight(0.85f)
        ) {
            EditGastoSheet(
                gasto = gastoSeleccionado!!,
                onDismiss = { gastoSeleccionado = null },
                onGastoActualizado = {
                    gastoSeleccionado = null
                    viewModel.cargarGastosUsuario()
                },
                onGastoEliminado = {
                    gastoSeleccionado = null
                    viewModel.cargarGastosUsuario()
                }
            )
        }
    }
}
