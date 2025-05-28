package com.example.controlgastos.ui.usuario


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.controlgastos.data.model.Usuario
import androidx.compose.runtime.livedata.observeAsState
import com.example.controlgastos.ui.usuario.components.InvitarUsuarioDialog
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaUsuariosScreen(
    planId: String, viewModel: UsuariosViewModel = viewModel()
) {
    val usuarios by viewModel.usuarios.observeAsState(initial = emptyList())
    val mensaje by viewModel.mensaje.observeAsState() //Mostrar mensaje de éxito/error invitación
    val rolActual by viewModel.rolUsuarioActual.observeAsState()
    val usuarioActualId by viewModel.currentUserId.observeAsState()
    var showDialogInvitacion by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    // Mostrar el mensaje si existe
    LaunchedEffect(mensaje) {
        if (!mensaje.isNullOrEmpty()) {
            snackbarHostState.showSnackbar(mensaje!!)
            viewModel.limpiarMensaje()
        }
    }

    // Cargar usuarios al entrar a la pantalla
    LaunchedEffect(planId) {
        viewModel.cargarUsuariosDelPlan(planId)
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Usuarios") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialogInvitacion = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Person, contentDescription = "Invitar usuario")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {

            if (usuarios.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay usuarios registrados.")
                }
            } else {
                LazyColumn {
                    items(usuarios) { usuario ->
                        UsuarioItem(
                            usuario = usuario,
                            usuarioActualId = usuarioActualId.toString(),
                            rolActual = rolActual,
                            onEliminarUsuario = { uid ->
                                viewModel.eliminarAcceso(uid, planId)
                            }
                        )
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

    // Mostrar el diálogo de invitación
    if (showDialogInvitacion) {
        InvitarUsuarioDialog(
            planId = planId,
            onInvitar = { email ->
                viewModel.invitarUsuario(email, planId)
                showDialogInvitacion = false
            },
            onDismiss = { showDialogInvitacion = false }
        )
    }


}

@Composable
fun UsuarioItem(
    usuario: Usuario,
    usuarioActualId: String,
    rolActual: String?,
    onEliminarUsuario: (String) -> Unit
) {
    val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val fecha = formato.format(usuario.fechaRegistro)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text("Nombre: ${usuario.nombre}")
            Text("Email: ${usuario.email}")
            Text("Teléfono: ${usuario.telefono}")
            Text("Registrado: $fecha")
        }

        // Mostrar botón si el usuario actual es admin y no está viendo su propio acceso
        if (rolActual == "administrador" && usuario.uid != usuarioActualId) {
            IconButton(onClick = { onEliminarUsuario(usuario.uid) }) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar usuario")
            }
        }
    }
}