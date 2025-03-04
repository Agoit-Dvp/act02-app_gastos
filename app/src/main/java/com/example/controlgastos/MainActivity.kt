package com.example.controlgastos

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        //****************************************************************
/*        //Ejemplo de uso de DBHelper para manipular la base de datos SQLite
        //Creamos un objeto de tipo DBHelper
        val dbHelper = DBHelper(this)

        //Insertamos un usuario
        val usuarioId = dbHelper.userInsert("Tiago","pjesus.tiagob92@linkifp.online","112344","sdfj23476l","03/03/2025").toInt()

        //Insertar un gasto para el usuario
        dbHelper.gastosInsert("Pizza",usuarioId, "03.03.2025", "Pizza dominos", 30.00,"pendiente", false, "mensual")

        //Obtener usuarios
        val usuarios = dbHelper.selectUsuarios()
        usuarios.forEach{println("ID: ${it.first}, Nombre: ${it.second}")}

        //Obtener gastos  del usuario
        val gastos = dbHelper.gastosByUser(usuarioId)
        gastos.forEach{println("ID: ${it.first}, Nombre: ${it.second}, userID: ${it.third}")}*/

        //********************************************************************

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
            val intent = Intent(this, GastosActivity::class.java)
            startActivity(intent)
        }

        btnIngresos.setOnClickListener{
            val intent = Intent(this, IngresoActivity::class.java)
            startActivity(intent)
        }





    }
}