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
import com.example.controlgastos.ui.gasto.components.GastosContent
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GastosScreen(viewModel: GastosViewModel = viewModel()) {
    val gastos by viewModel.gastos.observeAsState(initial = emptyList())
    val error by viewModel.error.observeAsState()

    val sheetState = rememberModalBottomSheetState()
    var showSheet by remember { mutableStateOf(false) }

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
                onGastoGuardado = {
                    showSheet = false
                    viewModel.cargarGastosUsuario()
                }
            )
        }
    }
}
