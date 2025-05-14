package com.example.controlgastos.data.repository

import com.example.controlgastos.data.model.Gasto
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class GastoRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val gastosCollection = firestore.collection("gastos")

    fun addGasto(gasto: Gasto, onResult: (Boolean, String?) -> Unit) {
        val id = UUID.randomUUID().toString()
        val gastoWithId = gasto.copy(id = id)
        gastosCollection.document(id).set(gastoWithId)
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { onResult(false, it.message) }
    }

    fun getGastosByUser(userId: String, onResult: (List<Gasto>?, String?) -> Unit) {
        gastosCollection.whereEqualTo("usuarioId", userId)
            .get()
            .addOnSuccessListener { result ->
                val gastos = result.toObjects(Gasto::class.java)
                onResult(gastos, null)
            }
            .addOnFailureListener {
                onResult(null, it.message)
            }
    }
}