package com.example.controlgastos.ui.ingreso.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.controlgastos.data.model.Ingreso
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun IngresoItem(ingreso: Ingreso) {
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val fecha = formatter.format(ingreso.fecha)

    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text("Nombre: ${ingreso.nombre}")
        Text("Valor: $${ingreso.valor}")
        Text("Descripción: ${ingreso.descripcion}")
        Text("Fecha: $fecha")
        if (ingreso.recurrente) {
            Text("Recurrente: Sí")
        }
    }
}