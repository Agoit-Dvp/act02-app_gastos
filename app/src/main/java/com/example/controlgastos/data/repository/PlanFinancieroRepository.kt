package com.example.controlgastos.data.repository

import android.util.Log
import com.example.controlgastos.data.model.AccesoPlanFinanciero
import com.example.controlgastos.data.model.PlanFinanciero
import com.google.firebase.firestore.FirebaseFirestore

class PlanFinancieroRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    private val coleccionPlanes = "planes_financieros"
    private val accesoRepo = AccesoPlanFinancieroRepository()

    // 1. Crear plan + acceso propietario
    fun crearPlan(
        plan: PlanFinanciero,
        onComplete: (Boolean) -> Unit
    ) {
        val ref = db.collection(coleccionPlanes).document()
        val planConId = plan.copy(id = ref.id)

        ref.set(planConId)
            .addOnSuccessListener {
                val acceso = AccesoPlanFinanciero(

                    planId = planConId.id,
                    usuarioId = planConId.creadorId,
                    rol = "administrador",
                    esPropietario = true,
                    estado = "aceptado"
                )
                Log.d("Planes", "Creando acceso para planId=${planConId.id}, usuarioId=${planConId.creadorId}")
                accesoRepo.guardarAcceso(acceso, onComplete)
            }
            .addOnFailureListener {
                Log.e("Firestore", "Error al crear plan", it)
                onComplete(false)
            }
    }

    // 2. Obtener planes completos a los que tiene acceso el usuario
    fun obtenerPlanesDeUsuario(
        usuarioId: String,
        onResult: (List<PlanFinanciero>) -> Unit
    ) {
        accesoRepo.obtenerAccesosDeUsuario(usuarioId) { accesos ->
            if (accesos.isEmpty()) {
                onResult(emptyList())
                return@obtenerAccesosDeUsuario
            }

            val planIds = accesos.map { it.planId }
            db.collection(coleccionPlanes)
                .whereIn("id", planIds)
                .get()
                .addOnSuccessListener { snapshot ->
                    val planes = snapshot.documents.mapNotNull {
                        it.toObject(PlanFinanciero::class.java)
                    }
                    onResult(planes)
                }
                .addOnFailureListener {
                    Log.e("Firestore", "Error al obtener planes", it)
                    onResult(emptyList())
                }
        }
    }
    //Obtener planes por id
    fun obtenerPlanesPorIds(
        ids: List<String>,
        onResult: (List<PlanFinanciero>) -> Unit
    ) {
        if (ids.isEmpty()) {
            onResult(emptyList())
            return
        }

        db.collection(coleccionPlanes)
            .whereIn("id", ids)
            .get()
            .addOnSuccessListener { result ->
                val planes = result.documents.mapNotNull {
                    it.toObject(PlanFinanciero::class.java)
                }
                onResult(planes)
            }
            .addOnFailureListener {
                Log.e("Firestore", "Error al obtener planes por IDs", it)
                onResult(emptyList())
            }
    }
}
