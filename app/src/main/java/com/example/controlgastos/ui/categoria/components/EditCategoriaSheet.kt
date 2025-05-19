package com.example.controlgastos.ui.categoria.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.example.controlgastos.data.repository.CategoriaRepository
import com.example.controlgastos.ui.categoria.CategoriaViewModel
import com.example.controlgastos.ui.theme.AppColors

@Composable
fun EditCategoriaSheet(
    categoria: Categoria,
    viewModel: CategoriaViewModel,
    onCategoriaActualizada: () -> Unit,
    onCategoriaEliminada: () -> Unit
) {
    var nombre by remember { mutableStateOf(categoria.nombre) }
    var selectedIcon by remember { mutableStateOf(categoria.iconName) }
    var isSaving by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Editar Categoría", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(12.dp))

        TextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))
        Text("Selecciona un ícono", style = MaterialTheme.typography.bodyMedium)
        IconSelector(selectedIcon = selectedIcon, onIconSelected = { selectedIcon = it })

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(
                onClick = {
                    viewModel.eliminarCategoria(
                        id = categoria.id,
                        planId = categoria.planId,
                        esIngreso = categoria.esIngreso
                    ) { success, errorMsg ->
                        if (success) onCategoriaEliminada()
                        else error = errorMsg
                    }
                }
            )  {
                Text("Eliminar")
            }

            Button(
                onClick = {
                    if (nombre.isBlank()) {
                        error = "El nombre no puede estar vacío"
                        return@Button
                    }

                    val nombreLimpio = nombre.trim()

                    // Validación: evitar duplicados excepto si es la misma categoría
                    val nombreDuplicado = viewModel
                        .categorias
                        .value
                        ?.any {
                            it.nombre.equals(nombreLimpio, ignoreCase = true)
                                    && it.esIngreso == categoria.esIngreso
                                    && it.id != categoria.id // excluye a sí misma
                        } ?: false

                    if (nombreDuplicado) {
                        error = "Ya existe otra categoría con ese nombre"
                        return@Button
                    }

                    val actualizada = categoria.copy(
                        nombre = nombreLimpio,
                        iconName = selectedIcon
                    )

                    viewModel.actualizarCategoria(actualizada) { success, errorMsg ->
                        if (success) onCategoriaActualizada()
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