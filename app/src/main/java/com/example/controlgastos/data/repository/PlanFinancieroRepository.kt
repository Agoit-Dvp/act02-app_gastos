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
    /**
     * Crea un nuevo plan financiero y registra automáticamente el acceso del creador.
     *
     * @param plan El plan a crear (sin ID asignado).
     * @param onComplete Callback que devuelve:
     *   - [Boolean]: `true` si el plan y el acceso se crearon correctamente.
     *   - [String?]: El ID del plan creado (null si falló).
     *
     * Nota: Aunque actualmente algunos consumidores solo usan el valor booleano,
     * mantener el planId permite mayor flexibilidad para flujos futuros (como
     * navegación directa a Home con el plan recién creado).
     */
    fun crearPlan(
        plan: PlanFinanciero,
        onComplete: (Boolean, String?) -> Unit
    ) {
        val ref = db.collection(coleccionPlanes).document()
        val planConId = plan.copy(id = ref.id)
        Log.d("PlanRepo", "Creando plan con creadorId=${planConId.creadorId}")

        ref.set(planConId)
            .addOnSuccessListener {
                val acceso = AccesoPlanFinanciero(

                    planId = planConId.id,
                    usuarioId = planConId.creadorId,
                    rol = "administrador",
                    esPropietario = true,
                    estado = "aceptado"
                )
                Log.d(
                    "Planes",
                    "Creando acceso para planId=${planConId.id}, usuarioId=${planConId.creadorId}"
                )
                accesoRepo.guardarAcceso(acceso) { accesoOk ->
                    if (accesoOk) {
                        onComplete(true, planConId.id) // ✅ Devuelve también el ID
                    } else {
                        onComplete(false, null)
                    }
                }
            }
            .addOnFailureListener {
                Log.e("Firestore", "Error al crear plan", it)
                onComplete(false, null)
            }
    }

    //Crear Plan suspendida
    suspend fun crearPlanSuspendido(plan: PlanFinanciero): String {
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

    //Obtener planes por usuario suspend
    suspend fun obtenerPlanesDeUsuarioSuspend(usuarioId: String): List<PlanFinanciero> {
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
