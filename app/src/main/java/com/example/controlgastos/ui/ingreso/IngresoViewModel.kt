package com.example.controlgastos.ui.ingreso

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.controlgastos.data.model.Ingreso
import com.example.controlgastos.data.repository.IngresoRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class IngresosViewModel(
    private val repository: IngresoRepository = IngresoRepository()
) : ViewModel() {

    private val _ingresos = MutableLiveData<List<Ingreso>>()
    val ingresos: LiveData<List<Ingreso>> = _ingresos

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    // cargar ingresos filtrando por usuario y plan
    fun cargarIngresosDePlanByUser(planId: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        repository.getIngresosByUserAndPlan(userId, planId) { lista, errorMsg ->
            if (errorMsg != null) {
                _error.value = errorMsg
            } else {
                _ingresos.value = lista ?: emptyList()
            }
        }
    }
    //Cargar ingresos por plan
    fun cargarIngresosDePlan(planId: String) {
        viewModelScope.launch {
            try {
                val lista = repository.getIngresosByPlan(planId)
                _ingresos.value = lista
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }


}