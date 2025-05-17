package com.example.controlgastos.data.model

import java.util.Date

data class AccesoPlanFinanciero(
    val planId: String = "",
    val usuarioId: String = "",
    val rol: String = "lector",             // Ej.: "lector", "editor", "administrador"
    val esPropietario: Boolean = false,     // Si el usuario creó el plan
    val estado: String = "pendiente", // "pendiente", "aceptado", "rechazado"
    val fechaAcceso: Date = Date()
)
