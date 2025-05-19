package com.example.controlgastos.ui.ingreso.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.controlgastos.data.model.Ingreso
import com.example.controlgastos.data.repository.IngresoRepository
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
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
    var categoriaSeleccionada by remember { mutableStateOf(ingreso.categoriaId) }
    var categorias by remember { mutableStateOf(listOf<String>()) }
    var recurrente by remember { mutableStateOf(ingreso.recurrente) }
    var iconoSeleccionado by remember { mutableStateOf(ingreso.icono) }
    var fecha by remember {
        mutableStateOf(SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(ingreso.fecha))
    }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        ingresoRepository.getCategoriasIngreso {
            categorias = it
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Editar Ingreso", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(12.dp))

        TextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") })
        Spacer(Modifier.height(8.dp))

        TextField(value = valor, onValueChange = { valor = it }, label = { Text("Valor") })
        Spacer(Modifier.height(8.dp))

        TextField(value = descripcion, onValueChange = { descripcion = it }, label = { Text("Descripción") })
        Spacer(Modifier.height(8.dp))

        TextField(value = fecha, onValueChange = { fecha = it }, label = { Text("Fecha (dd/MM/yyyy)") })
        Spacer(Modifier.height(8.dp))

        if (categorias.isNotEmpty()) {
            ExposedDropdownMenuBox(expanded = false, onExpandedChange = {}) {
                TextField(
                    value = categoriaSeleccionada,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoría") },
                    trailingIcon = {
                        DropdownMenu(expanded = true, onDismissRequest = {}) {
                            categorias.forEach {
                                DropdownMenuItem(text = { Text(it) }, onClick = {
                                    categoriaSeleccionada = it
                                })
                            }
                        }
                    }
                )
            }
            Spacer(Modifier.height(8.dp))
        }

        Text("Selecciona un icono", style = MaterialTheme.typography.labelLarge)
        IconSelectorIngreso(
            selectedIcon = iconoSeleccionado,
            onIconSelected = { iconoSeleccionado = it }
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = recurrente, onCheckedChange = { recurrente = it })
            Text("Recurrente")
        }

        Spacer(Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            OutlinedButton(onClick = {
                ingresoRepository.deleteIngreso(ingreso.id) { success, msg ->
                    if (success) onIngresoEliminado() else error = msg
                }
            }) {
                Text("Eliminar")
            }

            Button(onClick = {
                if (nombre.isBlank() || valor.toDoubleOrNull() == null) {
                    error = "Revisa los campos"
                    return@Button
                }

                val parsedDate = try {
                    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(fecha)
                } catch (e: Exception) {
                    Date()
                }

                val actualizado = ingreso.copy(
                    nombre = nombre,
                    valor = valor.toDouble(),
                    descripcion = descripcion,
                    categoriaId = categoriaSeleccionada,
                    recurrente = recurrente,
                    icono = iconoSeleccionado,
                    fecha = parsedDate ?: Date()
                )

                ingresoRepository.updateIngreso(actualizado) { success, msg ->
                    if (success) onIngresoActualizado() else error = msg
                }
            }) {
                Text("Guardar")
            }
        }

        error?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }
    }
}

