package com.example.controlgastos.data.model

data class PlanFinanciero(
    val id: String = "",
    val nombre: String,
    val descripcion: String = "",
    val fechaCreacion: Long = System.currentTimeMillis(),
    val creadorId: String
)

//He dejado System.currentTimeMillis para ver la diferencia de date
