package com.example.controlgastos.ui.gasto.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.controlgastos.data.model.Gasto
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun GastoItem(gasto: Gasto, onClick: () -> Unit) {
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val fechaFormateada = formatter.format(gasto.fecha)

    Column(modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp)) {
        Text("Nombre: ${gasto.nombre}")
        Text("Valor: ${gasto.valor} ${gasto.moneda ?: ""}")
        Text("Categoría: ${gasto.categoriaId}")
        Text("Fecha: $fechaFormateada")
        Text("Método de pago: ${gasto.metodoPago}")
        Text("Estado: ${gasto.estado}")
        gasto.notas?.takeIf { it.isNotBlank() }?.let {
            Text("Notas: $it")
        }
        if (gasto.recurrente) {
            Text("Recurrente: Sí (${gasto.frecuencia ?: "Sin frecuencia"})")
        }
    }
}