package com.example.controlgastos.ui.home

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import com.example.controlgastos.R
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.controlgastos.ui.planfinanciero.PlanesViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    planId: String, //Se agrega id del plan
    viewModel: HomeViewModel = viewModel(),
    onNavigateToIngresos: () -> Unit = {},
    onNavigateToGastos: () -> Unit = {},
    onNavigateToUsuarios: () -> Unit = {},
    onNavigateToPerfil: () -> Unit = {},
    onNavigateToCategorias: () -> Unit = {},
    onNavigateToPlanesUsuario: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    // bloquear el botón de retroceso
    BackHandler {
        // No hace nada si el usuario pulsa hacia atras
    }


    val usuario by viewModel.usuario.observeAsState()
    val planSeleccionado by viewModel.planSeleccionado.observeAsState() //para el plan seleccionado
    val context = LocalContext.current

    //Cargar PlanesViewModel para acceder al estado interno hayInvitacioensPendientes
    val planesViewModel: PlanesViewModel = viewModel()
    val hayInvitacionesPendientes by planesViewModel.hayInvitacionesPendientes.collectAsState()

    // Cargar los datos del usuario al entrar y el plan actual
    LaunchedEffect(Unit) {
        viewModel.cargarUsuario()
        viewModel.cargarPlanSeleccionado(planId)
        planesViewModel.cargarInvitacionesPendientes()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    //Logo que se queda a la izquierda
                    Image(
                        painter = painterResource(id = R.drawable.logo_horizontal),
                        contentDescription = "PlanSave Logo",
                        modifier = Modifier.height(32.dp)
                    )
                },
                actions = {
                    // Imagen de perfil circular a la derecha
                    Box(
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .size(40.dp)
                            .clickable { onNavigateToPerfil() }
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_userprofile_circle_24),
                            contentDescription = "Perfil",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                        )

                        if (hayInvitacionesPendientes) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .align(Alignment.TopEnd)
                                    .offset(x = 2.dp, y = (-2).dp)
                                    .background(Color.Red, shape = CircleShape)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF283593)
                )
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Top
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Bienvenido, ${usuario?.nombre ?: "Usuario"}",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                //Confirmar que recebimos el plan
                Text(
                    text = "Plan actual: ${planSeleccionado?.nombre ?: "No seleccionado"}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                if (!planSeleccionado?.descripcion.isNullOrBlank()) {
                    Text(
                        text = planSeleccionado!!.descripcion,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                // Composables después del mensaje de bienvenida
                DashboardGrid(
                    onIngresosClick = { onNavigateToIngresos() },
                    onGastosClick = { onNavigateToGastos() },
                    onUsuariosClick = { onNavigateToUsuarios() },
                    onCategoriasClick = { onNavigateToCategorias() },
                    onPlanesUsuarioClick = { onNavigateToPlanesUsuario() },
                    onLogoutClick = {
                        viewModel.cerrarSesion()
                        Toast.makeText(context, "Sesión cerrada", Toast.LENGTH_SHORT).show()
                        onLogout()
                    }
                )
            }
        }
    )
}

@Composable
fun DashboardGrid(
    onIngresosClick: () -> Unit,
    onGastosClick: () -> Unit,
    onUsuariosClick: () -> Unit,
    onCategoriasClick: () -> Unit,
    onPlanesUsuarioClick: () -> Unit,//Probar pantalla invitaciones
    onLogoutClick: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            DashboardItem(
                title = "Ingresos",
                iconRes = R.drawable.ic_ingresos_24,
                onClick = onIngresosClick,
                modifier = Modifier.weight(1f)
            )
            DashboardItem(
                title = "Gastos",
                iconRes = R.drawable.ic_gastos_24,
                onClick = onGastosClick,
                modifier = Modifier.weight(1f)
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            DashboardItem(
                title = "Usuarios",
                iconRes = R.drawable.ic_user_24,
                onClick = onUsuariosClick,
                modifier = Modifier.weight(1f)
            )
            DashboardItem(
                title = "Categorías", // ✅ nuevo botón
                iconRes = R.drawable.ic_categories_24, // asegúrate de tener este ícono en drawable
                onClick = onCategoriasClick,
                modifier = Modifier.weight(1f)
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            //
            DashboardItem(
                title = "Planes",
                iconRes = R.drawable.ic_planesuser_24,
                onClick = onPlanesUsuarioClick,
                modifier = Modifier.weight(1f)
            )
            //
            DashboardItem(
                title = "Salir",
                iconRes = R.drawable.ic_logout_24,
                onClick = onLogoutClick,
                modifier = Modifier.weight(1f)
            )
        }

    }
}

@Composable
fun DashboardItem(
    title: String,
    iconRes: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(4.dp)
            .clip(MaterialTheme.shapes.medium)
            .clickable { onClick() }
            .padding(24.dp),
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = title,
            modifier = Modifier
                .size(48.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = title, style = MaterialTheme.typography.bodyMedium)
    }
}
