package com.example.controlgastos.ui.invitaciones

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.controlgastos.ui.planfinanciero.PlanesViewModel
import com.example.controlgastos.ui.theme.GreenButton
import com.example.controlgastos.ui.theme.RedButton

@Composable
fun InvitacionesScreen(viewModel: PlanesViewModel = viewModel()) {
    val invitaciones by viewModel.invitaciones.collectAsState()
    val nombresCreadores by viewModel.nombresCreadores.collectAsState()
    val planesInvitaciones by viewModel.planesInvitaciones.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.cargarInvitacionesPendientes()
    }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text("Invitaciones pendientes", style = MaterialTheme.typography.titleLarge)

            if (invitaciones.isEmpty()) {
                Text("No tienes invitaciones por aceptar.")
            } else {
                invitaciones.forEach { acceso ->
                    val plan = planesInvitaciones.find { it.id == acceso.planId }
                    val nombrePlan = plan?.nombre ?: "Plan desconocido"
                    val nombreCreador = nombresCreadores[plan?.creadorId] ?: "Desconocido"
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        elevation = CardDefaults.cardElevation()
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text("Invitación a: $nombrePlan", style = MaterialTheme.typography.bodyLarge)
                            Text("Invitado por: $nombreCreador", style = MaterialTheme.typography.bodySmall)

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        viewModel.aceptarInvitacion(acceso.planId)
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
                                        viewModel.rechazarInvitacion(acceso.planId)
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