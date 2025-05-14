package com.example.controlgastos.ui.home

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import com.example.controlgastos.R
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.controlgastos.ui.login.LoginViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: LoginViewModel = viewModel(),
               onNavigateToIngresos: () -> Unit = {},
               onNavigateToGastos: () -> Unit = {},
               onLogout: () -> Unit = {}
) {
    val userEmail = viewModel.email.value ?: "Usuario"
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Image(
                        painter = painterResource(id = R.drawable.logo_horizontal),
                        contentDescription = "PlanSave Logo",
                        modifier = Modifier.height(32.dp)
                    )
                },
                actions = {
                    // Imagen de perfil circular a la derecha
                    Image(
                        painter = painterResource(id = R.drawable.ic_userprofile_circle_24), // Usa tu icono de usuario o avatar
                        contentDescription = "Perfil",
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .size(40.dp)
                            .clip(CircleShape)
                    )
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
                    text = "Bienvenido, $userEmail",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Aquí puedes añadir más botones, tarjetas, gráficos, etc.
                DashboardGrid(
                    onIngresosClick = { onNavigateToIngresos() },
                    onGastosClick = { onNavigateToGastos() },
                    onUsuariosClick = { /* Navega a Usuarios */ },
                    onLogoutClick = {
                        viewModel.logout()
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

private fun LoginViewModel.logout() {
    TODO("Not yet implemented")
}
