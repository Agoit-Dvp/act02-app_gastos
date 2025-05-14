package com.example.controlgastos.ui.gasto

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.controlgastos.data.model.Gasto
import com.example.controlgastos.data.repository.GastoRepository
import com.google.firebase.auth.FirebaseAuth

class GastosViewModel(
    private val repository: GastoRepository = GastoRepository()
) : ViewModel() {

    private val _gastos = MutableLiveData<List<Gasto>>()
    val gastos: LiveData<List<Gasto>> = _gastos

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun cargarGastosUsuario() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        repository.getGastosByUser(userId) { lista, errorMsg ->
            if (errorMsg != null) {
                _error.value = errorMsg
            } else {
                _gastos.value = lista ?: emptyList()
            }
        }
    }
}