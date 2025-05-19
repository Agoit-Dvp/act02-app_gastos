package com.example.controlgastos.data.initializer

import android.util.Log
import com.example.controlgastos.data.model.Categoria
import com.google.firebase.firestore.FirebaseFirestore

class FirestoreInitializer(private val usuarioId: String) {

    private val db = FirebaseFirestore.getInstance()

    fun inicializarCategoriasPorDefecto(planId: String, onComplete: () -> Unit) {
        val ref = db.collection("categorias")
            .whereEqualTo("usuarioId", usuarioId)
            .whereEqualTo("planId", planId)

        ref.get().addOnSuccessListener { snapshot ->
            if (snapshot.isEmpty) {
                val categorias = listOf(
                    Categoria(nombre = "Salario", esIngreso = true, iconName = "💼", usuarioId = usuarioId, planId = planId),
                    Categoria(nombre = "Otros ingresos", esIngreso = true, iconName = "💰", usuarioId = usuarioId, planId = planId),
                    Categoria(nombre = "Comida", esIngreso = false, iconName = "🍔", usuarioId = usuarioId, planId = planId),
                    Categoria(nombre = "Transporte", esIngreso = false, iconName = "🚗", usuarioId = usuarioId, planId = planId),
                    Categoria(nombre = "Entretenimiento", esIngreso = false, iconName = "🎮", usuarioId = usuarioId, planId = planId),
                    Categoria(nombre = "Salud", esIngreso = false, iconName = "🩺", usuarioId = usuarioId, planId = planId)
                )

                val batch = db.batch()
                categorias.forEach {
                    val docRef = db.collection("categorias").document()
                    batch.set(docRef, it)
                }
                batch.commit().addOnSuccessListener {
                    Log.d("Init", "Categorías por defecto creadas")
                    onComplete()
                }.addOnFailureListener {
                    Log.e("Init", "Error al crear categorías", it)
                    onComplete()
                }
            } else {
                onComplete()
            }
        }.addOnFailureListener {
            Log.e("Init", "Error al consultar categorías", it)
            onComplete()
        }
    }
}