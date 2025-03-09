package com.example.controlgastos

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class IngresoActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper
    private lateinit var listView: ListView
    private lateinit var imgBtAdd: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ingreso)

        dbHelper = DBHelper(this)
        listView = findViewById(R.id.lvIngresos)

        initGUI()
        cargarIngresos()
        imgBtAdd.setOnClickListener{
            val intent = Intent(this, AddIngresoActivity::class.java)
            startActivity(intent)
        }
    }

    private fun cargarIngresos() {
        val ingresos = dbHelper.getAllIngresos()
        if (ingresos.isNotEmpty()) {
            val adapter = IngresoAdapter(this, ingresos)
            listView.adapter = adapter
        }else{
            Log.d("IngresoActivity", "No hay ingresos disponibles.")
        }
    }

    private fun initGUI(){
        imgBtAdd = findViewById(R.id.imgBtAdd)
    }

}