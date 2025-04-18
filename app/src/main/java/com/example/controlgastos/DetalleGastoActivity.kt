package com.example.controlgastos

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Locale

class DetalleGastoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_gasto)

        val tvNombre = findViewById<TextView>(R.id.tvNombre)
        val tvFecha = findViewById<TextView>(R.id.tvFecha)
        val tvValor = findViewById<TextView>(R.id.tvValor)
        val tvCategoria = findViewById<TextView>(R.id.tvCategoria)
        val tvMetodoPago = findViewById<TextView>(R.id.tvMetodoPago)
        val tvEstado = findViewById<TextView>(R.id.tvEstado)
        val tvNotas = findViewById<TextView>(R.id.tvNotas)
        val btnEliminar = findViewById<Button>(R.id.btnEliminar)

        val gastoId = intent.getIntExtra("gasto_id", -1)
        if (gastoId != -1) {
            val dbHelper = DBHelper(this)
            val gasto = dbHelper.obtenerGastoPorId(gastoId)

            if (gasto != null) {
                tvNombre.text = gasto.nombre

                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                tvFecha.text = dateFormat.format(gasto.fecha)

                tvValor.text = "${gasto.valor} EUR"
                tvCategoria.text = gasto.categoriaId
                tvMetodoPago.text = gasto.metodoPago

                tvEstado.text = gasto.estado
                tvNotas.text = gasto.notas ?: "Sin notas"
            }

            btnEliminar.setOnClickListener {
                AlertDialog.Builder(this)
                    .setTitle("Eliminar Gasto")
                    .setMessage("¿Estás seguro de que deseas eliminar este gasto?")
                    .setPositiveButton("Sí") { _, _ ->
                        val resultado = dbHelper.eliminarGasto(gastoId)
                        if (resultado > 0) {
                            Toast.makeText(this, "Gasto eliminado correctamente", Toast.LENGTH_LONG)
                                .show()

                            // Verificar si quedan más gastos
                            val gastosRestantes =
                                dbHelper.gastosByUser(UsuarioLogueado.usuarioId) // Suponiendo usuario_id = 1

                            val intent = if (gastosRestantes.isEmpty()) {
                                Intent(
                                    this,
                                    GastosMenuActivity::class.java
                                ) // Si no hay gastos, ir a GastosMenuActivity
                            } else {
                                Intent(
                                    this,
                                    ListaGastosActivity::class.java
                                ) // Si hay gastos, ir a ListaGastosActivity
                            }

                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this, "Error al eliminar el gasto", Toast.LENGTH_LONG)
                                .show()
                        }
                    }
                    .setNegativeButton("No", null)
                    .show()
            }
        }
    }
}
