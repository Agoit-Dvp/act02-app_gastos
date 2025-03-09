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

        listView.setOnItemClickListener { _, _, position, _ ->
            val ingreso = listView.adapter.getItem(position) as Ingreso

            // Crear un Intent para abrir DetalleIngresoActivity
            val intent = Intent(this, DetalleIngresoActivity::class.java)

            // Pasar el ID del ingreso (puedes pasar más información si lo necesitas)
            intent.putExtra("ingreso_id", ingreso.id)
            intent.putExtra("ingreso_nombre", ingreso.nombre)
            intent.putExtra("ingreso_monto", ingreso.monto)
            intent.putExtra("ingreso_fecha", ingreso.fecha)
            intent.putExtra("ingreso_desc", ingreso.descripcion)
            intent.putExtra("ingreso_categoria", ingreso.categoriaId)
            intent.putExtra("ingreso_usuario", ingreso.usuarioId)
            intent.putExtra("ingreso_recurrente", ingreso.recurrente)

            startActivity(intent)
        }
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

    // Recargar el ListView al regresar a la actividad
    override fun onResume() {
        super.onResume()
        cargarIngresos()
    }

}