package com.example.controlgastos

import java.util.Date

class Usuario(
    var id: Int? = null,
    var usuario: String,
    var password: String,
    var email: String,
    var telefono: String,
    var fechaCreacion: Date
) {
    override fun toString(): String {
        return "Usuario(id=$id, usuario='$usuario', " +
                "password='****', email='$email', telefono='$telefono', " +
                "fechaCreacion=$fechaCreacion)"
    }

}