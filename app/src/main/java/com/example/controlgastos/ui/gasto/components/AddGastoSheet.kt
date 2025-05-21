package com.example.controlgastos.ui.gasto.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.controlgastos.data.model.Categoria
import com.example.controlgastos.data.model.Gasto
import com.example.controlgastos.data.repository.CategoriaRepository
import com.example.controlgastos.data.repository.GastoRepository
import com.google.firebase.auth.FirebaseAuth
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGastoSheet(
    planId: String,
    onGastoGuardado: () -> Unit,
    gastoRepository: GastoRepository = GastoRepository()
) {
    var nombre by remember { mutableStateOf("") }
    var valor by remember { mutableStateOf("") }

    //-------Para dropdown con opciones de categoria
    val categoriaRepository = CategoriaRepository()
    var categorias by remember { mutableStateOf<List<Categoria>>(emptyList()) }
    var categoriaSeleccionada by remember { mutableStateOf<Categoria?>(null) }
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(planId) {
        categoriaRepository.obtenerCategoriasDePlan(planId, esIngreso = false) { lista ->
            categorias = lista
            if (categoriaSeleccionada == null && lista.isNotEmpty()) {
                categoriaSeleccionada = lista[0] // selección por defecto opcional
            }
        }
    }
    //-------Para dropdown con opciones de categoria

    //----- Para dropdown con opciones de pago
    val metodosPago = listOf("Tarjeta", "Efectivo", "Transferencia")
    var metodoPagoSeleccionado by remember { mutableStateOf(metodosPago[0]) }
    var expandedPago by remember { mutableStateOf(false) }
    //----- Para dropdown con opciones de pago


    var descripcion by remember { mutableStateOf("") }
    var metodoPago by remember { mutableStateOf("") }

    var isSaving by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Nuevo Gasto", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))

        TextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") })
        Spacer(Modifier.height(8.dp))

        TextField(value = valor, onValueChange = { valor = it }, label = { Text("Valor") })
        Spacer(Modifier.height(8.dp))

        Text("Categoría", style = MaterialTheme.typography.labelLarge)

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                readOnly = true,
                value = categoriaSeleccionada?.nombre ?: "Selecciona una categoría",
                onValueChange = {},
                label = { Text("Categoría") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                categorias.forEach { categoria ->
                    DropdownMenuItem(
                        text = { Text(categoria.nombre) },
                        onClick = {
                            categoriaSeleccionada = categoria
                            expanded = false
                        }
                    )
                }
            }
        }
        Spacer(Modifier.height(8.dp))

        //Despliegable metodo de pago
        ExposedDropdownMenuBox(
            expanded = expandedPago,
            onExpandedChange = { expandedPago = !expandedPago }
        ) {
            OutlinedTextField(
                readOnly = true,
                value = metodoPagoSeleccionado,
                onValueChange = {},
                label = { Text("Método de pago") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedPago) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expandedPago,
                onDismissRequest = { expandedPago = false }
            ) {
                metodosPago.forEach { metodo ->
                    DropdownMenuItem(
                        text = { Text(metodo) },
                        onClick = {
                            metodoPagoSeleccionado = metodo
                            expandedPago = false
                        }
                    )
                }
            }
        }
        Spacer(Modifier.height(8.dp))

        TextField(value = descripcion, onValueChange = { descripcion = it }, label = { Text("Descripción") })

        if (!errorMessage.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(errorMessage ?: "", color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                val uid = FirebaseAuth.getInstance().currentUser?.uid
                val valorDouble = valor.toDoubleOrNull()

                if (uid.isNullOrBlank() || nombre.isBlank() || valorDouble == null) {
                    errorMessage = "Completa los campos correctamente"
                    return@Button
                }

                isSaving = true
                val nuevoGasto = Gasto(
                    nombre = nombre,
                    valor = valorDouble,
                    fecha = Date(),
                    categoriaId = categoriaSeleccionada?.id ?: "",
                    metodoPago = metodoPago,
                    //estado = estado,
                    notas = descripcion,
                    recurrente = false,
                    //frecuencia = frequencia,
                    usuarioId = uid,
                    planId = planId,
                )

                gastoRepository.addGasto(nuevoGasto) { success, error ->
                    isSaving = false
                    if (success) onGastoGuardado()
                    else errorMessage = error ?: "Error al guardar"
                }
            },
            enabled = !isSaving,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isSaving) "Guardando..." else "Guardar")
        }
    }
}