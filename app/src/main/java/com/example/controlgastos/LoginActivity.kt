package com.example.controlgastos

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class LoginActivity : AppCompatActivity() {

    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var dbHelper: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initGUI()
        initDB()
        btnLogin.setOnClickListener {
            login()
        }

    }

    private fun initGUI() {
        edtEmail = findViewById(R.id.edtEmail)
        edtPassword = findViewById(R.id.edtPassword)
        btnLogin = findViewById(R.id.btnLogin)
    }

    private fun initDB() {
        dbHelper = DBHelper(this)
    }

    private fun login() {
        val email = edtEmail.text.toString()
        val password = edtPassword.text.toString()

        // Validar credenciales con la base de datos
        val usuario = dbHelper.autenticarUsuario(email, password)

        if (usuario != null) {
            // Guardar el usuario logueado
            UsuarioLogueado.usuarioId = usuario.id ?: -1
            UsuarioLogueado.nombreUsuario = usuario.usuario
            UsuarioLogueado.emailUsuario = usuario.email

            // Redirigir al usuario a la siguiente actividad
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()  // Finaliza la actividad de login para que no vuelva atrás
        } else {
            // Mostrar un mensaje de error
            Toast.makeText(this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
        }

    }
}