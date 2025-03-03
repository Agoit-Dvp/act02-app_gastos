package com.example.controlgastos

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper (context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase){
        //Querys para crear las tablas de las entidades definidas
        //Tabla usuario
        val createUserTable = """
            CREATE TABLE usuarios (
            id INTERGER  PRIMARY KEY AUTOINCREMENT,
            nombre TEXT NOT NULL,
            email TEXT NOT NULL UNIQUE,
            telefono TEXT NOT NULL,
            fechaCreacion TEXT NOT NULL
            )                    
        """.trimIndent()
        //Tabla categoria_gasto
        val createCategoriaGasto = """
            CREATE TABLE categoria_gasto(
            id INTERGER PRIMARY KEY AUTOINCREMENT,
            nombre TEXT NOT NULL
            )
        """.trimIndent()

        //Tabla Gastos
        val createGastoTable = """
        CREATE TABLE gastos (
        id INTERGER PRIMARY KEY AUTOINCREMENT,        
        nombre TEXT NOT NULL,
        usuario_id INTERGER NOT NULL,
        fecha TEXT NOT NULL,
        nota TEXT NOT NULL,
        monto REAL NOT NULL,
        estado TEXT NOT NULL CHECK(estado IN('PENDIENTE', 'PAGADO', 'CANCELADO')),
        recurrente INTERGER NOT NULL DEFAULT 0 CHECK(recurrente IN(0,1)),
        frequencia TEXT NOT NULL CHECK(frequencia IN('diario','semanal','mensual','anual')),
        FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
        FOREIGN KEY (categoria_id) REFERENCES categoria_gasto(id) ON DELETE CASCADE       
        )
        
    """.trimIndent()

        //Tabla categoria_presupuesto
        val createCategoriaPresupuesto = """
            CREATE TABLE categoria_presupuesto(
            id INTERGER PRIMARY KEY AUTOINCREMENT,
            nombre TEXT NOT NULL            
            )
        """.trimIndent()

        //Tabla Presupuesto
        val createPresupuesto = """
            CREATE TABLE presupuesto(
            id INTERGER PRIMARY KEY AUTOINCREMENT,
            nombre TEXT NOT NULL,
            fechaInicio TEXT NOT NULL,
            fechaFin TEXT NOT NULL,
            montoAsignado REAL NOT NULL,
            notas TEXT NOT NULL,
            recurrente INTERGER NOT NULL DEFAULT 0 CHECK(recurrente IN(0,1)),
            frequencia TEXT NOT NULL CHECK(frequencia IN('mensual','trimestral','anual')),
            totalGastos REAL NOT NULL,
            valorDisponible REAL NOT NULL,
            usuario_id INTERGER NOT NULL,
            categoria_id INTERNGER NOT NULL,
            FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
            FOREIGN KEY (categoria_id) REFERENCES categoria_presupuesto(id) ON DELETE CASCADE            
            )
        """.trimIndent()

        //Tabla ingreso
        val createIngreso = """
            CREATE TABLE ingreso(
            id INTERGER PRIMARY KEY AUTOINCREMENT,
            usuario_id INTERGER NOT NULL,
            descripcion TEXT NOT NULL,
            monto REAL NOT NULL,
            recurrente INTERGER NOT NULL DEFAULT 0 CHECK(recurrente IN(0,1)),            
            fecha TEXT NOT NULL,
            FOREIGN KEY (userio_id) REFERENCES usuarios(id) ON DELETE CASCADE
            )
        """.trimIndent()
        //Ejecutar query SQL create
        db.execSQL(createUserTable)
        db.execSQL(createCategoriaGasto)
        db.execSQL(createGastoTable)
        db.execSQL(createCategoriaPresupuesto)
        db.execSQL(createPresupuesto)
        db.execSQL(createIngreso)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS usuarios")
        db.execSQL("DROP TABLE IF EXISTS categoria_gasto")
        db.execSQL("DROP TABLE IF EXISTS gastos")
        db.execSQL("DROP TABLE IF EXISTS categoria_presupuesto")
        db.execSQL("DROP TABLE IF EXISTS presupuesto")
        db.execSQL("DROP TABLE IF EXISTS ingreso")
        onCreate(db)
    }
    //Generar archivo de la base de datos
    companion object{
        private const val DATABASE_NAME = "ControlGatos.db"
        private const val DATABASE_VERSION = 1
    }

    //Metodos para manejar las tablas: Insertar, Consultar, Eliminar
    // ------------------------- INSERT: Insertar DATOS ------------------------------
    //Usuarios
    fun userInsert(nombre: String, email: String, telefono: String, fechaCreacion: String): Long {
        val db = writableDatabase
        val valores = ContentValues().apply {
            put("nombre", nombre)
            put("email", email)
            put("telefono", telefono)
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

    //Gastos
    fun gastosInsert(nombre: String, usuario_id: Int, fecha: String, nota: String, monto: Double, estado: String, recurrente: Boolean, frequencia: String): Long{
        val db = writableDatabase
        val valores = ContentValues().apply(){
            put("nombre", nombre)
            put("usuario_id",usuario_id)
            put("fecha", fecha)
            put("nota", nota)
            put("monto", monto)
            put("estado", estado)
            put("recurrente", if(recurrente) 1 else 0)
            put("frequencia", frequencia)
        }
        return db.insert("gastos", null, valores)
    }

    //Categoria presupuesto
    fun categoriaPresuInsert(nombre: String): Long{
        val db = writableDatabase
        val valores = ContentValues().apply {
            put("nombre", nombre)
        }
        return db.insert("categoria_presupuesto", null, valores)
    }

    //Presupuesto
    fun presupuestoInsert(nombre: String, fechaInicio: String, fechaFin: String, montoAsignado: Double, notas: String, recurrente: Boolean, frequencia: String, totalGastos: Double, valorDisponible: Double, usuario_id: Int, categoria_id: Int): Long{
        val db = writableDatabase
        val valores = ContentValues().apply {
            put("nombre", nombre)
            put("fechaInicio", fechaInicio)
            put("fechaFin", fechaFin)
            put("montoAsignado", montoAsignado)
            put("notas", notas)
            put("recurrente", if(recurrente) 1 else 0)
            put("frequencia", frequencia)
            put("totalGastos", totalGastos)
            put("valorDisponible", valorDisponible)
            put("usuario_id", usuario_id)
            put("categoria_id", categoria_id)
        }
        return db.insert("presupuesto", null, valores)
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

    //Obtener Categoria presupuesto
    fun selectCatPresupuesto(): List<Pair<Int, String>>{
        val db = readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT id, nombre FROM categoria_presupuesto", null)
        val catPresupuesto = mutableListOf<Pair<Int, String>>()
        while (cursor.moveToNext()){
            val id = cursor.getInt(0)
            val nombre = cursor.getString(1)
            catPresupuesto.add(Pair(id, nombre))
        }
        cursor.close()
        return catPresupuesto
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
}