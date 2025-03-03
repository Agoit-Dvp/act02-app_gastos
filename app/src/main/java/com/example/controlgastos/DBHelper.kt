package com.example.controlgastos

import android.content.Context
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

    companion object{
        private const val DATABASE_NAME = "ControlGatos.db"
        private const val DATABASE_VERSION = 1
    }
}