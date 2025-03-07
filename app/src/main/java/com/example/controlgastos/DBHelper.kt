package com.example.controlgastos

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.util.Date
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.ZoneId



class DBHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        //Querys para crear las tablas de las entidades definidas
        //Tabla usuario
        val createUserTable = """
            CREATE TABLE usuarios (
            id INTEGER  PRIMARY KEY AUTOINCREMENT,
            nombre TEXT NOT NULL,
            email TEXT NOT NULL UNIQUE,
            telefono TEXT NOT NULL,
            password TEXT NOT NULL,
            fechaCreacion TEXT NOT NULL
            )                    
        """.trimIndent()
        //Tabla categoria_gasto
        val createCategoriaGasto = """
            CREATE TABLE categoria_gasto(
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            nombre TEXT NOT NULL
            )
        """.trimIndent()

        //Tabla Gastos
        val createGastoTable = """
        CREATE TABLE gastos (
            id INTEGER PRIMARY KEY AUTOINCREMENT,        
            nombre TEXT NOT NULL,        
            fecha TEXT NOT NULL,
            nota TEXT NOT NULL,
            monto REAL NOT NULL,
            estado TEXT NOT NULL CHECK(estado IN('PENDIENTE', 'PAGADO', 'CANCELADO')),
            recurrente INTEGER NOT NULL DEFAULT 0 CHECK(recurrente IN(0,1)),
            frequencia TEXT NOT NULL CHECK(frequencia IN('diario','semanal','mensual','anual')),
            usuario_id INTEGER NOT NULL,
            categoria TEXT NOT NULL, -- Ahora almacena el nombre de la categoría
            metodo_pago TEXT NOT NULL CHECK(metodo_pago IN('Efectivo', 'Pago bancario / tarjeta', 'Otros'))
        )
        """.trimIndent()

        //Tabla categoria_ingreso
        val createCategoriaIngreso = """
            CREATE TABLE categoria_ingreso(
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            nombre TEXT NOT NULL
            )
        """.trimIndent()

        //Tabla ingreso
        val createIngreso = """
            CREATE TABLE ingresos(
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            usuario_id INTEGER NOT NULL,
            categoria_id INTEGER NOT NULL,
            descripcion TEXT NOT NULL,
            monto REAL NOT NULL,
            recurrente INTEGER NOT NULL DEFAULT 'Sin descripción',            
            fecha TEXT NOT NULL,
            FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
            FOREIGN KEY (categoria_id) REFERENCES categoria_ingreso(id) ON DELETE CASCADE
            )
        """.trimIndent()
        //Ejecutar query SQL create
        db.execSQL(createUserTable)
        db.execSQL(createCategoriaGasto)
        catByDefaultInsert(db)
        db.execSQL(createGastoTable)
        db.execSQL(createCategoriaIngreso)
        db.execSQL(createIngreso)
    }

    //Actualizaciones
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS usuarios")
        db.execSQL("DROP TABLE IF EXISTS categoria_gasto")
        db.execSQL("DROP TABLE IF EXISTS gastos")
        db.execSQL("DROP TABLE IF EXISTS categoria_ingreso")
        db.execSQL("DROP TABLE IF EXISTS ingresos")
        onCreate(db)
    }

    //Generar archivo de la base de datos
    companion object {
        private const val DATABASE_NAME = "ControlGatos.db"
        private const val DATABASE_VERSION = 1
    }


    //Metodos para manejar las tablas: Insertar, Consultar, Actualizar, Eliminar
    // ------------------------- INSERT: Insertar DATOS ------------------------------
    //Usuarios
    fun userInsert(
        nombre: String,
        email: String,
        telefono: String,
        password: String,
        fechaCreacion: String
    ): Long {
        val db = writableDatabase
        val valores = ContentValues().apply {
            put("nombre", nombre)
            put("email", email)
            put("telefono", telefono)
            put("password", password)
            put("fechaCreacion", fechaCreacion)
        }
        return db.insert("usuarios", null, valores)
    }

    //Categoria Gastos
    fun categoriaGastoInsert(nombre: String): Long {
        val db = writableDatabase
        val valores = ContentValues().apply {
            put("nombre", nombre)
        }
        return db.insert("categoria_gasto", null, valores)
    }

    fun catByDefaultInsert(db: SQLiteDatabase) {
        val categorias = listOf(
            "Vivienda",
            "Suministros",
            "Mercado",
            "Transporte",
            "Restaurante",
            "Ropa",
            "Salud",
            "Paseo",
            "Viaje",
            "Regalo",
            "Entretenimiento",
            "Mascota",
            "Créditos",
            "Otros"
        )
        categorias.forEach { categoria ->
            val valores = ContentValues().apply {
                put("nombre", categoria)
            }
            db.insertWithOnConflict(
                "categoria_gasto",
                null,
                valores,
                SQLiteDatabase.CONFLICT_IGNORE
            )
        }
    }

    //Gastos
    fun gastosInsert(
        nombre: String,
        fecha: String,
        nota: String,
        monto: Double,
        estado: String,
        recurrente: Boolean,
        frequencia: String,
        usuario_id: Int,
        categoria: String,
        metodo_pago: String
    ): Long {
        val db = writableDatabase
        val valores = ContentValues().apply {
            put("nombre", nombre)
            put("fecha", fecha)
            put("nota", nota)
            put("monto", monto)
            put("estado", estado)
            put("recurrente", if (recurrente) 1 else 0)
            put("frequencia", frequencia)
            put("usuario_id", usuario_id)
            put("categoria", categoria)
            put("metodo_pago", metodo_pago)
        }
        return db.insert("gastos", null, valores)
    }

    //Categoria ingreso
    fun categoriaIngresoInsert(nombre: String): Long {
        val db = writableDatabase
        val valores = ContentValues().apply {
            put("nombre", nombre)
        }
        return db.insert("categoria_presupuesto", null, valores)
    }

    //Ingreso
    fun ingresoInsert(
        usuario_id: Int,
        categoria_id: Int,
        descripcion: String,
        monto: Double,
        recurrente: Boolean,
        fecha: String
    ): Long {
        val db = writableDatabase
        val valores = ContentValues().apply {
            put("usuario_id", usuario_id)
            put("categoria_id", categoria_id)
            put("descripcion", descripcion)
            put("monto", monto)
            put("recurrente", if (recurrente) 1 else 0)
            put("fecha", fecha)
        }
        return db.insert("ingresos", null, valores)
    }

    // ------------------------- SELECT: OBTENER DATOS ------------------------------
    //Obtener Usuarios

    // Obtener todos los usuarios
    fun getUsuarios(): List<Usuario> {
        val db = readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT id, usuario, email, telefono, fechaCreacion FROM usuarios", null)
        val usuariosList = mutableListOf<Usuario>()

        while (cursor.moveToNext()) {
            val id = cursor.getInt(0)
            val usuario = cursor.getString(1)
            val email = cursor.getString(2)
            val telefono = cursor.getString(3)
            val fechaCreacion = Date(cursor.getLong(4))

            // Crear el objeto Usuario y añadirlo a la lista
            val usuarioObj = Usuario(id, usuario, "", email, telefono, fechaCreacion)
            usuariosList.add(usuarioObj)
        }

        cursor.close()
        return usuariosList
    }

    //Obtener Gastos
    //       por usuario
    fun gastosByUser(usuario_id: Int): List<Triple<Int, String, Double>> {
        val db = readableDatabase
        val cursor: Cursor = db.rawQuery(
            "SELECT id, nombre, monto from gastos where usuario_id = ?",
            arrayOf(usuario_id.toString())
        )
        val listaGastos = mutableListOf<Triple<Int, String, Double>>()

        while (cursor.moveToNext()) {
            val id = cursor.getInt(0)
            val nombre = cursor.getString(1)
            val monto = cursor.getDouble(2)
            listaGastos.add(Triple(id, nombre, monto))
        }

        cursor.close()
        return listaGastos
    }

    // Obtener gasto por ID con categoría y método de pago correctos
    fun obtenerGastoPorId(id: Int): Gasto? {
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery(
            "SELECT id, nombre, fecha, monto, estado, nota, categoria, usuario_id, metodo_pago FROM gastos WHERE id = ?",
            arrayOf(id.toString())
        )

        return if (cursor.moveToFirst()) {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val fecha = try {
                LocalDate.parse(cursor.getString(2), formatter)
            } catch (e: Exception) {
                LocalDate.now()
            }
            val date = Date.from(fecha.atStartOfDay(ZoneId.systemDefault()).toInstant())

            Gasto(
                id = cursor.getInt(0),
                nombre = cursor.getString(1),
                fecha = date,
                valor = cursor.getDouble(3),
                estado = cursor.getString(4),
                notas = cursor.getString(5) ?: "Sin notas",
                categoriaId = cursor.getString(6), // Recuperar el nombre de la categoría
                metodoPago = cursor.getString(8)
            )
        } else {
            null
        }.also { cursor.close() }
    }

    //Categoria gasto
    fun obtenerNombreCategoria(categoriaId: Int): String? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT nombre FROM categoria_gasto WHERE id = ?",
            arrayOf(categoriaId.toString())
        )
        return if (cursor.moveToFirst()) {
            cursor.getString(0)
        } else {
            null
        }.also { cursor.close() }
    }

    //Metodo pago gasto
    fun obtenerMetodoPagoPorId(id: Int): String {
        val db = readableDatabase
        val cursor =
            db.rawQuery("SELECT metodo_pago FROM gastos WHERE id = ?", arrayOf(id.toString()))
        return if (cursor.moveToFirst()) {
            cursor.getString(0)
        } else {
            "No especificado"
        }.also { cursor.close() }
    }

    //Obtener Categoria gastos
    fun selectCatGastos(): List<Pair<Int, String>> {
        val db = readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT id, nombre FROM categoria_gasto", null)
        val catList = mutableListOf<Pair<Int, String>>()
        while (cursor.moveToNext()) {
            val id = cursor.getInt(0)
            val nombre = cursor.getString(1)
            catList.add(Pair(id, nombre))
        }
        cursor.close()
        return catList
    }


    //Obtener Categoria ingreso
    fun selectCatIngreso(): List<Pair<Int, String>> {
        val db = readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT id, nombre FROM categoria_ingreso", null)
        val catIngreso = mutableListOf<Pair<Int, String>>()
        while (cursor.moveToNext()) {
            val id = cursor.getInt(0)
            val nombre = cursor.getString(1)
            catIngreso.add(Pair(id, nombre))
        }
        cursor.close()
        return catIngreso
    }

    // Obtener ingreso por ID: todos los campos
    fun obtenerIngresoPorId(id: Int): Ingreso? {
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery(
            "SELECT id, usuario_id, categoria_id, descripcion, monto, recurrente, fecha FROM ingresos WHERE id = ?",
            arrayOf(id.toString())
        )

        return if (cursor.moveToFirst()) {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val fecha = try {
                LocalDate.parse(cursor.getString(6), formatter)
            } catch (e: Exception) {
                LocalDate.now()
            }
            val date = Date.from(fecha.atStartOfDay(ZoneId.systemDefault()).toInstant())

            Ingreso(
                id = cursor.getInt(0),
                usuarioId = cursor.getInt(1),
                categoriaId = cursor.getInt(2),
                descripcion = cursor.getString(3),
                monto = cursor.getDouble(4),
                recurrente = cursor.getInt(cursor.getColumnIndexOrThrow("recurrente")) == 1,
                fecha = date
            )
        } else {
            null
        }.also { cursor.close() }
    }

    // ------------------------- DROP: ELIMINAR DATOS ------------------------------
    //Usuarios
    fun eliminarUsuario(id: Int): Int {
        val db = writableDatabase
        return db.delete("usuarios", "id = ?", arrayOf(id.toString()))
    }    
    
    // Eliminar un usuario
    fun eliminarUsuario(id: Int?): Int {
        val db = writableDatabase
        var result = 0
        try {
            // Verifica si el usuario existe antes de eliminarlo
            val cursor = db.rawQuery("SELECT * FROM usuarios WHERE id = ?", arrayOf(id.toString()))

            if (cursor.moveToFirst()) {
                // Usuario encontrado, proceder con la eliminación
                result = db.delete("usuarios", "id = ?", arrayOf(id.toString()))
            } else {
                // Usuario no encontrado
                Log.e("EliminarUsuario", "Usuario con ID $id no encontrado.")
            }

            cursor.close()
        } catch (e: Exception) {
            // Manejo de cualquier excepción
            Log.e("EliminarUsuario", "Error al eliminar el usuario con ID $id: ${e.message}")
        } finally {
            db.close()  // Cerrar la base de datos en el bloque finally
        }
        return result  // Retornar el resultado de la operación
    }
    

    //Gastos
    fun eliminarGasto(id: Int): Int {
        val db = writableDatabase
        return db.delete("gastos", "id = ?", arrayOf(id.toString()))
    }
    
    //Categorias Gastos

    //Ingresos
    fun eliminarIngreso(id: Int): Int {
        val db = writableDatabase
        return db.delete("ingreso", "id = ?", arrayOf(id.toString()))
    }

    //Categorias Ingresos
    fun eliminarCatIngreso(id: Int): Int {
        val db = writableDatabase
        return db.delete("ingreso", "id = ?", arrayOf(id.toString()))
    }

    // ------------------------- UPDATE: ACTUALIZAR DATOS ------------------------------
    //Usuarios
    fun actualizarUsuario(
        id: Int,
        nombre: String,
        email: String,
        telefono: String,
        pass: String
    ): Int {
        val db = writableDatabase
        val valores = ContentValues().apply {
            put("nombre", nombre)
            put("email", email)
            put("telefono", telefono)
            put("pass", telefono)
        }
        return db.update("usuarios", valores, "id =?", arrayOf(id.toString()))
    }
    
      fun actualizarDatosUsuario(usuarioEmail: String, nuevoNombre: String, nuevoTelefono: String, nuevoEmail: String, nuevaPassword: String): Boolean {
        val db = writableDatabase
        val valores = ContentValues().apply {
            put("nombre", nuevoNombre)           // Actualizar nombre
            put("telefono", nuevoTelefono)      // Actualizar teléfono
            put("email", nuevoEmail)            // Actualizar correo electrónico
            put("password", nuevaPassword)      // Actualizar contraseña
        }

        // Actualizamos los datos del usuario en la base de datos
        val resultado = db.update("usuarios", valores, "email = ?", arrayOf(usuarioEmail))

        return resultado > 0  // Si el número de filas actualizadas es mayor a 0, fue exitoso
    }

    //Gastos
    fun actualizarGasto(
        id: Int,
        nombre: String,
        fecha: String,
        nota: String,
        monto: Double,
        estado: String,
        recurrente: Boolean,
        frequencia: String,
        usuario_id: Int,
        categoria: String,
        metodo_pago: String
    ): Int {
        val db = writableDatabase
        val valores = ContentValues().apply {
            put("nombre", nombre)
            put("fecha", fecha)
            put("nota", nota)
            put("monto", monto)
            put("estado", estado)
            put("recurrente", if (recurrente) 1 else 0)
            put("frequencia", frequencia)
            put("usuario_id", usuario_id)
            put("categoria", categoria)
            put("metodo_pago", metodo_pago)
        }
        return db.update("gastos", valores, "id =?", arrayOf(id.toString()))
    }

    //Categoria Gastos
    fun actualizarCatGasto(id: Int, newName: String): Int{
        val db = writableDatabase
        val valores = ContentValues().apply {
            put("nombre", newName)
        }

        //Actualizar la categoria por id
        return db.update("categoria_ingreso", valores, "id =?", arrayOf(id.toString()))
    }

    //Ingresos
    fun actualizarIngreso(
        id: Int,
        nombre: String,
        fecha: String,
        nota: String,
        monto: Double,
        estado: String,
        recurrente: Boolean,
        frequencia: String,
        usuario_id: Int,
        categoria: String,
        metodo_pago: String
    ): Int {
        val db = writableDatabase
        val valores = ContentValues().apply {
            put("nombre", nombre)
            put("fecha", fecha)
            put("nota", nota)
            put("monto", monto)
            put("estado", estado)
            put("recurrente", if (recurrente) 1 else 0)
            put("frequencia", frequencia)
            put("usuario_id", usuario_id)
            put("categoria", categoria)
            put("metodo_pago", metodo_pago)
        }
        return db.update("ingresos", valores, "id =?", arrayOf(id.toString()))
    }

    //Categoria Gastos
    fun actualizarCatIngreso(id: Int, newName: String): Int{
        val db = writableDatabase
        val valores = ContentValues().apply {
            put("nombre", newName)
        }

        //Actualizar la categoria por id
        return db.update("categoria_ingreso", valores, "id =?", arrayOf(id.toString()))
    }
}