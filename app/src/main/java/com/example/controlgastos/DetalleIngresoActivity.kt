package com.example.controlgastos

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class DetalleIngresoActivity : AppCompatActivity() {
    private lateinit var btnModificar: Button
    private lateinit var btnEliminar: Button
    private lateinit var edtNombre: EditText
    private lateinit var edtMonto: EditText
    private lateinit var edtFecha: EditText
    private lateinit var edtDesc: EditText
    private lateinit var edtCategoria: EditText
    private lateinit var edtUsuario: EditText
    private lateinit var edtRecurrente: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_ingreso)

        initGUI()
        // Recibir los datos del Intent
        val ingresoNombre = intent.getStringExtra("ingreso_nombre")
        val ingresoMonto = intent.getDoubleExtra("ingreso_monto", 0.0)
        val ingresoFecha = intent.getStringExtra("ingreso_fecha")
        val ingresoDesc = intent.getStringExtra("ingreso_desc")
        val ingresoCategoria = intent.getStringExtra("ingreso_categoria")
        val ingresoUsuario = intent.getStringExtra("ingreso_usuario")
        val ingresoRecurrente = intent.getStringExtra("ingreso_recurrente")

        // Asignar los valores a los EditText
        edtNombre.setText(ingresoNombre)
        edtMonto.setText(ingresoMonto.toString())
        edtFecha.setText(ingresoFecha)
        edtDesc.setText(ingresoDesc)
        edtCategoria.setText(ingresoCategoria)
        edtUsuario.setText(ingresoUsuario)
        edtRecurrente.setText(ingresoRecurrente)

        // Configurar el botón para modificar el ingreso
        btnModificar.setOnClickListener {
            // Acción para modificar el ingreso

        }

        // Configurar el botón para eliminar el ingreso
        btnEliminar.setOnClickListener {
            val ingresoId = intent.getIntExtra("ingreso_id", -1)
            // Lógica para eliminar el ingreso
            val dbHelper = DBHelper(this)
            dbHelper.eliminarIngreso(ingresoId)
            Toast.makeText(this, "Ingreso eliminado", Toast.LENGTH_SHORT).show()
            finish()
        }

    }

    private fun initGUI() {
        // Inicializar los componentes de la UI
        btnModificar = findViewById(R.id.btnAdd)
        btnEliminar = findViewById(R.id.btnEliminar)
        edtNombre = findViewById(R.id.edtNombre)
        edtMonto = findViewById(R.id.edtMonto)
        edtFecha = findViewById(R.id.edtFecha)
        edtDesc = findViewById(R.id.edtDesc)
        edtCategoria = findViewById(R.id.edtCategoria)
        edtUsuario = findViewById(R.id.edtUsuario)
        edtRecurrente = findViewById(R.id.chkRecurrente)

        // Deshabilitar los EditText, ya que no se deben editar directamente
        edtNombre.isEnabled = false
        edtMonto.isEnabled = false
        edtFecha.isEnabled = false
        edtDesc.isEnabled = false
        edtCategoria.isEnabled = false
        edtUsuario.isEnabled = false
        edtRecurrente.isEnabled = false
    }
}