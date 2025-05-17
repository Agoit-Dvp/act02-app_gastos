package com.example.controlgastos.data.repository

import android.util.Log
import com.example.controlgastos.data.model.AccesoPlanFinanciero
import com.google.firebase.firestore.FirebaseFirestore


class AccesoPlanFinancieroRepository {

    private val db = FirebaseFirestore.getInstance()
    private val coleccion = "acceso_plan_financiero"

    fun guardarAcceso(acceso: AccesoPlanFinanciero, onComplete: (Boolean) -> Unit) {
        val docId = "${acceso.usuarioId}_${acceso.planId}"
        db.collection(coleccion)
            .document(docId)
            .set(acceso)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener {
                Log.e("Firestore", "Error al guardar acceso", it)
                onComplete(false)
            }
    }

    fun actualizarEstado(
        usuarioId: String,
        planId: String,
        nuevoEstado: String,
        onComplete: (Boolean) -> Unit
    ) {
        val docId = "${usuarioId}_${planId}"
        db.collection(coleccion)
            .document(docId)
            .update("estado", nuevoEstado)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener {
                Log.e("Firestore", "Error al actualizar estado", it)
                onComplete(false)
            }
    }

    fun obtenerAccesosDeUsuario(
        usuarioId: String,
        soloAceptados: Boolean = true,
        onResult: (List<AccesoPlanFinanciero>) -> Unit
    ) {
        var query = db.collection(coleccion)
            .whereEqualTo("usuarioId", usuarioId)

        if (soloAceptados) {
            query = query.whereEqualTo("estado", "aceptado")
        }

        query.get()
            .addOnSuccessListener { result ->
                val accesos = result.documents.mapNotNull {
                    it.toObject(AccesoPlanFinanciero::class.java)
                }
                onResult(accesos)
            }
            .addOnFailureListener {
                Log.e("Firestore", "Error al obtener accesos", it)
                onResult(emptyList())
            }
    }

    fun obtenerInvitacionesPendientes(
        usuarioId: String,
        onResult: (List<AccesoPlanFinanciero>) -> Unit
    ) {
        db.collection(coleccion)
            .whereEqualTo("usuarioId", usuarioId)
            .whereEqualTo("estado", "pendiente")
            .get()
            .addOnSuccessListener { result ->
                val invitaciones = result.documents.mapNotNull {
                    it.toObject(AccesoPlanFinanciero::class.java)
                }
                onResult(invitaciones)
            }
            .addOnFailureListener {
                Log.e("Firestore", "Error al obtener invitaciones", it)
                onResult(emptyList())
            }
    }

    fun eliminarAcceso(usuarioId: String, planId: String, onComplete: (Boolean) -> Unit) {
        val docId = "${usuarioId}_${planId}"
        db.collection(coleccion)
            .document(docId)
            .delete()
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener {
                Log.e("Firestore", "Error al eliminar acceso", it)
                onComplete(false)
            }
    }
}