package com.example.controlgastos

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class PruebasActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pruebas)

        //Codigo MainActivity
        val btnUsuario = findViewById<Button>(R.id.btnUsuario)
        val btnGastos = findViewById<Button>(R.id.btnGastos)
        val btnIngresos = findViewById<Button>(R.id.btnIngresos)

        val dbHelper = DBHelper(this)
        val db = dbHelper.writableDatabase

        btnUsuario.setOnClickListener{
            val intent = Intent(this, UsuarioActivity::class.java)
            startActivity(intent)
        }

        btnGastos.setOnClickListener{
            val intent = Intent(this, GastosMenuActivity::class.java)
            startActivity(intent)
        }

        btnIngresos.setOnClickListener{
            val intent = Intent(this, IngresoActivity::class.java)
            startActivity(intent)
        }
    }
}