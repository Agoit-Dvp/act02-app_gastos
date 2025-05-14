package com.example.controlgastos.data.model

data class Usuario(
    val uid: String = "",
    val nombre: String = "",
    val email: String = "",
    val fechaRegistro: Long = System.currentTimeMillis()
)