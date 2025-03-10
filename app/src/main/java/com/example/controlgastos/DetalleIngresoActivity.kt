package com.example.controlgastos

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class DetalleIngresoActivity : AppCompatActivity() {
    private lateinit var dbHelper: DBHelper
    private lateinit var btnModificar: Button
    private lateinit var btnEliminar: Button
    private lateinit var edtNombre: EditText
    private lateinit var edtMonto: EditText
    private lateinit var edtFecha: EditText
    private lateinit var edtDesc: EditText
    private lateinit var edtCategoria: EditText
    private lateinit var spnCategoria: Spinner
    private lateinit var edtUsuario: EditText
    private lateinit var chkRecurrente: CheckBox
    private var usuarioId = UsuarioLogueado.usuarioId

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_ingreso)

        initGUI()
        initDB()
        receiveIntent()
        cargarCategorias()

        // Configurar el botón para modificar el ingreso
        btnModificar.setOnClickListener {
            // Acción para modificar el ingreso
            // Habilitar los campos que el usuario puede modificar
            edtNombre.isEnabled = true
            edtMonto.isEnabled = true
            edtFecha.isEnabled = true
            edtDesc.isEnabled = true
            spnCategoria.visibility = View.VISIBLE //Hacemos visible el spinner de categoria
            edtCategoria.visibility = View.INVISIBLE // Ocultamos el edittext categoria
            chkRecurrente.isEnabled = true

            // Cambiar el texto del botón a "Guardar"
            btnModificar.text = "Guardar"

            // Deshabilitar el botón de eliminar mientras se modifica
            btnEliminar.isEnabled = false

            // Cuando el usuario hace clic en "Guardar", actualizar los datos
            btnModificar.setOnClickListener {
                // Obtener los valores modificados por el usuario
                val nombreModificado = edtNombre.text.toString()
                val montoModificado = edtMonto.text.toString().toDoubleOrNull() ?: 0.0
                val fechaModificada = edtFecha.text.toString()
                val descripcionModificada = edtDesc.text.toString()
                val recurrenteModificado = chkRecurrente.isChecked

                // Verificar si los valores son válidos
                if (nombreModificado.isNotEmpty() && montoModificado > 0.0 && fechaModificada.isNotEmpty() && descripcionModificada.isNotEmpty()) {
                    // Aquí debes obtener el ID del ingreso desde el Intent
                    val ingresoId = intent.getIntExtra("ingreso_id", -1)

                    // Obtener la categoría seleccionada
                    val categoriaSeleccionada = spnCategoria.selectedItemPosition
                    val categoriaId = dbHelper.selectCatIngreso()[categoriaSeleccionada].first

                    // Llamar a la función de DBHelper para actualizar el ingreso en la base de datos

                    dbHelper.actualizarIngreso(
                        ingresoId,
                        nombreModificado,
                        usuarioId,
                        categoriaId,
                        descripcionModificada,
                        montoModificado,
                        recurrenteModificado,
                        fechaModificada
                    )

                    // Mostrar un mensaje de éxito
                    Toast.makeText(this, "Ingreso modificado correctamente", Toast.LENGTH_SHORT)
                        .show()

                    // Deshabilitar los campos nuevamente y cambiar el texto del botón de vuelta
                    edtNombre.isEnabled = false
                    edtMonto.isEnabled = false
                    edtFecha.isEnabled = false
                    edtDesc.isEnabled = false
                    edtCategoria.visibility = View.VISIBLE //Mostramos el edittext categoria
                    edtCategoria.isEnabled = false
                    spnCategoria.visibility = View.INVISIBLE
                    chkRecurrente.isEnabled = false

                    btnModificar.text = "Modificar"
                    btnEliminar.isEnabled = true
                } else {
                    // Si algún campo obligatorio no es válido, mostrar un mensaje
                    Toast.makeText(
                        this,
                        "Por favor, complete todos los campos correctamente.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

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
        spnCategoria = findViewById(R.id.spnCategoria)
        edtUsuario = findViewById(R.id.edtUsuario)
        chkRecurrente = findViewById(R.id.chkRecurrente)

        // Deshabilitar los EditText
        edtNombre.isEnabled = false
        edtMonto.isEnabled = false
        edtFecha.isEnabled = false
        edtDesc.isEnabled = false
        edtCategoria.isEnabled = false
        spnCategoria.visibility = View.INVISIBLE
        edtUsuario.isEnabled = false
        chkRecurrente.isEnabled = false
    }

    private fun initDB() {
        dbHelper = DBHelper(this)
    }

    private fun receiveIntent() {
        // Recibir los datos del Intent
        val ingresoNombre = intent.getStringExtra("ingreso_nombre")
        val ingresoMonto = intent.getDoubleExtra("ingreso_monto", 0.0)
        val ingresoFecha = intent.getStringExtra("ingreso_fecha")
        val ingresoDesc = intent.getStringExtra("ingreso_desc")
        val ingresoCategoria = intent.getIntExtra("ingreso_categoria", -1)
        val ingresoUsuario = intent.getStringExtra("ingreso_usuario")
        val ingresoRecurrente = intent.getBooleanExtra("ingreso_recurrente", false)

        //Obtener el nombre de la categoria a traves del ID
        val categoriaNombre = dbHelper.obtenerNombreCategoriaPorId("categoria_ingreso", ingresoCategoria)

        // Asignar los valores a los EditText
        edtNombre.setText(ingresoNombre)
        edtMonto.setText(ingresoMonto.toString())
        edtFecha.setText(ingresoFecha)
        edtDesc.setText(ingresoDesc)
        edtCategoria.setText(categoriaNombre)
        edtUsuario.setText(ingresoUsuario)
        chkRecurrente.isChecked = ingresoRecurrente
    }

    private fun cargarCategorias() {
        val categorias = dbHelper.selectCatIngreso()

        // Verificar que el método retorna categorías
        Log.d("DetalleIngresoActivity", "Categorías: $categorias")

        if (categorias.isNotEmpty()) {
            val categoriasAdapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                categorias.map { it.second }  // Asegúrate de que la categoría sea el valor correcto
            )
            categoriasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spnCategoria.adapter = categoriasAdapter
        } else {
            Log.d("DetalleIngresoActivity", "No se encontraron categorías")
        }
    }
}