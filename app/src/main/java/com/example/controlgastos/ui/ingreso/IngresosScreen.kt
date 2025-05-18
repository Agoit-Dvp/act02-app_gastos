package com.example.controlgastos.ui.ingreso

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.padding

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.controlgastos.data.model.Ingreso
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.controlgastos.ui.ingreso.components.AddIngresoSheet
import com.example.controlgastos.ui.ingreso.components.EditIngresoSheet
import com.example.controlgastos.ui.ingreso.components.IngresosContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IngresosScreen(viewModel: IngresosViewModel = viewModel()) {
    val ingresos by viewModel.ingresos.observeAsState(initial = emptyList())
    val error by viewModel.error.observeAsState()

    var showSheet by remember { mutableStateOf(false) }

    var ingresoSeleccionado by remember { mutableStateOf<Ingreso?>(null) }
    val sheetState = rememberModalBottomSheetState()

    LaunchedEffect(Unit) {
        viewModel.cargarIngresosUsuario()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ingresos") },
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
                Icon(Icons.Default.Add, contentDescription = "Agregar ingreso")
            }
        }
    ) { padding ->
        IngresosContent(
            ingresos = ingresos,
            error = error,
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            onIngresoClick = { ingresoSeleccionado = it }
        )
    }

    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = sheetState
        ) {
            AddIngresoSheet(
                onIngresoGuardado = {
                    showSheet = false
                    viewModel.cargarIngresosUsuario()
                }
            )
        }
    }
    //Editar y modificar en IngresoScreen
    if (ingresoSeleccionado != null) {
        ModalBottomSheet(
            onDismissRequest = { ingresoSeleccionado = null },
            sheetState = sheetState
        ) {
            EditIngresoSheet(
                ingreso = ingresoSeleccionado!!,
                onDismiss = { ingresoSeleccionado = null },
                onIngresoActualizado = {
                    ingresoSeleccionado = null
                    viewModel.cargarIngresosUsuario()
                },
                onIngresoEliminado = {
                    ingresoSeleccionado = null
                    viewModel.cargarIngresosUsuario()
                }
            )
        }
    }
}

