package com.example.controlgastos

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class GastosMenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gastos_menu)

        val btnAgregarGasto = findViewById<Button>(R.id.btnAgregarGasto)
        val btnVerGastos = findViewById<Button>(R.id.btnVerGastos)

        btnAgregarGasto.setOnClickListener {
            val intent = Intent(this, GastosActivity::class.java)
            startActivity(intent)
        }

        btnVerGastos.setOnClickListener {
            val intent = Intent(this, ListaGastosActivity::class.java)
            startActivity(intent)
        }
    }
}
