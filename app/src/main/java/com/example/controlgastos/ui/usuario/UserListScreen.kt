package com.example.controlgastos.ui.usuario


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.controlgastos.data.model.Usuario
import androidx.compose.runtime.livedata.observeAsState
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaUsuariosScreen(viewModel: UsuariosViewModel = viewModel()) {
    val usuarios by viewModel.usuarios.observeAsState(initial = emptyList())
    val error by viewModel.error.observeAsState()

    // Cargar usuarios al entrar a la pantalla
    LaunchedEffect(Unit) {
        viewModel.cargarUsuarios()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Usuarios") },
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

            if (usuarios.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay usuarios registrados.")
                }
            } else {
                LazyColumn {
                    items(usuarios) { usuario ->
                        UsuarioItem(usuario)
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 4.dp),
                            thickness = 1.dp,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun UsuarioItem(usuario: Usuario) {
    val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val fecha = formato.format(usuario.fechaRegistro)

    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text("Nombre: ${usuario.nombre}")
        Text("Email: ${usuario.email}")
        Text("Teléfono: ${usuario.telefono}")
        Text("Registrado: $fecha")
    }
}