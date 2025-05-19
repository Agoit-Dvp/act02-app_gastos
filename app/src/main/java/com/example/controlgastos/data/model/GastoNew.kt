package com.example.controlgastos.data.model

import java.util.Date

data class Gasto(
    val id: String = "",
    val nombre: String = "",
    val fecha: Date = Date(),
    val valor: Double = 0.0,
    val moneda: String? = null,
    val categoriaId: String = "",
    val metodoPago: String = "",
    val estado: String = "",
    val notas: String? = null,
    val recurrente: Boolean = false,
    val frecuencia: String? = null,
    val usuarioId: String = "",
    val icono: String = ""
)
