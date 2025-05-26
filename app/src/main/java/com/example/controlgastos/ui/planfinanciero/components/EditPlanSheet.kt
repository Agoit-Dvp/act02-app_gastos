package com.example.controlgastos.ui.planfinanciero.components

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.controlgastos.data.model.PlanFinanciero
import com.example.controlgastos.data.repository.PlanFinancieroRepository

@Composable
fun EditPlanSheet(
    plan: PlanFinanciero,
    onDismiss: () -> Unit,
    onActualizar: (PlanFinanciero) -> Unit,
    onEliminar: ((String) -> Unit)? = null,
    planRepository: PlanFinancieroRepository = PlanFinancieroRepository()
) {
    var nombre by remember { mutableStateOf(plan.nombre) }
    var descripcion by remember { mutableStateOf(plan.descripcion) }
    var isSaving by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Editar Plan", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(12.dp))

        TextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            label = { Text("Descripción") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancelar")
            }
            Spacer(modifier = Modifier.width(8.dp))
            if (onEliminar != null) {
                OutlinedButton(
                    onClick = {
                        onEliminar(plan.id)
                        onDismiss()
                    },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Eliminar")
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    if (nombre.isBlank()) {
                        error = "El nombre no puede estar vacío"
                        return@Button
                    }
                    isSaving = true
                    val actualizado = plan.copy(nombre = nombre.trim(), descripcion = descripcion.trim())
                    Log.d("PlanEdit", "Actualizando plan: ${actualizado.id}, nombre: ${actualizado.nombre}")
                    planRepository.actualizarPlan(actualizado) { success ->
                        isSaving = false
                        if (success) onActualizar(actualizado) else error = "Error al guardar"
                    }
                },
                enabled = !isSaving,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Guardar")
            }
        }

        error?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }
    }
}
