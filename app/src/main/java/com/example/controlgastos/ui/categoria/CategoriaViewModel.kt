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

    fun cargarCategorias(esIngreso: Boolean) {
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
    }

    fun agregarCategoria(categoria: Categoria, onFinish: (Boolean, String?) -> Unit = { _, _ -> }) {
        repository.agregarCategoria(categoria) { success, errorMsg ->
            if (success) {
                cargarCategorias(categoria.esIngreso)
            }
            onFinish(success, errorMsg)
        }
    }

    fun eliminarCategoria(id: String, esIngreso: Boolean, onFinish: (Boolean, String?) -> Unit = { _, _ -> }) {
        repository.eliminarCategoria(id) { success, errorMsg ->
            if (success) {
                cargarCategorias(esIngreso)
            }
            onFinish(success, errorMsg)
        }
    }

    fun actualizarCategoria(categoria: Categoria, onFinish: (Boolean, String?) -> Unit = { _, _ -> }) {
        repository.actualizarCategoria(categoria) { success, errorMsg ->
            if (success) {
                cargarCategorias(categoria.esIngreso)
            }
            onFinish(success, errorMsg)
        }
    }
}