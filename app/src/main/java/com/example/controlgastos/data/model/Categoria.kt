package com.example.controlgastos.data.model

data class Categoria(
    val id: String = "",
    val nombre: String = "",
    val esIngreso: Boolean = true,
    val iconName: String = "category",
    val usuarioId: String = "",  // quién la creó
    val planId: String = ""      // a qué plan pertenece
)