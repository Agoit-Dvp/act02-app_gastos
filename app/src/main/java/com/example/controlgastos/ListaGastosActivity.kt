package com.example.controlgastos

import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class ListaGastosActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_gastos)

        val listViewGastos = findViewById<ListView>(R.id.listViewGastos)
        val dbHelper = DBHelper(this)
        val gastos = dbHelper.gastosByUser(UsuarioLogueado.usuarioId) // Suponiendo que el usuario es el ID 1

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, gastos.map { it.second })
        listViewGastos.adapter = adapter

        listViewGastos.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val gastoSeleccionado = gastos[position]
            val intent = Intent(this, DetalleGastoActivity::class.java)
            intent.putExtra("gasto_id", gastoSeleccionado.first)
            startActivity(intent)
        }
    }
}
