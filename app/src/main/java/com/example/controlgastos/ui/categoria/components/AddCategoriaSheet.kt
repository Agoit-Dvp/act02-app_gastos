package com.example.controlgastos.ui.categoria.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.controlgastos.data.model.Categoria
import com.example.controlgastos.data.repository.CategoriaRepository
import com.example.controlgastos.ui.categoria.CategoriaViewModel
import com.example.controlgastos.ui.theme.AppColors
import java.util.*

@Composable
fun AddCategoriaSheet(
    esIngreso: Boolean,
    usuarioId: String,
    planId: String,
    viewModel: CategoriaViewModel,
    onCategoriaCreada: () -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var selectedIcon by remember { mutableStateOf(customIcons.first().first) }
    var isSaving by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Nueva Categoría", style = MaterialTheme.typography.titleMedium)
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

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (nombre.isBlank()) {
                    error = "El nombre no puede estar vacío"
                    return@Button
                }

                if (viewModel.existeCategoriaConNombre(nombre, esIngreso)) {
                    error = "Ya existe una categoría con ese nombre"
                    return@Button
                }

                isSaving = true
                val nueva = Categoria(
                    id = UUID.randomUUID().toString(),
                    nombre = nombre.trim(),
                    esIngreso = esIngreso,
                    iconName = selectedIcon,
                    planId = planId
                )

                viewModel.agregarCategoria(nueva) { success, errorMsg ->
                    isSaving = false
                    if (success) onCategoriaCreada()
                    else error = errorMsg ?: "Error al guardar"
                }
            },
            enabled = !isSaving,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = AppColors.primary)
        ) {
            Text(if (isSaving) "Guardando..." else "Guardar")
        }

        error?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }
    }
}