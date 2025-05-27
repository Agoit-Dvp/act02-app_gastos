package com.example.controlgastos.ui.planfinanciero.components

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
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
    var presupuesto by remember { mutableStateOf(plan.presupuestoMensual.toString()) }
    var isSaving by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var presupuestoError by remember { mutableStateOf<String?>(null) }

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

        TextField(
            value = presupuesto,
            onValueChange = {
                presupuesto = it
                presupuestoError = null // Limpiar error al modificar
            },
            label = { Text("Presupuesto mensual") },
            isError = presupuestoError != null,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            supportingText = {
                if (presupuestoError != null) {
                    Text(
                        text = presupuestoError ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        )

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
                    val presupuestoDouble = presupuesto.toDoubleOrNull()
                    if (presupuestoDouble == null || presupuestoDouble < 0) {
                        presupuestoError = "Presupuesto inválido"
                        return@Button
                    }

                    isSaving = true
                    error = null
                    presupuestoError = null

                    val actualizado = plan.copy(
                        nombre = nombre.trim(),
                        descripcion = descripcion.trim(),
                        presupuestoMensual = presupuestoDouble
                    )
                    Log.d(
                        "PlanEdit",
                        "Actualizando plan: ${actualizado.id}, nombre: ${actualizado.nombre}"
                    )
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
