package com.example.controlgastos

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_usuario)

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
