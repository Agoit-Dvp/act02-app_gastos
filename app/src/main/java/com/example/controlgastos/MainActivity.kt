package com.example.controlgastos

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_usuario)

        // Crear el objeto DBHelper para interactuar con la base de datos SQLite
        val dbHelper = DBHelper(this)

        // Generar la fecha actual en formato "dd/MM/yyyy"
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val fechaCreacion = sdf.format(Date()) // Fecha actual

        // Insertar un nuevo usuario
        val usuarioId = dbHelper.userInsert(
            "Tiago",
            "pjesus.tiagob92@linkifp.online",
            "112344",
            fechaCreacion,
            fechaCreacion
        ) // Este ID lo obtendrás directamente del resultado de `userInsert`


        // Obtener usuarios
        val usuarios = dbHelper.getUsuarios() // Esta función debe devolver una lista de objetos Usuario
        usuarios.forEach {
            println("ID: ${it.id}, Nombre: ${it.usuario}, Email: ${it.email}, Teléfono: ${it.telefono}")
        }

    }
}
