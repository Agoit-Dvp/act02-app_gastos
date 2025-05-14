package com.example.controlgastos.data.model

data class Gasto(
    val id: String = "",
    val cantidad: Double = 0.0,
    val categoria: String = "",
    val fecha: Long = System.currentTimeMillis(),
    val descripcion: String = "",
    val usuarioId: String = ""
)