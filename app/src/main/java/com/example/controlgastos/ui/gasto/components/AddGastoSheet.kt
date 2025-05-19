package com.example.controlgastos.ui.gasto.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.controlgastos.data.model.Gasto
import com.example.controlgastos.data.repository.GastoRepository
import com.google.firebase.auth.FirebaseAuth
import java.util.*

@Composable
fun AddGastoSheet(
    planId: String,
    onGastoGuardado: () -> Unit,
    gastoRepository: GastoRepository = GastoRepository()
) {
    var nombre by remember { mutableStateOf("") }
    var valor by remember { mutableStateOf("") }
    var categoriaId by remember { mutableStateOf("") }
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

        TextField(value = categoriaId, onValueChange = { categoriaId = it }, label = { Text("Categoría") })
        Spacer(Modifier.height(8.dp))

        TextField(value = metodoPago, onValueChange = { metodoPago = it }, label = { Text("Método de pago") })
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
                    categoriaId = categoriaId,
                    metodoPago = metodoPago,
                    estado = "Pagado",
                    notas = descripcion,
                    recurrente = false,
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