package com.example.controlgastos.ui.ingreso.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.controlgastos.data.model.Ingreso
import com.example.controlgastos.ui.categoria.components.getPainterByName
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.CircleShape

@Composable
fun IngresoItem(ingreso: Ingreso, onClick: () -> Unit) {
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val fecha = formatter.format(ingreso.fecha)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = getPainterByName(ingreso.icono),
            contentDescription = ingreso.icono,
            modifier = Modifier
                .size(40.dp)
                .padding(end = 12.dp)
                .clip(CircleShape)
        )

        Column {
            Text("🟢 ${ingreso.nombre}")
            Text("💰 Valor: $${ingreso.valor}")
            Text("📝 ${ingreso.descripcion}")
            Text("📅 Fecha: $fecha")
            if (ingreso.recurrente) Text("🔁 Recurrente")
            if (ingreso.categoriaId.isNotBlank()) Text("🏷 Categoría: ${ingreso.categoriaId}")
        }
    }
}
