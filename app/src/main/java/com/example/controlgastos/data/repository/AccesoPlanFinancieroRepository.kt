package com.example.controlgastos.data.repository

import android.util.Log
import com.example.controlgastos.data.model.AccesoPlanFinanciero
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import java.util.Date


class AccesoPlanFinancieroRepository {

    private val db = FirebaseFirestore.getInstance()
    private val accesoCollection = db.collection("acceso_plan_financiero")
    private val usuarioCollection = db.collection("usuarios")

    fun guardarAcceso(acceso: AccesoPlanFinanciero, onComplete: (Boolean) -> Unit) {
        val docId = "${acceso.usuarioId}_${acceso.planId}" // 👈 estructura fija
        Log.d("Acceso", "Guardando acceso con ID: $docId")
        accesoCollection
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
    //Version suspendida guardarAcceso
    suspend fun guardarAccesoSuspendido(acceso: AccesoPlanFinanciero) {
        val docId = "${acceso.usuarioId}_${acceso.planId}"

        accesoCollection
            .document(docId)
            .set(acceso)
            .await()
    }


    fun actualizarEstado(
        usuarioId: String,
        planId: String,
        nuevoEstado: String,
        onComplete: (Boolean) -> Unit
    ) {
        val docId = "${usuarioId}_${planId}"
        Log.d("Firestore", "Intentando actualizar docId: $docId a estado: $nuevoEstado")
        accesoCollection
            .document(docId)
            .update("estado", nuevoEstado)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener {
                Log.e("Firestore", "Error al actualizar estado", it)
                onComplete(false)
            }
    }

    suspend fun obtenerPrimerPlanIdDeUsuario(uid: String): String? {
        val snapshot = accesoCollection
            .whereEqualTo("usuarioId", uid)
            .whereEqualTo("estado", "aceptado")
            .limit(1)
            .get()
            .await()

        return snapshot.documents.firstOrNull()?.getString("planId")
    }

    suspend fun obtenerAccesosDeUsuario(usuarioId: String): List<AccesoPlanFinanciero> {
        return try {
            val snapshot = accesoCollection
                .whereEqualTo("usuarioId", usuarioId)
                .whereEqualTo("estado", "aceptado")
                .get()
                .await()

            snapshot.documents.mapNotNull {
                it.toObject(AccesoPlanFinanciero::class.java)
            }
        } catch (e: Exception) {
            Log.e("Firestore", "Error al obtener accesos suspendido", e)
            emptyList()
        }
    }

    fun obtenerInvitacionesPendientes(
        usuarioId: String,
        onResult: (List<AccesoPlanFinanciero>) -> Unit
    ) {
        accesoCollection
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

    suspend fun eliminarAcceso(usuarioId: String, planId: String): Boolean {
        return try {
            val docId = "${usuarioId}_${planId}"
            accesoCollection
                .document(docId)
                .delete()
                .await()
            true
        } catch (e: Exception) {
            Log.e("Firestore", "Error al eliminar acceso", e)
            false
        }
    }

    //Invitar usuarios a planes
    suspend fun invitarUsuarioAPlan(usuarioId: String, planId: String): Boolean {
        val docId = "${usuarioId}_${planId}"
        val ref = accesoCollection
            .document(docId)

        val snapshot = ref.get().await()
        if (snapshot.exists()) return false

        val acceso = AccesoPlanFinanciero(
            planId = planId,
            usuarioId = usuarioId,
            rol = "lector",
            esPropietario = false,
            estado = "pendiente",
            fechaAcceso = Date()
        )
        ref.set(acceso).await()
        return true
    }

    //Buscar por correo
    suspend fun buscarUsuarioPorEmail(email: String): String? {
        val snapshot = usuarioCollection
            .whereEqualTo("email", email)
            .limit(1)
            .get()
            .await()

        return snapshot.documents.firstOrNull()?.getString("uid")
    }

    suspend fun obtenerAccesosPorPlan(planId: String): List<AccesoPlanFinanciero> {
        return try {
            val snapshot = accesoCollection
                .whereEqualTo("planId", planId)
                .whereEqualTo("estado", "aceptado")
                .get()
                .await()

            snapshot.documents.mapNotNull {
                it.toObject(AccesoPlanFinanciero::class.java)
            }
        } catch (e: Exception) {
            Log.e("Firestore", "Error al obtener accesos por plan (suspend)", e)
            emptyList()
        }
    }
}