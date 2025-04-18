package com.example.controlgastos

import java.util.Date

data class Ingreso (
    val id: Int,
    val nombre: String,
    val usuarioId: Int,
    val categoriaId: Int,
    val descripcion: String,
    val monto: Double,
    val recurrente: Boolean,
    val fecha: Date
)
