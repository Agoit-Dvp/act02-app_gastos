package com.example.controlgastos.data.model

data class Categoria(
    val id: String = "",
    val nombre: String = "",
    val esIngreso: Boolean = true,
    val iconName: String = "category" // nombre del ícono por defecto
)
