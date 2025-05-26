package com.example.controlgastos.ui.categoria


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.controlgastos.data.model.Categoria
import com.example.controlgastos.data.repository.CategoriaRepository

class CategoriaViewModel(
    private val repository: CategoriaRepository = CategoriaRepository()
) : ViewModel() {

    private val _categorias = MutableLiveData<List<Categoria>>()
    val categorias: LiveData<List<Categoria>> = _categorias

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

/*    fun cargarCategorias(esIngreso: Boolean) { //eliminar si funciona el nuevo
        _isLoading.value = true
        repository.obtenerCategorias(esIngreso) { lista, errorMsg ->
            _isLoading.value = false
            if (errorMsg != null) {
                _error.value = errorMsg
            } else {
                _categorias.value = lista ?: emptyList()
                _error.value = null
            }
        }
    }*/

    fun cargarCategorias(planId: String, esIngreso: Boolean) {
        _isLoading.value = true
        repository.obtenerCategoriasDePlan(planId, esIngreso) { lista ->
            _isLoading.value = false
            _categorias.value = lista
            _error.value = null
        }
    }

    fun existeCategoriaConNombre(nombre: String, esIngreso: Boolean): Boolean {
        return _categorias.value
            ?.any { it.nombre.equals(nombre.trim(), ignoreCase = true) && it.esIngreso == esIngreso }
            ?: false
    }
//Funciones sin uso
   fun agregarCategoria(categoria: Categoria, onFinish: (Boolean, String?) -> Unit = { _, _ -> }) {
        repository.agregarCategoria(categoria) { success, errorMsg ->
            if (success) {
                cargarCategorias(categoria.planId, categoria.esIngreso)
            }
            onFinish(success, errorMsg)
        }
    }

    fun eliminarCategoria(
        id: String,
        planId: String,
        esIngreso: Boolean,
        onFinish: (Boolean, String?) -> Unit = { _, _ -> }
    ) {
        repository.eliminarCategoria(id) { success, errorMsg ->
            if (success) {
                cargarCategorias(planId, esIngreso)
            }
            onFinish(success, errorMsg)
        }
    }


    fun actualizarCategoria(categoria: Categoria, onFinish: (Boolean, String?) -> Unit = { _, _ -> }) {
        repository.actualizarCategoria(categoria) { success, errorMsg ->
            if (success) {
                cargarCategorias(categoria.planId, categoria.esIngreso)
            }
            onFinish(success, errorMsg)
        }
    }
}