package com.example.controlgastos.data.initializer

import android.util.Log
import com.example.controlgastos.R
import com.example.controlgastos.data.model.Categoria
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirestoreInitializer(private val usuarioId: String) {

    private val db = FirebaseFirestore.getInstance()

    suspend fun inicializarCategoriasSuspend(planId: String) {
        val ref = db.collection("categorias")
            .whereEqualTo("usuarioId", usuarioId)
            .whereEqualTo("planId", planId)

        try {
            val snapshot = ref.get().await()

            if (snapshot.isEmpty) {
                val categorias = listOf(
                    Categoria(nombre = "Salario", esIngreso = true, iconName = "sueldo", usuarioId = usuarioId, planId = planId),
                    Categoria(nombre = "Otros ingresos", esIngreso = true, iconName = "otra", usuarioId = usuarioId, planId = planId),
                    Categoria(nombre = "Comida", esIngreso = false, iconName = "comida", usuarioId = usuarioId, planId = planId),
                    Categoria(nombre = "Transporte", esIngreso = false, iconName = "transporte", usuarioId = usuarioId, planId = planId),
                    Categoria(nombre = "Entretenimiento", esIngreso = false, iconName = "entretenimiento", usuarioId = usuarioId, planId = planId),
                    Categoria(nombre = "Salud", esIngreso = false, iconName = "salud", usuarioId = usuarioId, planId = planId),
                    Categoria(nombre = "Vivienda", esIngreso = false, iconName = "vivienda", usuarioId = usuarioId, planId = planId),
                    Categoria(nombre = "Mercado", esIngreso = false, iconName = "mercado", usuarioId = usuarioId, planId = planId),
                    Categoria(nombre = "Ropa", esIngreso = false, iconName = "ropa", usuarioId = usuarioId, planId = planId),
                    Categoria(nombre = "Suministros", esIngreso = false, iconName = "suministros", usuarioId = usuarioId, planId = planId),
                    Categoria(nombre = "Regalos", esIngreso = true, iconName = "regalos", usuarioId = usuarioId, planId = planId),
                    Categoria(nombre = "Otros gastos", esIngreso = false, iconName = "otra", usuarioId = usuarioId, planId = planId),
                )

                val batch = db.batch()
                categorias.forEach { categoria ->
                    val docRef = db.collection("categorias").document()
                    val categoriaConId = categoria.copy(id = docRef.id)
                    batch.set(docRef, categoriaConId)
                }

                batch.commit().await()
                Log.d("Init", "Categorías por defecto creadas")
            } else {
                Log.d("Init", "Ya existían categorías por defecto para este plan")
            }

        } catch (e: Exception) {
            Log.e("Init", "Error al inicializar categorías", e)
            throw e
        }
    }
}