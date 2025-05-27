package com.example.controlgastos.ui.planfinanciero.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.controlgastos.ui.theme.GreenButton


@Composable
fun AddPlanSheet(
    onCrear: (String, String, Double) -> Unit,
    onCancelar: () -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var presupuesto by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var presupuestoError by remember { mutableStateOf<String?>(null) }


    Column(modifier = Modifier.padding(16.dp)) {
        Text("Nuevo plan financiero", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre del plan") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            label = { Text("Descripción") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = presupuesto,
            onValueChange = {
                presupuesto = it
                presupuestoError = null // limpiar el error al escribir
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

        if (!errorMessage.isNullOrEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(errorMessage ?: "", color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = { if (!isSaving) onCancelar() }) {
                Text("Cancelar")
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {
                    if (nombre.isBlank()) {
                        errorMessage = "El nombre es obligatorio"
                        return@Button
                    }
                    val presupuestoDouble = presupuesto.toDoubleOrNull()
                    if (presupuestoDouble == null || presupuestoDouble < 0) {
                        presupuestoError = "Presupuesto inválido"
                        return@Button
                    }

                    isSaving = true
                    errorMessage = null

                    onCrear(nombre, descripcion, presupuestoDouble)
                    isSaving = false
                },
                enabled = !isSaving,
                colors = ButtonDefaults.buttonColors(
                    containerColor = GreenButton,
                    contentColor = Color.White
                )
            ) {
                Text(if (isSaving) "Creando..." else "Crear")
            }
        }
    }
}