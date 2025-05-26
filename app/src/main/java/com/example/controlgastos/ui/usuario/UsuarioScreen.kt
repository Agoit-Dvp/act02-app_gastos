package com.example.controlgastos.ui.usuario

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.controlgastos.data.model.Usuario
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.shadow
import com.example.controlgastos.ui.invitaciones.InvitacionesScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsuarioScreen(viewModel: UsuarioViewModel = viewModel()) {
    val usuario by viewModel.usuario.observeAsState()
    val error by viewModel.error.observeAsState()

    var showInvitaciones by remember { mutableStateOf(false) }
    var showEditSheet by remember { mutableStateOf(false) }

    var topBarPadding by remember { mutableStateOf(0.dp) } //Calcular limite altura animación pantalla invitaciones

    // Cargar datos al entrar
    LaunchedEffect(Unit) {
        viewModel.cargarDatosUsuario()
    }
    Box(modifier = Modifier.fillMaxSize()) { //Permite usar  en su contenido modifer align
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Perfil de Usuario") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    actions = {
                        IconButton(onClick = { showInvitaciones = !showInvitaciones }) {
                            Icon(Icons.Default.MailOutline, contentDescription = "Ver invitaciones")
                        }
                    }
                )
            }
        ) { padding ->
            topBarPadding = padding.calculateTopPadding() //almacernamos el padding para animación
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
                    usuario?.let {
                        UsuarioInfo(usuario = it, onEditar = { showEditSheet = true })
                    }
                }
            }
        }

        // Modal para editar usuario
        if (showEditSheet && usuario != null) {
            ModalBottomSheet(
                onDismissRequest = { showEditSheet = false },
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ) {
                EditarUsuarioSheet(
                    usuario = usuario!!,
                    onGuardar = { nuevoNombre, nuevoTelefono ->
                        viewModel.actualizarDatosUsuario(nuevoNombre, nuevoTelefono)
                        showEditSheet = false
                    },
                    onCancelar = { showEditSheet = false }
                )
            }
        }



        //Ventana retráctil dede el lado derecho
        //Para InvitacionesScreen
        AnimatedVisibility(
            visible = showInvitaciones,
            enter = slideInHorizontally(initialOffsetX = { fullWidth -> fullWidth }),
            exit = slideOutHorizontally(targetOffsetX = { fullWidth -> fullWidth }),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = topBarPadding.value.dp, start = 60.dp)
                    .align(Alignment.TopEnd) // ahora se aplica dentro del contenido visible
                    .background(
                        MaterialTheme.colorScheme.surface,
                        RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp)
                    )
                    .shadow(8.dp)
            ) {
                InvitacionesScreen()
                IconButton(
                    onClick = { showInvitaciones = false },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Cerrar invitaciones")
                }
            }
        }
    }
}

@Composable
fun UsuarioInfo(usuario: Usuario, onEditar: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("UID: ${usuario.uid}")
        Text("Nombre: ${usuario.nombre}")
        Text("Email: ${usuario.email}")
        Text("Teléfono: ${usuario.telefono}")
        Text("Registrado: ${usuario.fechaRegistro}")
        Spacer(Modifier.height(8.dp))
        Button(onClick = onEditar) {
            Text("Editar perfil")
        }
    }
}

@Composable
fun EditarUsuarioSheet(
    usuario: Usuario,
    onGuardar: (String, String) -> Unit,
    onCancelar: () -> Unit
) {
    var nombre by rememberSaveable { mutableStateOf(usuario.nombre) }
    var telefono by rememberSaveable { mutableStateOf(usuario.telefono) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Editar perfil", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = telefono,
            onValueChange = { telefono = it },
            label = { Text("Teléfono") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = onCancelar) {
                Text("Cancelar")
            }
            Button(
                onClick = { onGuardar(nombre, telefono) },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Guardar")
            }
        }
    }
}