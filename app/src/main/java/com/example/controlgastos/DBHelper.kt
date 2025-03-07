package com.example.controlgastos

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.util.Date
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.ZoneId



class DBHelper (context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase){
        //Querys para crear las tablas de las entidades definidas
        //Tabla usuario
        val createUserTable = """
            CREATE TABLE usuarios (
            id INTEGER  PRIMARY KEY AUTOINCREMENT,
            nombre TEXT NOT NULL,
            email TEXT NOT NULL UNIQUE,
            telefono TEXT NOT NULL,
            pass TEXT NOT NULL,
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
            CREATE TABLE ingreso(
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            usuario_id INTEGER NOT NULL,
            categoria_id INTEGER NOT NULL,
            descripcion TEXT NOT NULL,
            monto REAL NOT NULL,
            recurrente INTEGER NOT NULL DEFAULT 0 CHECK(recurrente IN(0,1)),            
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
        db.execSQL("DROP TABLE IF EXISTS ingreso")
        onCreate(db)
    }

    //Generar archivo de la base de datos
    companion object{
        private const val DATABASE_NAME = "ControlGatos.db"
        private const val DATABASE_VERSION = 1
    }


    //Metodos para manejar las tablas: Insertar, Consultar, Actualizar, Eliminar
    // ------------------------- INSERT: Insertar DATOS ------------------------------
    //Usuarios
    fun userInsert(nombre: String, email: String, telefono: String, pass: String, fechaCreacion: String): Long {
        val db = writableDatabase
        val valores = ContentValues().apply {
            put("nombre", nombre)
            put("email", email)
            put("telefono", telefono)
            put("pass", telefono)
            put("fechaCreacion", fechaCreacion)
        }
        return db.insert("usuarios",null,valores)
    }
    //Categoria Gastos
    fun categoriaGastoInsert(nombre: String): Long{
        val db = writableDatabase
        val valores = ContentValues().apply {
            put("nombre", nombre)
        }
        return db.insert("categoria_gasto", null, valores)
    }

    fun catByDefaultInsert(db: SQLiteDatabase){
        val categorias = listOf("Vivienda","Suministros","Mercado", "Transporte", "Restaurante", "Ropa", "Salud", "Paseo", "Viaje", "Regalo", "Entretenimiento", "Mascota", "Créditos", "Otros")
        categorias.forEach{ categoria ->
            val valores = ContentValues().apply {
                put("nombre", categoria)
            }
            db.insertWithOnConflict("categoria_gasto", null, valores, SQLiteDatabase.CONFLICT_IGNORE)
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
    fun categoriaIngresoInsert(nombre: String): Long{
        val db = writableDatabase
        val valores = ContentValues().apply {
            put("nombre", nombre)
        }
        return db.insert("categoria_presupuesto", null, valores)
    }

    //Ingreso
    fun ingresoInsert(usuario_id: Int, descripcion: String, monto: Double, recurrente: Boolean, fecha: String): Long{
        val db = writableDatabase
        val valores = ContentValues().apply {
            put("usuario_id", usuario_id)
            put("descripcion", descripcion)
            put("monto", monto)
            put("recurrente", if(recurrente) 1 else 0)
            put("fecha", fecha)
        }
        return db.insert("ingreso", null, valores)
    }

    // ------------------------- SELECT: OBTENER DATOS ------------------------------
    //Obtener Usuarios
    fun selectUsuarios(): List<Pair<Int, String>>{
        val db = readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT id, nombre FROM usuarios", null)
        val usersList = mutableListOf<Pair<Int, String>>()
        while (cursor.moveToNext()){
            val id = cursor.getInt(0)
            val nombre = cursor.getString(1)
            usersList.add(Pair(id, nombre))
        }
        cursor.close()
        return usersList
    }

    //Obtener Gastos
    //       por usuario
    fun gastosByUser(usuario_id: Int): List<Triple<Int, String, Double>>{
        val db = readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT id, nombre, monto from gastos where usuario_id = ?", arrayOf(usuario_id.toString())
        )
        val listaGastos = mutableListOf<Triple<Int, String, Double>>()

        while (cursor.moveToNext()){
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
        val cursor = db.rawQuery("SELECT nombre FROM categoria_gasto WHERE id = ?", arrayOf(categoriaId.toString()))
        return if (cursor.moveToFirst()) {
            cursor.getString(0)
        } else {
            null
        }.also { cursor.close() }
    }

    //Metodo pago gasto
    fun obtenerMetodoPagoPorId(id: Int): String {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT metodo_pago FROM gastos WHERE id = ?", arrayOf(id.toString()))
        return if (cursor.moveToFirst()) {
            cursor.getString(0)
        } else {
            "No especificado"
        }.also { cursor.close() }
    }

    //Obtener Categoria gastos
    fun selectCatGastos(): List<Pair<Int, String>>{
        val db = readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT id, nombre FROM categoria_gasto", null)
        val catList = mutableListOf<Pair<Int, String>>()
        while (cursor.moveToNext()){
            val id = cursor.getInt(0)
            val nombre = cursor.getString(1)
            catList.add(Pair(id, nombre))
        }
        cursor.close()
        return catList
    }


   //Obtener Categoria ingreso
    fun selectCatIngreso(): List<Pair<Int, String>>{
        val db = readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT id, nombre FROM categoria_ingreso", null)
        val catIngreso = mutableListOf<Pair<Int, String>>()
        while (cursor.moveToNext()){
            val id = cursor.getInt(0)
            val nombre = cursor.getString(1)
            catIngreso.add(Pair(id, nombre))
        }
        cursor.close()
        return catIngreso
    }

    // ------------------------- DROP: ELIMINAR DATOS ------------------------------
    //Usuarios
    fun eliminarUsuario(id: Int): Int {
        val db = writableDatabase
        return db.delete("usuarios", "id = ?", arrayOf(id.toString()))
    }

    //Gastos
    fun eliminarGasto(id: Int): Int{
        val db = writableDatabase
        return db.delete("gastos", "id = ?", arrayOf(id.toString()))
    }

    //Categorias Gastos

    //Ingresos
    fun eliminarIngreso(id: Int): Int{
        val db = writableDatabase
        return db.delete("ingreso", "id = ?", arrayOf(id.toString()))
    }

    //Categorias Ingresos
    fun eliminarCatIngreso(id: Int): Int{
        val db = writableDatabase
        return db.delete("ingreso", "id = ?", arrayOf(id.toString()))
    }

    // ------------------------- UPDATE: ACTUALIZAR DATOS ------------------------------
    //Usuarios

}