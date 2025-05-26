package com.example.controlgastos.data.repository

import android.util.Log
import com.example.controlgastos.data.model.Gasto
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.*

class GastoRepository {
    //Crear conexión con la base de datos
    private val db = FirebaseFirestore.getInstance()
    //Indicar la colección de firestore donde se realizara: Add, Get, Update, Delete
    private val gastosCollection = db.collection("gastos")

    fun addGasto(gasto: Gasto, onResult: (Boolean, String?) -> Unit) {
        val id = UUID.randomUUID().toString()
        val gastoWithId = gasto.copy(id = id)
        gastosCollection.document(id).set(gastoWithId)
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { onResult(false, it.message) }
    }

    fun getGastosByUserAndPlan(
        userId: String,
        planId: String,
        onResult: (List<Gasto>?, String?) -> Unit
    ) {
        gastosCollection
            .whereEqualTo("usuarioId", userId)
            .whereEqualTo("planId", planId) // 👈 nuevo filtro
            .get()
            .addOnSuccessListener { result ->
                val gastos = result.toObjects(Gasto::class.java)
                onResult(gastos, null)
            }
            .addOnFailureListener {
                onResult(null, it.message)
            }
    }

    fun getGastoById(gastoId: String, onResult: (Gasto?, String?) -> Unit) {
        gastosCollection.document(gastoId)
            .get()
            .addOnSuccessListener { doc ->
                val gasto = doc.toObject(Gasto::class.java)
                onResult(gasto, null)
            }
            .addOnFailureListener { e -> onResult(null, e.message) }
    }

    fun deleteGasto(gastoId: String, onResult: (Boolean, String?) -> Unit) {
        gastosCollection.document(gastoId)
            .delete()
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { e -> onResult(false, e.message) }
    }

    fun updateGasto(gasto: Gasto, onResult: (Boolean, String?) -> Unit) {
        if (gasto.id.isBlank()) {
            onResult(false, "ID del gasto vacío")
            return
        }

        gastosCollection.document(gasto.id).set(gasto)
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { e -> onResult(false, e.message) }
    }

    //Eliminar por Plan
    suspend fun eliminarGastosPorPlan(planId: String): Boolean {
        return try {
            val snapshot = gastosCollection
                .whereEqualTo("planId", planId)
                .get().await()

            snapshot.documents.forEach { it.reference.delete() }

            true
        } catch (e: Exception) {
            Log.e("GastosRepo", "Error al eliminar gastos", e)
            false
        }
    }
}