package com.example.controlgastos.data.model

import java.util.Date

data class Gasto(
    val id: String = "",
    val nombre: String = "",               // equivalente al campo nombre del gasto
    val fecha: Date = Date(), // como timestamp
    val valor: Double = 0.0,
    val moneda: String? = null,
    val categoriaId: String = "",          // antes solo 'categoria', ahora el ID (más flexible)
    val metodoPago: String = "",
    val estado: String = "",               // por ejemplo: "Pagado", "Pendiente"
    val notas: String? = null,
    val recurrente: Boolean = false,
    val frecuencia: String? = null,        // "Mensual", "Semanal", etc.
    val usuarioId: String = ""             // ID del usuario que hizo el gasto
)
