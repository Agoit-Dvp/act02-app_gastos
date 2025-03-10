package com.example.controlgastos

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity


class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        val btnUsuario = findViewById<ImageButton>(R.id.imgBtUser)
        val btnGastos = findViewById<ImageButton>(R.id.imgBtGastos)
        val btnIngresos = findViewById<ImageButton>(R.id.imgBtIngresos)
        val btnCloseSession = findViewById<ImageButton>(R.id.ImgBtCloseSession)

        val txTitle = findViewById<TextView>(R.id.txTitle)
        val txLoggedUser = findViewById<TextView>(R.id.txtuserLogged)

        txTitle.text = "Control de Gastos" // Ajustar según el valor necesario
        txLoggedUser.text = buildString {
            append("  User: ")
            append(UsuarioLogueado.nombreUsuario)
        }

        btnUsuario.setOnClickListener {
            val intent = Intent(this, ListaUsuariosActivity::class.java)
            startActivity(intent)
        }

        btnGastos.setOnClickListener {
            val intent = Intent(this, GastosMenuActivity::class.java)
            startActivity(intent)
        }

        btnIngresos.setOnClickListener {
            val intent = Intent(this, IngresoActivity::class.java)
            startActivity(intent)
        }

        btnCloseSession.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        //funciona para cerrar sesion
        fun cerrarSesion() {
            // Limpiar los datos del usuario logueado
            UsuarioLogueado.usuarioId = -1
            UsuarioLogueado.nombreUsuario = "User"
            UsuarioLogueado.emailUsuario = "user@correo.es"
            finish()  // Finaliza la actividad actual para que no pueda volver atrás
        }


    }
}