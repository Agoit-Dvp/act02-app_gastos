package com.example.controlgastos.ui.ingreso

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.controlgastos.data.model.Ingreso
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IngresosScreen(viewModel: IngresosViewModel = viewModel()) {
    val ingresos by viewModel.ingresos.observeAsState(initial = emptyList())
    val error by viewModel.error.observeAsState()

    // Cargar ingresos al entrar a la pantalla
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

            if (ingresos.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay ingresos registrados.")
                }
            } else {
                LazyColumn {
                    items(ingresos) { ingreso ->
                        IngresoItem(ingreso)
                        Divider()
                    }
                }
            }
        }
    }
}

@Composable
fun IngresoItem(ingreso: Ingreso) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text("Categoría: ${ingreso.categoria}")
        Text("Cantidad: $${ingreso.cantidad}")
        Text("Descripción: ${ingreso.descripcion}")
    }
}