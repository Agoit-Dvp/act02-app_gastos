package com.example.controlgastos.ui.ingreso.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.controlgastos.data.model.Ingreso
import com.example.controlgastos.data.repository.IngresoRepository
import com.google.firebase.auth.FirebaseAuth
import java.util.*

@Composable
fun AddIngresoSheet(
    onIngresoGuardado: () -> Unit,
    ingresoRepository: IngresoRepository = IngresoRepository()
) {
    var nombre by remember { mutableStateOf("") }
    var valor by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Nuevo Ingreso", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))

        TextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") })
        Spacer(Modifier.height(8.dp))

        TextField(value = valor, onValueChange = { valor = it }, label = { Text("Valor") })
        Spacer(Modifier.height(8.dp))

        TextField(value = descripcion, onValueChange = { descripcion = it }, label = { Text("Descripción") })

        if (!errorMessage.isNullOrEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(errorMessage ?: "", color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                val uid = FirebaseAuth.getInstance().currentUser?.uid
                val valorDouble = valor.toDoubleOrNull()

                if (uid.isNullOrBlank() || nombre.isBlank() || valorDouble == null) {
                    errorMessage = "Verifica que los campos sean válidos"
                    return@Button
                }

                isSaving = true
                val nuevoIngreso = Ingreso(
                    nombre = nombre,
                    valor = valorDouble,
                    descripcion = descripcion,
                    fecha = Date(),
                    recurrente = false,
                    categoriaId = "", // si lo gestionas después
                    usuarioId = uid
                )

                ingresoRepository.addIngreso(nuevoIngreso) { success, error ->
                    isSaving = false
                    if (success) {
                        onIngresoGuardado()
                    } else {
                        errorMessage = error ?: "Error al guardar"
                    }
                }
            },
            enabled = !isSaving,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isSaving) "Guardando..." else "Guardar")
        }
    }
}
