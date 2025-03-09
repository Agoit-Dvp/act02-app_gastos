package com.example.controlgastos

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class PruebasActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pruebas)

        //Codigo MainActivity
        val btnUsuario = findViewById<Button>(R.id.btnUsuario)
        val btnGastos = findViewById<Button>(R.id.btnGastos)
        val btnIngresos = findViewById<Button>(R.id.btnIngresos)
        val btnLoginTest = findViewById<Button>(R.id.btnLoginTest)
        val btnMain = findViewById<Button>(R.id.btnMain)
        val txNombreU = findViewById<TextView>(R.id.textNombreU)
        val txCorreo = findViewById<TextView>(R.id.textCorreo)

        val dbHelper = DBHelper(this)
        val db = dbHelper.writableDatabase


        txNombreU.text = UsuarioLogueado.nombreUsuario
        txCorreo.text = UsuarioLogueado.emailUsuario


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

        btnLoginTest.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        btnMain.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

    }
}