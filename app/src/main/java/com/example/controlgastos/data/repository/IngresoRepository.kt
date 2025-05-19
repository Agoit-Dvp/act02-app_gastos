package com.example.controlgastos.data.repository

import com.example.controlgastos.data.model.Ingreso
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class IngresoRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val ingresosCollection = firestore.collection("ingresos")

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

    fun getCategoriasIngreso(onResult: (List<String>) -> Unit) {
        val categoriasRef = FirebaseFirestore.getInstance().collection("categoriasIngreso")

        categoriasRef.get()
            .addOnSuccessListener { result ->
                val categorias = result.mapNotNull { it.getString("nombre") }
                onResult(categorias)
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }

}