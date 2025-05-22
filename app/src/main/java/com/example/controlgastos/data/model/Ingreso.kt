package com.example.controlgastos.data.model

import java.util.Date

data class Ingreso(
    val id: String = "",
    val nombre: String = "",
    val valor: Double = 0.0,
    val descripcion: String = "",
    val fecha: Date = Date(),
    val recurrente: Boolean = false,
    val categoriaId: String = "",
    val usuarioId: String = "",
    val planId: String = ""
)
