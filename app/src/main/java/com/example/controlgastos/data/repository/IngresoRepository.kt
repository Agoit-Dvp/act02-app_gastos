package com.example.controlgastos.data.repository

import android.util.Log
import com.example.controlgastos.data.model.Ingreso
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.*

class IngresoRepository {
    private val db = FirebaseFirestore.getInstance()
    private val ingresosCollection = db.collection("ingresos")

    fun addIngreso(ingreso: Ingreso, onResult: (Boolean, String?) -> Unit) {
        val id = UUID.randomUUID().toString()
        val ingresoWithId = ingreso.copy(id = id)
        ingresosCollection.document(id).set(ingresoWithId)
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { onResult(false, it.message) }
    }

    fun getIngresosByUser(userId: String, onResult: (List<Ingreso>?, String?) -> Unit) {
        ingresosCollection.whereEqualTo("usuarioId", userId)
            .get()
            .addOnSuccessListener { result ->
                val ingresos = result.toObjects(Ingreso::class.java)
                onResult(ingresos, null)
            }
            .addOnFailureListener {
                onResult(null, it.message)
            }
    }

    //Funcion para obtener ingresos por usuario y plan
    fun getIngresosByUserAndPlan(
        userId: String,
        planId: String,
        onResult: (List<Ingreso>?, String?) -> Unit
    ) {
        ingresosCollection
            .whereEqualTo("usuarioId", userId)
            .whereEqualTo("planId", planId) // 👈 nuevo filtro
            .get()
            .addOnSuccessListener { result ->
                val ingresos = result.toObjects(Ingreso::class.java)
                onResult(ingresos, null)
            }
            .addOnFailureListener {
                onResult(null, it.message)
            }
    }

    suspend fun getIngresosByPlan(planId: String): List<Ingreso> {
        return try {
            val snapshot = ingresosCollection
                .whereEqualTo("planId", planId)
                .get()
                .await()
            snapshot.toObjects(Ingreso::class.java)
        } catch (e: Exception) {
            emptyList() // o lanza la excepción según lo que prefieras
        }
    }

    fun getIngresoById(ingresoId: String, onResult: (Ingreso?, String?) -> Unit) {
        ingresosCollection.document(ingresoId)
            .get()
            .addOnSuccessListener { doc ->
                val ingreso = doc.toObject(Ingreso::class.java)
                onResult(ingreso, null)
            }
            .addOnFailureListener { e -> onResult(null, e.message) }
    }

    fun deleteIngreso(id: String, onResult: (Boolean, String?) -> Unit) {
        ingresosCollection.document(id)
            .delete()
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { onResult(false, it.message) }
    }

    fun updateIngreso(ingreso: Ingreso, onResult: (Boolean, String?) -> Unit) {
        if (ingreso.id.isBlank()) {
            onResult(false, "ID vacío")
            return
        }
        ingresosCollection.document(ingreso.id)
            .set(ingreso)
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { onResult(false, it.message) }
    }

    //Eliminar por planId desde PlanesViewModel
    suspend fun eliminarIngresosPorPlan(planId: String): Boolean {
        return try {
            val snapshot = ingresosCollection
                .whereEqualTo("planId", planId)
                .get()
                .await()

            snapshot.documents.forEach { it.reference.delete() }
            true
        } catch (e: Exception) {
            Log.e("IngresoRepo", "Error al eliminar ingresos del plan", e)
            false
        }
    }

    //Obtener total ingresos por plan
    suspend fun obtenerTotalIngresos(planId: String): Double {
        return try {
            val snapshot = ingresosCollection
                .whereEqualTo("planId", planId)
                .get()
                .await()

            snapshot.sumOf { it.getDouble("valor") ?: 0.0 }
        } catch (e: Exception) {
            Log.e("Repo", "Error al obtener ingresos", e)
            0.0
        }
    }

}