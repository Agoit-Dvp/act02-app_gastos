package com.example.controlgastos.data.model

import java.util.Date

data class PlanFinanciero(
    val id: String = "",                    // ID del plan (Firestore document ID)
    val nombre: String = "",
    val descripcion: String = "",
    val creadorId: String = "",             // ID del usuario creador
    val fechaCreacion: Date = Date()
)
