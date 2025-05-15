package com.example.controlgastos.ui.ingreso.components

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.controlgastos.data.model.Ingreso
import com.example.controlgastos.data.repository.IngresoRepository
import com.example.controlgastos.ui.theme.AppColors

@Composable
fun EditIngresoSheet(
    ingreso: Ingreso,
    onDismiss: () -> Unit,
    onIngresoActualizado: () -> Unit,
    onIngresoEliminado: () -> Unit,
    ingresoRepository: IngresoRepository = IngresoRepository()
) {
    var nombre by remember { mutableStateOf(ingreso.nombre) }
    var valor by remember { mutableStateOf(ingreso.valor.toString()) }
    var descripcion by remember { mutableStateOf(ingreso.descripcion) }
    var isSaving by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Editar Ingreso", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(12.dp))

        TextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") })
        Spacer(Modifier.height(8.dp))

        TextField(
            value = valor,
            onValueChange = { valor = it },
            label = { Text("Valor") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(Modifier.height(8.dp))

        TextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            label = { Text("Descripción") }
        )
        Spacer(Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedButton(onClick = {
                ingresoRepository.deleteIngreso(ingreso.id) { success, errorMsg ->
                    if (success) onIngresoEliminado() else error = errorMsg
                }
            }) {
                Text("Eliminar")
            }

            Button(
                onClick = {
                    if (nombre.isBlank() || valor.toDoubleOrNull() == null) {
                        error = "Datos inválidos"
                        return@Button
                    }

                    val actualizado = ingreso.copy(
                        nombre = nombre.trim(),
                        valor = valor.toDoubleOrNull() ?: 0.0,
                        descripcion = descripcion.trim()
                    )

                    ingresoRepository.updateIngreso(actualizado) { success, errorMsg ->
                        if (success) onIngresoActualizado()
                        else error = errorMsg
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.primary)
            ) {
                Text("Guardar")
            }
        }

        error?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }
    }
}