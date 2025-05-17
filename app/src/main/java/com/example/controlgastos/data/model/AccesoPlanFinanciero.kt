package com.example.controlgastos.data.model

data class AccesoPlanFinanciero(
    val planId: String,
    val usuarioId: String,
    val rol: String = "lector",             // Ej.: "lector", "editor", "administrador"
    val esPropietario: Boolean = false,     // Si el usuario creó el plan
    val fechaAcceso: Long = System.currentTimeMillis()
)
