package com.example.controlgastos

object ValidadorFormIngreso {
    fun esFormularioValido(nombre: String, fecha: String, monto: Double, descripcion: String): Boolean {
        return nombre.isNotEmpty() &&
                fecha.isNotEmpty() &&
                monto > 0.0 &&
                descripcion.isNotEmpty()
    }
}// Funcion para validar los campos vacios de AddIngreso
