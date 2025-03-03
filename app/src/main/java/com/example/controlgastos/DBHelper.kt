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
            id INTERGER PRIMARY KEY AUTOINCREMENTE,
            nombre TEXT NOT NUL
            )
        """.trimIndent()

        //Tabla Gastos
        val createGastoTable = """
        CREATE TABLE gastos (
        id INTERGER PRIMARY KEY AUTOINCREMENTE,
        usuario_id INTERGER NOT NULL,
        nombre TEXT NOT NULL,
        fecha TEXT NO NULL,
        nota TEXT NOT NULL,
        monto REAL NOT NULL,
        estado TEXT NOT NULL CHECK(estado IN('PENDIENTE', 'PAGADO', 'CANCELADO')),
        recurrente INTERGER NOT NULL DEFAULT 0 CHECK(recurrente IN(0,1)),
        frequencia TEXT NOT NULL CHECK(frequencia IN('diario','semanal','mensual','anual')),
        FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
        FOREIGN KEY (categoria_id) REFERENCES categoria_gasto(id) ON DELETE CASCADE       
        )
        
    """.trimIndent()
    }
}