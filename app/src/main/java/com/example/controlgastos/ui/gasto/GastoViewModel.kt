package com.example.controlgastos.ui.gasto

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.controlgastos.data.model.Gasto
import com.example.controlgastos.data.repository.GastoRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class GastosViewModel(
    private val repository: GastoRepository = GastoRepository()
) : ViewModel() {

    private val _gastos = MutableLiveData<List<Gasto>>()
    val gastos: LiveData<List<Gasto>> = _gastos

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    // NUEVO: Cargar gastos filtrando por usuario y plan
    fun cargarGastosDePlanByUser(planId: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        repository.getGastosByUserAndPlan(userId, planId) { lista, errorMsg ->
            if (errorMsg != null) {
                _error.value = errorMsg
            } else {
                _gastos.value = lista ?: emptyList()
            }
        }
    }

    fun cargarGastosDePlan(planId: String) {
        viewModelScope.launch {
            try {
                val lista = repository.getGastosByPlan(planId)
                _gastos.value = lista
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }


}