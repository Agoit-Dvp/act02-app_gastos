package com.example.controlgastos.data.repository

import android.util.Log
import com.example.controlgastos.data.model.AccesoPlanFinanciero
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.util.Date


class AccesoPlanFinancieroRepository {

    private val db = FirebaseFirestore.getInstance()
    private val coleccion = "acceso_plan_financiero"

    fun guardarAcceso(acceso: AccesoPlanFinanciero, onComplete: (Boolean) -> Unit) {
        val docId = "${acceso.usuarioId}_${acceso.planId}" // 👈 estructura fija
        Log.d("Acceso", "Guardando acceso con ID: $docId")
        db.collection(coleccion)
            .document(docId) // 👈 usamos ID personalizado
            .set(acceso, SetOptions.merge())
            .addOnSuccessListener {
                Log.d("Acceso", "Acceso guardado correctamente")
                onComplete(true)
            }
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
        Log.d("Firestore", "Intentando actualizar docId: $docId a estado: $nuevoEstado")
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

    //Invitar usuarios a planes
    fun invitarUsuarioAPlan(
        usuarioId: String,
        planId: String,
        rol: String = "lector",
        onComplete: (Boolean) -> Unit
    ) {
        val docId = "${usuarioId}_${planId}"
        val acceso = AccesoPlanFinanciero(
            planId = planId,
            usuarioId = usuarioId,
            rol = rol,
            esPropietario = false,
            estado = "pendiente",
            fechaAcceso = Date()
        )

        db.collection(coleccion)
            .document(docId)
            .get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    Log.d("Firestore", "El usuario ya fue invitado a este plan.")
                    onComplete(false) // Ya existe, no sobrescribimos
                } else {
                    db.collection(coleccion)
                        .document(docId)
                        .set(acceso)
                        .addOnSuccessListener {
                            Log.d("Firestore", "Invitación enviada a $usuarioId para plan $planId")
                            onComplete(true)
                        }
                        .addOnFailureListener {
                            Log.e("Firestore", "Error al invitar usuario", it)
                            onComplete(false)
                        }
                }
            }
            .addOnFailureListener {
                Log.e("Firestore", "Error al verificar invitación existente", it)
                onComplete(false)
            }
    }

    //Buscar por correo
    fun buscarUsuarioPorEmail(
        email: String,
        onResult: (String?) -> Unit
    ) {
        FirebaseFirestore.getInstance().collection("usuarios")
            .whereEqualTo("email", email)
            .limit(1)
            .get()
            .addOnSuccessListener { result ->
                val usuario = result.documents.firstOrNull()
                val uid = usuario?.getString("uid")
                onResult(uid)
            }
            .addOnFailureListener {
                Log.e("Firestore", "Error al buscar usuario por email", it)
                onResult(null)
            }
    }
}