package com.example.controlgastos.ui.gasto.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.controlgastos.data.model.Gasto
import com.example.controlgastos.data.repository.CategoriaRepository
import com.example.controlgastos.ui.categoria.components.getPainterByName
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GastoItem(gasto: Gasto, onClick: () -> Unit) {
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val fechaFormateada = formatter.format(gasto.fecha)
    val categoriaRepo = remember { CategoriaRepository() }
    var categoriaNombre by remember { mutableStateOf("Cargando...") }
    var categoriaIconName by remember { mutableStateOf("otra") }

    LaunchedEffect(gasto.categoriaId) {
        categoriaRepo.obtenerCategoriaPorId(gasto.categoriaId) { categoria, _ ->
            categoriaNombre = categoria?.nombre ?: "Sin categoría"
            categoriaIconName = categoria?.iconName ?: "otra"
        }
    }

    Column(modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp)) {
        Text("Nombre: ${gasto.nombre}")
        Text("Valor: ${gasto.valor} ${gasto.moneda ?: ""}")
        Text("Categoría: $categoriaNombre")
        Text("Fecha: $fechaFormateada")
        Text("Método de pago: ${gasto.metodoPago}")
        //("Estado: ${gasto.estado}")
        gasto.notas?.takeIf { it.isNotBlank() }?.let {
            Text("Notas: $it")
        }
        if (gasto.recurrente) {
            Text("Recurrente: Sí (${gasto.frecuencia ?: "Sin frecuencia"})")
        }
    }
}