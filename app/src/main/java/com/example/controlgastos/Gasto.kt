package com.example.controlgastos

import java.util.Date

//Gastos en la aplicación
class Gasto (
    var id: Int? = null,
    var nombre: String,
    var fecha: Date,
    var valor: Double,
    var moneda: String? = null,
    var categoriaId: String,
    var metodoPago: String,
    var estado: String,
    var notas: String? = null,
    var recurrente: Boolean = false,
    var frecuencia: String? = null,
) {
    //Método mostrar info gasto
    override fun toString(): String {
        return "Gasto(id=$id, nombre='$nombre', fecha=$fecha, valor=$valor, moneda=$moneda," +
                "categoria='$categoriaId', metodoPago='$metodoPago', estado='$estado'," +
                "notas=$notas, recurrente=$recurrente, frecuencia=$frecuencia)"
    }
}
