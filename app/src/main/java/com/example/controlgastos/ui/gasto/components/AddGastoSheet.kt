package com.example.controlgastos.ui.gasto.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.controlgastos.data.model.Gasto
import com.example.controlgastos.data.repository.GastoRepository
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AddGastoSheet(
    onGastoGuardado: () -> Unit,
    gastoRepository: GastoRepository = GastoRepository()
) {
    var nombre by remember { mutableStateOf("") }
    var valor by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var categoriaId by remember { mutableStateOf("") }
    var metodoPago by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf("") }
    var recurrente by remember { mutableStateOf(false) }
    var iconoSeleccionado by remember { mutableStateOf("gastos") }

    var isSaving by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Nuevo Gasto", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))

        TextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") })
        Spacer(Modifier.height(8.dp))

        TextField(value = valor, onValueChange = { valor = it }, label = { Text("Valor") })
        Spacer(Modifier.height(8.dp))

        TextField(value = descripcion, onValueChange = { descripcion = it }, label = { Text("Descripción") })
        Spacer(Modifier.height(8.dp))

        TextField(value = categoriaId, onValueChange = { categoriaId = it }, label = { Text("Categoría") })
        Spacer(Modifier.height(8.dp))

        TextField(value = metodoPago, onValueChange = { metodoPago = it }, label = { Text("Método de pago") })
        Spacer(Modifier.height(8.dp))

        TextField(value = fecha, onValueChange = { fecha = it }, label = { Text("Fecha (dd/MM/yyyy)") })
        Spacer(Modifier.height(8.dp))

        Text("Selecciona un icono", style = MaterialTheme.typography.labelLarge)
        IconSelectorGasto(selectedIcon = iconoSeleccionado) { iconoSeleccionado = it }

        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
            Checkbox(checked = recurrente, onCheckedChange = { recurrente = it })
            Text("Recurrente")
        }

        errorMessage?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                val uid = FirebaseAuth.getInstance().currentUser?.uid
                val valorDouble = valor.toDoubleOrNull()
                val parsedDate = try {
                    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(fecha)
                } catch (e: Exception) { Date() }

                if (uid.isNullOrBlank() || nombre.isBlank() || valorDouble == null) {
                    errorMessage = "Completa todos los campos correctamente"
                    return@Button
                }

                isSaving = true
                val nuevoGasto = Gasto(
                    nombre = nombre,
                    valor = valorDouble,
                    fecha = parsedDate ?: Date(),
                    categoriaId = iconoSeleccionado,
                    metodoPago = metodoPago,
                    estado = "Pagado",
                    notas = descripcion,
                    recurrente = recurrente,
                    usuarioId = uid
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
