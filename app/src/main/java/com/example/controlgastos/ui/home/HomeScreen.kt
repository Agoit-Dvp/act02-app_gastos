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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.controlgastos.data.preferences.PlanPreferences
import com.example.controlgastos.ui.planfinanciero.PlanesViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


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

    //Para el saldo (saldoCard)
    val saldo by viewModel.saldo.observeAsState(0.0)
    //Para acceder a planId guardado en PlanPreferences
    val context = LocalContext.current

    //Para presupuesto mensual (PresupuestoBar)
    val totalGastado by viewModel.totalGastado.observeAsState(0.0)
    val presupuestoMensual = planSeleccionado?.presupuestoMensual ?: 0.0

    //Cargar PlanesViewModel para acceder al estado interno hayInvitacioensPendientes
    val planesViewModel: PlanesViewModel = viewModel()
    val hayInvitacionesPendientes by planesViewModel.hayInvitacionesPendientes.collectAsState()



    // Cargar los datos del usuario al entrar y el plan actual
    LaunchedEffect(Unit) {
        viewModel.cargarUsuario()
        viewModel.cargarPlanSeleccionado(planId)
        planesViewModel.cargarInvitacionesPendientes()
    }

    val lifecycleOwner = LocalLifecycleOwner.current

    // Escuchar ON_RESUME para actualizar saldo al volver
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.actualizarSaldo(planId)

            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    //Logo que se queda a la izquierda
                    Image(
                        painter = painterResource(id = R.drawable.ic_logo_plansave_color),
                        contentDescription = "PlanSave Logo",
                        modifier = Modifier.height(52.dp)
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = "Bienvenido, ${usuario?.nombre ?: "Usuario"}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                item {
                    Text(
                        text = "Plan actual: ${planSeleccionado?.nombre ?: "No seleccionado"}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                if (!planSeleccionado?.descripcion.isNullOrBlank()) {
                    item {
                        Text(
                            text = planSeleccionado!!.descripcion,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }

                //Tarjeta de saldo
                item {
                    SaldoCard(saldo = saldo)
                }

                //Barra de progreso del presupuesto
                item {
                    PresupuestoBar(
                        totalGastado = totalGastado,
                        presupuestoMensual = presupuestoMensual
                    )
                }

                item {
                    DashboardGrid(
                        onIngresosClick = { onNavigateToIngresos() },
                        onGastosClick = { onNavigateToGastos() },
                        onUsuariosClick = { onNavigateToUsuarios() },
                        onCategoriasClick = { onNavigateToCategorias() },
                        onPlanesUsuarioClick = { onNavigateToPlanesUsuario() },
                        onLogoutClick = {
                            viewModel.cerrarSesion()
                            Toast.makeText(context, "Sesión cerrada", Toast.LENGTH_SHORT).show()

                            // BORRAR planId del usuario actual de DataStore
                            val userId = FirebaseAuth.getInstance().currentUser?.uid
                            if (userId != null) {
                                // Usar una coroutine porque DataStore es suspend
                                CoroutineScope(Dispatchers.IO).launch {
                                    PlanPreferences.borrarUltimoPlan(context, userId)
                                }
                            }
                            onLogout()
                        }
                    )
                }
            }
        }
    )
}

//Menus de acceso
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

//Composable individual para cada menu
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
        horizontalAlignment = Alignment.CenterHorizontally,
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

//Composable para mostrar saldo
@Composable
fun SaldoCard(
    saldo: Double,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF4CAF50))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Saldo disponible",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White
            )
            Text(
                text = "$${"%.2f".format(saldo)}",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}

//Composable para mostrar el progreso del presupuesto
@Composable
fun PresupuestoBar(
    totalGastado: Double,
    presupuestoMensual: Double,
    modifier: Modifier = Modifier
) {
    val porcentajeUsado = if (presupuestoMensual > 0) {
        (totalGastado / presupuestoMensual).coerceAtMost(1.0)
    } else 0.0

    val (barraColor, textoColor, mensaje) = when {
        porcentajeUsado < 0.5 -> Triple(Color(0xFF4CAF50), Color.Gray, "Presupuesto saludable")
        porcentajeUsado < 0.8 -> Triple(Color(0xFFFFC107), Color.DarkGray, "Precaución")
        else -> Triple(Color(0xFFF44336), Color.Red, "¡Cerca del límite!")
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Presupuesto mensual",
                style = MaterialTheme.typography.bodyMedium
            )

            LinearProgressIndicator(
                progress = { porcentajeUsado.toFloat() }, // ✅ NUEVA API
                color = barraColor,
                trackColor = Color.LightGray,
                strokeCap = StrokeCap.Round,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp))
            )

            Text(
                text = "${(porcentajeUsado * 100).toInt()}% usado - $mensaje",
                style = MaterialTheme.typography.bodySmall,
                color = textoColor
            )
        }
    }
}
