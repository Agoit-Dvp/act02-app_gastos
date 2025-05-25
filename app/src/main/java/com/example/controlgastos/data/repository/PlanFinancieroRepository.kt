package com.example.controlgastos.data.repository

import android.util.Log
import com.example.controlgastos.data.model.AccesoPlanFinanciero
import com.example.controlgastos.data.model.PlanFinanciero
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class PlanFinancieroRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    private val coleccionPlanes = "planes_financieros"
    private val accesoRepo = AccesoPlanFinancieroRepository()

    // 1. Crear plan + acceso propietario
    //Crear Plan suspendida
    suspend fun crearPlan(plan: PlanFinanciero): String {
        val ref = db.collection(coleccionPlanes).document()
        val planConId = plan.copy(id = ref.id)

        try {
            // Guardar el plan
            ref.set(planConId).await()

            // Crear acceso como propietario
            val acceso = AccesoPlanFinanciero(
                planId = planConId.id,
                usuarioId = planConId.creadorId,
                rol = "administrador",
                esPropietario = true,
                estado = "aceptado"
            )

            accesoRepo.guardarAccesoSuspendido(acceso) // 🔁 Requiere que la conviertas también

            return planConId.id

        } catch (e: Exception) {
            Log.e("Firestore", "Error en crearPlanSuspendido", e)
            throw e
        }
    }

    // 2. Obtener planes completos a los que tiene acceso el usuario
    //Obtener planes por usuario suspend
    suspend fun obtenerPlanesDeUsuario(usuarioId: String): List<PlanFinanciero> {
        return try {
            val accesosSnapshot = FirebaseFirestore.getInstance()
                .collection("acceso_plan_financiero")
                .whereEqualTo("usuarioId", usuarioId)
                .whereEqualTo("estado", "aceptado")
                .get()
                .await()

            val accesos = accesosSnapshot.documents.mapNotNull {
                it.toObject(AccesoPlanFinanciero::class.java)
            }

            val planIds = accesos.map { it.planId }
            Log.d("PlanesRepo", "Accesos encontrados: ${accesos.size}")
            Log.d("PlanesRepo", "IDs de planes: $planIds")

            if (planIds.isEmpty()) return emptyList()

            val planesSnapshot = FirebaseFirestore.getInstance()
                .collection("planes_financieros")
                .whereIn("id", planIds)
                .get()
                .await()

            return planesSnapshot.documents.mapNotNull {
                it.toObject(PlanFinanciero::class.java)
            }

        } catch (e: Exception) {
            Log.e("Firestore", "Error al obtener planes suspendidos", e)
            emptyList()
        }
    }

    //Obtener planes por id
    suspend fun obtenerPlanesPorIds(planIds: List<String>): List<PlanFinanciero> {
        if (planIds.isEmpty()) return emptyList()

        val snapshot = FirebaseFirestore.getInstance()
            .collection("planes_financieros")
            .whereIn("id", planIds)
            .get()
            .await()

        return snapshot.documents.mapNotNull {
            it.toObject(PlanFinanciero::class.java)
        }
    }

    //Obtener un solo plan por id
    fun obtenerPlanPorId(
        planId: String,
        onResult: (PlanFinanciero?, String?) -> Unit
    ) {
        if (planId.isBlank()) {
            onResult(null, "ID del plan vacío")
            return
        }

        db.collection(coleccionPlanes)
            .document(planId)
            .get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val plan = doc.toObject(PlanFinanciero::class.java)?.copy(id = doc.id)
                    onResult(plan, null)
                } else {
                    onResult(null, "El plan no existe")
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error al obtener plan por ID", e)
                onResult(null, e.message ?: "Error desconocido")
            }
    }


    //Actualizar Plan desde PlanListadoSreen
    fun actualizarPlan(plan: PlanFinanciero, onResult: (Boolean) -> Unit) {
        if (plan.id.isBlank()) {
            onResult(false)
            return
        }

        FirebaseFirestore.getInstance()
            .collection("planes_financieros")
            .document(plan.id)
            .set(plan)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error al actualizar plan", e)
                onResult(false)
            }
    }

}
