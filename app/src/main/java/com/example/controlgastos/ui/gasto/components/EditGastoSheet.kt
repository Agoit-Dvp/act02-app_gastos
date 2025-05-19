package com.example.controlgastos.ui.gasto.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.controlgastos.data.model.Gasto
import com.example.controlgastos.data.repository.GastoRepository
import com.example.controlgastos.ui.theme.AppColors
import java.text.SimpleDateFormat
import java.util.*

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
    var descripcion by remember { mutableStateOf(gasto.notas ?: "") }
    var metodoPago by remember { mutableStateOf(gasto.metodoPago) }
    var fecha by remember {
        mutableStateOf(SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(gasto.fecha))
    }
    var recurrente by remember { mutableStateOf(gasto.recurrente) }
    var iconoSeleccionado by remember { mutableStateOf(gasto.categoriaId) }

    var isSaving by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Editar Gasto", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(12.dp))

        TextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") })
        Spacer(Modifier.height(8.dp))

        TextField(value = valor, onValueChange = { valor = it }, label = { Text("Valor") })
        Spacer(Modifier.height(8.dp))

        TextField(value = descripcion, onValueChange = { descripcion = it }, label = { Text("Descripción") })
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

        Spacer(Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            OutlinedButton(onClick = {
                repository.deleteGasto(gasto.id) { success, msg ->
                    if (success) onGastoEliminado() else error = msg
                }
            }) {
                Text("Eliminar")
            }

            Button(
                onClick = {
                    val valorDouble = valor.toDoubleOrNull()
                    val parsedDate = try {
                        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(fecha)
                    } catch (e: Exception) { gasto.fecha }

                    if (nombre.isBlank() || valorDouble == null) {
                        error = "Campos inválidos"
                        return@Button
                    }

                    val actualizado = gasto.copy(
                        nombre = nombre,
                        valor = valorDouble,
                        fecha = parsedDate ?: gasto.fecha,
                        notas = descripcion,
                        metodoPago = metodoPago,
                        categoriaId = iconoSeleccionado,
                        recurrente = recurrente
                    )

                    repository.updateGasto(actualizado) { success, msg ->
                        if (success) onGastoActualizado() else error = msg
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
