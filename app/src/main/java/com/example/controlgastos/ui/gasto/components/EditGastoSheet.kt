package com.example.controlgastos.ui.gasto.components

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.controlgastos.data.model.Categoria
import com.example.controlgastos.data.model.Gasto
import com.example.controlgastos.data.repository.CategoriaRepository
import com.example.controlgastos.data.repository.GastoRepository
import com.example.controlgastos.ui.theme.AppColors

@Composable
fun EditGastoSheet(
    gasto: Gasto,
    onDismiss: () -> Unit,
    onGastoActualizado: () -> Unit,
    onGastoEliminado: () -> Unit,
    repository: GastoRepository = GastoRepository()
) {
    var nombre by remember { mutableStateOf(gasto.nombre) }
    var valor by remember { mutableStateOf(gasto.valor.toString()) }
    var notas by remember { mutableStateOf(gasto.notas ?: "") }
    var isSaving by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Editar Gasto", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(12.dp))

        TextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        TextField(
            value = valor,
            onValueChange = { valor = it },
            label = { Text("Valor") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        TextField(
            value = notas,
            onValueChange = { notas = it },
            label = { Text("Notas") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedButton(onClick = {
                repository.deleteGasto(gasto.id) { success, errorMsg ->
                    if (success) onGastoEliminado() else error = errorMsg
                }
            }) {
                Text("Eliminar")
            }

            Button(
                onClick = {
                    if (nombre.isBlank() || valor.toDoubleOrNull() == null) {
                        error = "Campos inválidos"
                        return@Button
                    }

                    val actualizado = gasto.copy(
                        nombre = nombre.trim(),
                        valor = valor.toDoubleOrNull() ?: gasto.valor,
                        notas = notas.trim()
                    )

                    repository.updateGasto(actualizado) { success, errorMsg ->
                        if (success) onGastoActualizado()
                        else error = errorMsg
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.primary)
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