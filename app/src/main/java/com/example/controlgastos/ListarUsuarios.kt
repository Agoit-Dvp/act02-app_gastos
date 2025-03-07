package com.example.controlgastos

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat

class ListaUsuariosActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_usuario)

        val listViewUsuarios = findViewById<ListView>(R.id.listViewUsuarios)
        val btnEliminarUsuario = findViewById<Button>(R.id.btnEliminarUsuario)

        val dbHelper = DBHelper(this)
        val usuarios = dbHelper.getUsuarios() // Obtener lista de usuarios

        // Usamos un ArrayAdapter para mostrar solo los campos que necesitamos (nombre, email, telefono, fecha)
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_2,
            usuarios.map {
                "${it.usuario}\n${it.email}\n${it.telefono}\n${SimpleDateFormat("dd/MM/yyyy").format(it.fechaCreacion)}"
            }
        )
        listViewUsuarios.adapter = adapter

        // Variable para almacenar el usuario seleccionado
        var usuarioSeleccionado: Usuario? = null

        listViewUsuarios.setOnItemClickListener { _, _, position, _ ->
            // Al seleccionar un item, habilitamos el botón de eliminar
            usuarioSeleccionado = usuarios[position]
            btnEliminarUsuario.isEnabled = true
        }

        // Acción para eliminar usuario cuando se presiona el botón
        btnEliminarUsuario.setOnClickListener {
            usuarioSeleccionado?.let { usuario ->
                val resultado = dbHelper.eliminarUsuario(usuario.id)

                if (resultado > 0) {
                    Toast.makeText(this, "Usuario eliminado correctamente", Toast.LENGTH_LONG).show()

                    // Actualizar la lista de usuarios después de la eliminación
                    val updatedUsuarios = dbHelper.getUsuarios()  // Obtener la lista actualizada de usuarios

                    // Crear un nuevo adaptador con la lista actualizada
                    val updatedAdapter = ArrayAdapter(
                        this,
                        android.R.layout.simple_list_item_2,
                        updatedUsuarios.map {
                            "${it.usuario}\n${it.email}\n${it.telefono}\n${SimpleDateFormat("dd/MM/yyyy").format(it.fechaCreacion)}"
                        }
                    )

                    // Asignar el nuevo adaptador al ListView
                    listViewUsuarios.adapter = updatedAdapter

                    // Deshabilitar el botón de eliminar
                    btnEliminarUsuario.isEnabled = false
                } else {
                    Toast.makeText(this, "Error al eliminar el usuario", Toast.LENGTH_LONG).show()
                }
            } ?: run {
                Toast.makeText(this, "No se seleccionó ningún usuario", Toast.LENGTH_SHORT).show()
            }
        }

    }
}

