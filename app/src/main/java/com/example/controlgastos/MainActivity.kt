package com.example.controlgastos

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)


        //Ejemplo de uso de DBHelper para manipular la base de datos SQLite
        //Creamos un objeto de tipo DBHelper
        val dbHelper = DBHelper(this)

        //Insertamos un usuario
        val usuarioId = dbHelper.userInsert("Tiago","pjesus.tiagob92@linkifp.online","112344","03/03/2025").toInt()

        //Insertar un gasto para el usuario
        dbHelper.gastosInsert("Pizza",usuarioId, "03.03.2025", "Pizza dominos", 30.00,"pendiente", false, "mensual")

        //Obtener usuarios
        val usuarios = dbHelper.selectUsuarios()
        usuarios.forEach{println("ID: ${it.first}, Nombre: ${it.second}")}

        //Obtener gastos  del usuario
        val gastos = dbHelper.gastosByUser(usuarioId)
        gastos.forEach{println("ID: ${it.first}, Nombre: ${it.second}, userID: ${it.third}")}
    }
}