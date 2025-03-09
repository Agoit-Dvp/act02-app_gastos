package com.example.controlgastos

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class AddIngresoActivity : AppCompatActivity() {
    private lateinit var dbHelper: DBHelper
    private lateinit var btnAdd: Button
    private lateinit var edtNombre: EditText
    private lateinit var edtFecha: EditText
    private lateinit var edtMonto: EditText
    private lateinit var edtDesc: EditText
    private lateinit var chkRecurrente: CheckBox
    private lateinit var spnCategoria: Spinner
    private lateinit var edtUsuario: EditText
    val usuarioId = UsuarioLogueado.usuarioId

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_ingreso)

        // Vincular de las vistas
        btnAdd = findViewById(R.id.btnAdd)
        edtNombre = findViewById(R.id.edtNombre)
        edtFecha = findViewById(R.id.edtFecha)
        edtMonto = findViewById(R.id.edtMonto)
        edtDesc = findViewById(R.id.edtDesc)
        chkRecurrente = findViewById(R.id.chkRecurrente)
        spnCategoria = findViewById(R.id.spnCategoria)
        edtUsuario = findViewById(R.id.edtUsuario)

        initGUI()
        initDB()
        cargarCategorias()
        checkLogin()

        btnAdd.setOnClickListener {
            agregarIngreso(usuarioId)
        }

    }

    private fun initGUI(){
        // Inicialización de las vistas
        btnAdd = findViewById(R.id.btnAdd)
        edtNombre = findViewById(R.id.edtNombre)
        edtFecha = findViewById(R.id.edtFecha)
        edtMonto = findViewById(R.id.edtMonto)
        edtDesc = findViewById(R.id.edtDesc)
        chkRecurrente = findViewById(R.id.chkRecurrente)
        spnCategoria = findViewById(R.id.spnCategoria)
        edtUsuario = findViewById(R.id.edtUsuario)
    }

    private fun initDB(){
        dbHelper = DBHelper(this)
    }

    private fun cargarCategorias() {
        val categorias = dbHelper.selectCatIngreso()
        val categoriasAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            categorias.map { it.second }
        )
        categoriasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spnCategoria.adapter = categoriasAdapter
    }

    private fun checkLogin(){
        if (usuarioId == -1) {
            // Redirigir al login si no está logueado
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()  // Finalizar la actividad actual
        }
    }

    private fun agregarIngreso(usuarioId: Int) {
        // Obtener los valores de los campos
        val nombre = edtNombre.text.toString()
        val fecha = edtFecha.text.toString()
        val monto = edtMonto.text.toString().toDoubleOrNull() ?: 0.0
        val descripcion = edtDesc.text.toString()
        val recurrente = chkRecurrente.isChecked

        if (nombre.isEmpty() || fecha.isEmpty() || monto <= 0.0 || descripcion.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos.", Toast.LENGTH_SHORT).show()
            return
        }

        // Obtener la categoría seleccionada
        val categoriaSeleccionada = spnCategoria.selectedItemPosition
        val categoriaId = dbHelper.selectCatIngreso()[categoriaSeleccionada].first

        // Insertar el ingreso en la base de datos
        val ingresoId = dbHelper.ingresoInsert(
            nombre,
            usuarioId,
            categoriaId,
            descripcion,
            monto,
            recurrente,
            fecha
        )

        if (ingresoId > 0) {
            Toast.makeText(this, "Ingreso agregado correctamente", Toast.LENGTH_SHORT).show()
            limpiarCampos()  // Limpiar los campos para agregar otro ingreso
        } else {
            Toast.makeText(this, "Error al agregar el ingreso", Toast.LENGTH_SHORT).show()
        }
    }
    //Limpiar edits text, para nueva insercción
    private fun limpiarCampos() {
        // Limpiar los campos para un nuevo ingreso
        edtNombre.text.clear()
        edtFecha.text.clear()
        edtMonto.text.clear()
        edtDesc.text.clear()
        chkRecurrente.isChecked = false
        spnCategoria.setSelection(0)
    }


}