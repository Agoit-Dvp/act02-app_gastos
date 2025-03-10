package com.example.controlgastos

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UsuarioActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_usuario)

        val etUsuario = findViewById<EditText>(R.id.etUsuario)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etTelefono = findViewById<EditText>(R.id.etTelefono)
        val btnGuardar = findViewById<Button>(R.id.btnGuardar)
        val btnActualizarPassword = findViewById<Button>(R.id.btnActualizarPassword)

        val dbHelper = DBHelper(this)

        btnGuardar.setOnClickListener {
            val nombre = etUsuario.text.toString()
            val password = etPassword.text.toString()
            val email = etEmail.text.toString()
            val telefono = etTelefono.text.toString()
            val fechaCreacion = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

            // Verificar si los campos no están vacíos
            if (nombre.isEmpty() || password.isEmpty() || email.isEmpty() || telefono.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Insertar un nuevo usuario
            val usuarioId = dbHelper.userInsert(nombre, email, telefono, password, fechaCreacion)

            if (usuarioId != -1L) {
                Toast.makeText(this, "Usuario registrado correctamente", Toast.LENGTH_LONG).show()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Error al registrar el usuario", Toast.LENGTH_LONG).show()
            }
        }

        btnActualizarPassword.setOnClickListener {
            val nombre = etUsuario.text.toString()
            val nuevoTelefono = etTelefono.text.toString()
            val nuevoEmail = etEmail.text.toString()
            val nuevaPassword = etPassword.text.toString()

            // Verificar que los campos necesarios no estén vacíos
            if (nombre.isEmpty() || nuevoTelefono.isEmpty() || nuevoEmail.isEmpty() || nuevaPassword.isEmpty()) {
                Toast.makeText(this, "Por favor, ingresa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Actualizar los datos del usuario
            val actualizado = dbHelper.actualizarDatosUsuario(nombre, nombre, nuevoTelefono, nuevoEmail, nuevaPassword)

            if (actualizado) {
                Toast.makeText(this, "Datos actualizados correctamente", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Error al actualizar los datos", Toast.LENGTH_LONG).show()
            }
        }
    }
}
