package com.example.controlgastos.ui.gasto

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GastosScreen(viewModel: GastosViewModel = viewModel()) {
    val gastos by viewModel.gastos.observeAsState(initial = emptyList())
    val error by viewModel.error.observeAsState()

    // Cargar gastos al entrar a la pantalla
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
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            if (error != null) {
                Text(
                    text = error ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (gastos.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay gastos registrados.")
                }
            } else {
                LazyColumn {
                    items(gastos) { gasto ->
                        GastoItem(gasto)
                        Divider()
                    }
                }
            }
        }
    }
}

@Composable
fun GastoItem(gasto: Gasto) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text("Categoría: ${gasto.categoria}")
        Text("Cantidad: $${gasto.cantidad}")
        Text("Descripción: ${gasto.descripcion}")
    }
}