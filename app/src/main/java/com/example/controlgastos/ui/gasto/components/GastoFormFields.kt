package com.example.controlgastos.ui.gasto.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.controlgastos.data.model.Categoria
import com.example.controlgastos.data.repository.CategoriaRepository
import com.example.controlgastos.ui.theme.AppColors

@Composable
fun NombreTextField(value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text("Nombre") },
        modifier = Modifier.fillMaxWidth(),
        colors = textFieldColors()
    )
}

@Composable
fun ValorTextField(value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text("Valor") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier.fillMaxWidth(),
        colors = textFieldColors()
    )
}

@Composable
fun DescripcionTextField(value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text("Descripción") },
        modifier = Modifier.fillMaxWidth(),
        colors = textFieldColors()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MetodoPagoDropdown(
    value: String,
    onValueSelected: (String) -> Unit
) {
    val opciones = listOf("Tarjeta", "Efectivo", "Transferencia")
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            readOnly = true,
            value = value,
            onValueChange = {},
            label = { Text("Método de pago") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            colors = textFieldColors()
        )

        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            opciones.forEach { metodo ->
                DropdownMenuItem(
                    text = { Text(metodo) },
                    onClick = {
                        onValueSelected(metodo)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriaDropdown(
    planId: String,
    categoriaSeleccionada: Categoria?,
    onCategoriaSeleccionada: (Categoria) -> Unit
) {
    val categoriaRepo = remember { CategoriaRepository() }
    var categorias by remember { mutableStateOf<List<Categoria>>(emptyList()) }
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(planId) {
        categoriaRepo.obtenerCategoriasDePlan(planId, esIngreso = false) {
            categorias = it
            if (categoriaSeleccionada == null && it.isNotEmpty()) {
                onCategoriaSeleccionada(it.first())
            }
        }
    }

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            readOnly = true,
            value = categoriaSeleccionada?.nombre ?: "Selecciona una categoría",
            onValueChange = {},
            label = { Text("Categoría") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            colors = textFieldColors()
        )

        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            categorias.forEach { categoria ->
                DropdownMenuItem(
                    text = { Text(categoria.nombre) },
                    onClick = {
                        onCategoriaSeleccionada(categoria)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun GuardarButton(enabled: Boolean, onClick: () -> Unit, text: String = "Guardar") {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = AppColors.primary,
            contentColor = Color.White,
            disabledContainerColor = AppColors.primary.copy(alpha = 0.5f),
            disabledContentColor = Color.White.copy(alpha = 0.5f)
        )
    ) {
        Text(text)
    }
}

@Composable
fun ErrorText(errorMessage: String?) {
    if (!errorMessage.isNullOrBlank()) {
        Text(errorMessage, color = MaterialTheme.colorScheme.error)
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
fun SpacerVertical(height: Dp = 8.dp) {
    Spacer(modifier = Modifier.height(height))
}

@Composable
private fun textFieldColors() = TextFieldDefaults.colors(
    focusedTextColor = Color.Black,
    unfocusedTextColor = Color.DarkGray,
    focusedIndicatorColor = AppColors.primary,
    unfocusedIndicatorColor = AppColors.secondary,
    focusedContainerColor = Color.White,
    unfocusedContainerColor = Color(0xFFF5F5F5)
)
