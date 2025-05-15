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
import com.example.controlgastos.ui.theme.AppColors

@Composable
fun EditCategoriaSheet(
    categoria: Categoria,
    onCategoriaActualizada: () -> Unit,
    onCategoriaEliminada: () -> Unit,
    categoriaRepository: CategoriaRepository = CategoriaRepository()
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
                    categoriaRepository.eliminarCategoria(categoria.id) { success, errorMsg ->
                        if (success) onCategoriaEliminada()
                        else error = errorMsg
                    }
                }
            ) {
                Text("Eliminar")
            }

            Button(
                onClick = {
                    if (nombre.isBlank()) {
                        error = "El nombre no puede estar vacío"
                        return@Button
                    }

                    val actualizada = categoria.copy(
                        nombre = nombre.trim(),
                        iconName = selectedIcon
                    )

                    categoriaRepository.actualizarCategoria(actualizada) { success, errorMsg ->
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