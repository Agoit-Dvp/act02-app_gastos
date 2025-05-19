package com.example.controlgastos.ui.ingreso.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.controlgastos.data.model.Ingreso
import com.example.controlgastos.data.repository.IngresoRepository
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddIngresoSheet(
    onIngresoGuardado: () -> Unit,
    ingresoRepository: IngresoRepository = IngresoRepository()
) {
    var nombre by remember { mutableStateOf("") }
    var valor by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var categoriaSeleccionada by remember { mutableStateOf("") }
    var categorias by remember { mutableStateOf(listOf<String>()) }
    var mostrarExtras by remember { mutableStateOf(false) }
    var fecha by remember { mutableStateOf("") }
    var recurrente by remember { mutableStateOf(false) }
    var iconoSeleccionado by remember { mutableStateOf("ingresos") }

    var isSaving by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        ingresoRepository.getCategoriasIngreso {
            categorias = it
            categoriaSeleccionada = it.firstOrNull() ?: ""
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Nuevo Ingreso", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))

        TextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") })
        Spacer(Modifier.height(8.dp))

        TextField(value = valor, onValueChange = { valor = it }, label = { Text("Valor") })
        Spacer(Modifier.height(8.dp))

        TextField(value = descripcion, onValueChange = { descripcion = it }, label = { Text("Descripción") })
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

        TextButton(onClick = { mostrarExtras = !mostrarExtras }) {
            Text(if (mostrarExtras) "Ocultar campos adicionales" else "Mostrar más campos")
        }

        if (mostrarExtras) {
            TextField(value = fecha, onValueChange = { fecha = it }, label = { Text("Fecha (dd/MM/yyyy)") })
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = recurrente, onCheckedChange = { recurrente = it })
                Text("Recurrente")
            }
        }

        errorMessage?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.height(16.dp))
        Button(
            onClick = {
                val uid = FirebaseAuth.getInstance().currentUser?.uid
                val valorDouble = valor.toDoubleOrNull()

                if (uid.isNullOrBlank() || nombre.isBlank() || valorDouble == null) {
                    errorMessage = "Verifica los campos"
                    return@Button
                }

                val parsedDate = try {
                    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(fecha)
                } catch (e: Exception) {
                    Date()
                }

                val nuevoIngreso = Ingreso(
                    nombre = nombre,
                    valor = valorDouble,
                    descripcion = descripcion,
                    fecha = parsedDate ?: Date(),
                    recurrente = recurrente,
                    categoriaId = categoriaSeleccionada,
                    usuarioId = uid,
                    icono = iconoSeleccionado
                )

                isSaving = true
                ingresoRepository.addIngreso(nuevoIngreso) { success, error ->
                    isSaving = false
                    if (success) onIngresoGuardado()
                    else errorMessage = error
                }
            },
            enabled = !isSaving,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isSaving) "Guardando..." else "Guardar")
        }
    }
}
