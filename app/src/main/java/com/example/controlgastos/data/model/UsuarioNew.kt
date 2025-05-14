package com.example.controlgastos.data.model

import java.util.Date

data class Usuario(
    val uid: String = "",
    val nombre: String = "",
    val email: String = "",
    val telefono: String = "",
    val fechaRegistro: Date = Date()
)