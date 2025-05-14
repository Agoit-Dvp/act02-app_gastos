package com.example.controlgastos.ui.ingreso

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.controlgastos.data.model.Ingreso
import com.example.controlgastos.data.repository.IngresoRepository
import com.google.firebase.auth.FirebaseAuth

class IngresosViewModel(
    private val repository: IngresoRepository = IngresoRepository()
) : ViewModel() {

    private val _ingresos = MutableLiveData<List<Ingreso>>()
    val ingresos: LiveData<List<Ingreso>> = _ingresos

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun cargarIngresosUsuario() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        repository.getIngresosByUser(userId) { lista, errorMsg ->
            if (errorMsg != null) {
                _error.value = errorMsg
            } else {
                _ingresos.value = lista ?: emptyList()
            }
        }
    }
}