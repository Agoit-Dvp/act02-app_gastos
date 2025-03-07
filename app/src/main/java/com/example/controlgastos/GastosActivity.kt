package com.example.controlgastos

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class GastosActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gastos)

        val etNombre = findViewById<EditText>(R.id.etNombre)
        val etFecha = findViewById<EditText>(R.id.etFecha)
        val etValor = findViewById<EditText>(R.id.etValor)
        val spCategoria = findViewById<Spinner>(R.id.spCategoria)
        val spMetodoPago = findViewById<Spinner>(R.id.spMetodoPago)
        val spEstado = findViewById<Spinner>(R.id.spEstado)
        val etNotas = findViewById<EditText>(R.id.etNotas)
        val btnGuardar = findViewById<Button>(R.id.btnGuardar)

        val dbHelper = DBHelper(this)

        btnGuardar.setOnClickListener {
            val nombre = etNombre.text.toString()
            val fechaString = etFecha.text.toString()
            val valor = etValor.text.toString().toDoubleOrNull()
            val categoria = spCategoria.selectedItem.toString() // Guardar el nombre de la categoría
            val metodoPago = spMetodoPago.selectedItem.toString() // Guardar el nombre del método de pago
            val estado = spEstado.selectedItem.toString().uppercase(Locale.getDefault())
            val notas = etNotas.text.toString()

            if (nombre.isEmpty() || fechaString.isEmpty() || valor == null) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val fecha: Date = dateFormat.parse(fechaString) ?: Date()

            val usuarioId = 1 // Ajustar dinámicamente según el usuario logueado

            val gastoId = dbHelper.gastosInsert(
                nombre,
                fechaString,
                notas,
                valor,
                estado,
                false,
                "mensual",
                usuarioId,
                categoria,  // Guardar el nombre de la categoría
                metodoPago  // Guardar el nombre del método de pago
            )

            if (gastoId != -1L) {
                Toast.makeText(this, "Gasto guardado correctamente", Toast.LENGTH_LONG).show()
                val intent = Intent(this, GastosMenuActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Error al guardar el gasto", Toast.LENGTH_LONG).show()
            }
        }
    }
}
