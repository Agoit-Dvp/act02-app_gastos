package com.example.controlgastos.ui.usuario

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.controlgastos.data.model.Usuario
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsuarioScreen(viewModel: UsuarioViewModel = viewModel()) {
    val usuario by viewModel.usuario.observeAsState()
    val error by viewModel.error.observeAsState()

    // Cargar datos al entrar
    LaunchedEffect(Unit) {
        viewModel.cargarDatosUsuario()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Perfil de Usuario") },
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
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (error != null) {
                Text(text = error ?: "Error", color = MaterialTheme.colorScheme.error)
            } else if (usuario == null) {
                Text("No se encontró el usuario.")
            } else {
                UsuarioInfo(usuario!!)
            }
        }
    }
}

@Composable
fun UsuarioInfo(usuario: Usuario) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("UID: ${usuario.uid}")//Eliminar después de pruebas todo
        Text("Nombre: ${usuario.nombre}")
        Text("Email: ${usuario.email}")
        Text("Registrado: ${usuario.fechaRegistro}")
    }
}