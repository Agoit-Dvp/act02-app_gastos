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
}