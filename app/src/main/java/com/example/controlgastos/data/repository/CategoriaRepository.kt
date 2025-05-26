package com.example.controlgastos.data.repository

import android.util.Log
import com.example.controlgastos.data.model.Categoria
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.*

class CategoriaRepository {

    private val db = FirebaseFirestore.getInstance()
    private val categoriaCollection = db.collection("categorias")

    fun agregarCategoria(categoria: Categoria, onResult: (Boolean, String?) -> Unit) {
        val id = UUID.randomUUID().toString()
        val categoriaConId = categoria.copy(id = id)

        categoriaCollection.document(id)
            .set(categoriaConId)
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { e -> onResult(false, e.message) }
    }

    fun obtenerCategorias(esIngreso: Boolean, onResult: (List<Categoria>?, String?) -> Unit) {
        categoriaCollection
            .whereEqualTo("esIngreso", esIngreso)
            .get()
            .addOnSuccessListener { result ->
                val categorias = result.toObjects(Categoria::class.java)
                onResult(categorias, null)
            }
            .addOnFailureListener { e -> onResult(null, e.message) }
    }

    fun obtenerCategoriaPorId(id: String, onResult: (Categoria?, String?) -> Unit) {
        categoriaCollection.document(id)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val categoria = document.toObject(Categoria::class.java)
                    onResult(categoria, null)
                } else {
                    onResult(null, "Categoría no encontrada")
                }
            }
            .addOnFailureListener { e ->
                onResult(null, e.message)
            }
    }

    fun obtenerCategoriasDePlan(
        planId: String,
        esIngreso: Boolean = false,
        onResult: (List<Categoria>) -> Unit
    ) {
        categoriaCollection
            .whereEqualTo("planId", planId)
            .whereEqualTo("esIngreso", esIngreso)
            .get()
            .addOnSuccessListener { result ->
                val categorias = result.toObjects(Categoria::class.java)
                onResult(categorias)
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }

    fun actualizarCategoria(categoria: Categoria, onResult: (Boolean, String?) -> Unit) {
        categoriaCollection.document(categoria.id)
            .set(categoria)
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { onResult(false, it.message) }
    }

    fun eliminarCategoria(id: String, onResult: (Boolean, String?) -> Unit) {
        categoriaCollection.document(id)
            .delete()
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { onResult(false, it.message) }
    }
    //Eliminar por planID desde PlanesViewModel
    suspend fun eliminarCategoriasPorPlan(planId: String): Boolean {
        return try {
            val snapshot = categoriaCollection
                .whereEqualTo("planId", planId)
                .get()
                .await()

            snapshot.documents.forEach { it.reference.delete() }
            true
        } catch (e: Exception) {
            Log.e("CategoriaRepo", "Error al eliminar categorías del plan", e)
            false
        }
    }

}