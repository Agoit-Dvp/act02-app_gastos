package com.example.controlgastos

import android.content.Intent
import android.os.Bundle
import android.widget.Button
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

/*        // Crear el objeto DBHelper para interactuar con la base de datos SQLite
        val dbHelper = DBHelper(this)

        // Generar la fecha actual en formato "dd/MM/yyyy"
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val fechaCreacion = sdf.format(Date()) // Fecha actual*/

        val dbHelper = DBHelper(this)

        if (dbHelper.hayUsuarios()) {
            // Si hay usuarios, abrir LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        } else {
            // Si no hay usuarios, abrir UsuarioActivity para crear uno
            val intent = Intent(this, UsuarioActivity::class.java)
            startActivity(intent)
        }

        finish() // Cierra MainActivity para que no se pueda volver atrás

    }
}
