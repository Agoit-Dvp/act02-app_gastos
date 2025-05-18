package com.example.controlgastos.ui.planfinanciero

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.controlgastos.data.model.AccesoPlanFinanciero
import com.example.controlgastos.ui.theme.GreenButton
import com.example.controlgastos.ui.theme.RedButton

@Composable
fun PlanesUsuarioScreen(
    viewModel: PlanesViewModel,
    usuarioId: String
) {
    val planes by viewModel.planes.observeAsState(emptyList())
    val invitaciones by viewModel.invitaciones.observeAsState(emptyList())
    val nombresCreadores by viewModel.nombresCreadores.observeAsState(emptyMap())
    val isLoading by viewModel.isLoading.observeAsState(false)

    LaunchedEffect(usuarioId) {
        viewModel.cargarPlanesDelUsuario(usuarioId)
        viewModel.cargarInvitacionesPendientes(usuarioId)
    }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text("Tus planes", style = MaterialTheme.typography.titleLarge)

            when {
                isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                planes.isEmpty() -> {
                    Text("No tienes planes aceptados aún.")
                }

                else -> {
                    planes.forEach { plan ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            elevation = CardDefaults.cardElevation()
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Text(plan.nombre, style = MaterialTheme.typography.titleMedium)
                                Text(plan.descripcion, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Invitaciones pendientes", style = MaterialTheme.typography.titleLarge)

            if (invitaciones.isEmpty()) {
                Text("No tienes invitaciones por aceptar.")
            } else {
                invitaciones.forEach { acceso ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        elevation = CardDefaults.cardElevation()
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text(
                                "Invitación a: ${nombresCreadores[acceso.usuarioId] ?: "Plan ID: ${acceso.planId}"}",
                                style = MaterialTheme.typography.bodyLarge
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        viewModel.aceptarInvitacion(acceso.planId, usuarioId)
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = GreenButton,
                                        contentColor = Color.White
                                    )
                                ) {
                                    Text("Aceptar")
                                }

                                Button(
                                    onClick = {
                                        viewModel.rechazarInvitacion(acceso.planId, usuarioId)
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = RedButton,
                                        contentColor = Color.White
                                    )
                                ) {
                                    Text("Rechazar")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}