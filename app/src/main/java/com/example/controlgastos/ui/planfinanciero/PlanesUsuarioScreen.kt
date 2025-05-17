package com.example.controlgastos.ui.planfinanciero

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.controlgastos.data.model.AccesoPlanFinanciero

@Composable
fun PlanesUsuarioScreen(
    viewModel: PlanesViewModel,
    usuarioId: String
) {
    val planes = viewModel.planesUsuario
    val invitaciones = remember { mutableStateListOf<AccesoPlanFinanciero>() }

    // Cargar planes y invitaciones al iniciar
    LaunchedEffect(usuarioId) {
        viewModel.cargarPlanesDelUsuario(usuarioId)
        viewModel.cargarInvitacionesPendientes(usuarioId)
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        Text("Tus planes", style = MaterialTheme.typography.titleLarge)

        if (planes.isEmpty()) {
            Text("No tienes planes aceptados aún.")
        } else {
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
                        Text("Invitación a plan: ${acceso.planId}", style = MaterialTheme.typography.bodyLarge)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    viewModel.aceptarInvitacion(acceso.planId, usuarioId)
                                    //invitaciones.remove(acceso)
                                }
                            ) {
                                Text("Aceptar")
                            }
                            OutlinedButton(
                                onClick = {
                                    viewModel.rechazarInvitacion(acceso.planId, usuarioId)
                                    //invitaciones.remove(acceso)
                                }
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