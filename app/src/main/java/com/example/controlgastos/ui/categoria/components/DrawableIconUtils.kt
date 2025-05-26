package com.example.controlgastos.ui.categoria.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.painter.Painter
import com.example.controlgastos.R

val customIcons = listOf(
    "ingresos" to R.drawable.ic_ingresos_24,
    "gastos" to R.drawable.ic_gastos_24,
    "comida" to R.drawable.ic_cat_comida,
    "entretenimiento" to R.drawable.ic_cat_entretenimiento,
    "salud" to R.drawable.ic_cat_salud,
    "sueldo" to R.drawable.ic_cat_sueldo,
    "regalos" to R.drawable.ic_cat_regalo,
    "mercado" to R.drawable.ic_cat_mercado,
    "ropa" to R.drawable.ic_cat_ropa,
    "suministros" to R.drawable.ic_cat_suministros,
    "transporte" to R.drawable.ic_cat_transporte,
    "vivienda" to R.drawable.ic_cat_vivienda,
    "otra" to R.drawable.ic_cat_otra,
)

@Composable
fun getPainterByName(name: String): Painter {
    val iconRes = customIcons.find { it.first == name }?.second ?: R.drawable.ic_cat_otra
    return painterResource(id = iconRes)
}