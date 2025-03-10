package com.example.controlgastos

import android.app.AlertDialog
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import java.util.Locale

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
            // Formatear la fecha antes de pasarla al intent
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val fechaFormateada = sdf.format(ingreso.fecha)
            intent.putExtra("ingreso_fecha", fechaFormateada)
            intent.putExtra("ingreso_desc", ingreso.descripcion)
            intent.putExtra("ingreso_categoria", ingreso.categoriaId)
            intent.putExtra("ingreso_usuario", ingreso.usuarioId)
            intent.putExtra("ingreso_recurrente", ingreso.recurrente)

            startActivity(intent)
        }

        listView.setOnItemLongClickListener { _, _, position, _ ->
            val ingreso = listView.adapter.getItem(position) as Ingreso

            // Confirmar la eliminación (esto es opcional, pero recomendable)
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Eliminar Ingreso")
                .setMessage("¿Estás seguro de que quieres eliminar este ingreso?")
                .setPositiveButton("Sí") { dialog, _ ->
                    // Eliminar el ingreso de la base de datos
                    val success = dbHelper.eliminarIngreso(ingreso.id)

                    if (success) {
                        // Eliminar el ítem de la lista (se actualiza el adaptador)
                        cargarIngresos()
                    } else {
                        Toast.makeText(this, "Error al eliminar el ingreso", Toast.LENGTH_SHORT)
                            .show()
                    }

                    dialog.dismiss()
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
            builder.create().show()

            true // Indica que el evento ha sido manejado
        }

        imgBtAdd.setOnClickListener {
            val intent = Intent(this, AddIngresoActivity::class.java)
            startActivity(intent)
        }
    }


    private fun cargarIngresos() {
        val ingresos = dbHelper.getAllIngresos()
        if (ingresos.isNotEmpty()) {
            val adapter = IngresoAdapter(this, ingresos.toMutableList())
            listView.adapter = adapter
        } else {
            Log.d("IngresoActivity", "No hay ingresos disponibles.")
        }
    }

    private fun initGUI() {
        imgBtAdd = findViewById(R.id.imgBtAdd)
    }

    // Recargar el ListView al regresar a la actividad
    override fun onResume() {
        super.onResume()
        initGUI()
        cargarIngresos()
    }

}