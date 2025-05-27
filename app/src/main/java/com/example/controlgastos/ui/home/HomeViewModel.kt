package com.example.controlgastos.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.controlgastos.data.model.PlanFinanciero
import com.example.controlgastos.data.model.Usuario
import com.example.controlgastos.data.repository.AuthRepository
import com.example.controlgastos.data.repository.GastoRepository
import com.example.controlgastos.data.repository.IngresoRepository
import com.example.controlgastos.data.repository.PlanFinancieroRepository
import com.example.controlgastos.data.repository.UsuarioRepository
import com.google.firebase.auth.FirebaseAuth

class HomeViewModel(
    private val repository: UsuarioRepository = UsuarioRepository(),
    private val authRepository: AuthRepository = AuthRepository(),
    private val planRepository: PlanFinancieroRepository = PlanFinancieroRepository(),
    private val ingresoRepository: IngresoRepository = IngresoRepository(),
    private val gastoRepository: GastoRepository = GastoRepository()
) : ViewModel() {
    //usuario
    private val _usuario = MutableLiveData<Usuario?>()
    val usuario: LiveData<Usuario?> = _usuario
    //Mensaje de error
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    //Plan selecionado
    private val _planSeleccionado = MutableLiveData<PlanFinanciero?>()
    val planSeleccionado: LiveData<PlanFinanciero?> = _planSeleccionado

    //Estado para mostrar saldo
    private val _saldo = MutableLiveData<Double>()
    val saldo: LiveData<Double> = _saldo
    //Cargar usuario
    fun cargarUsuario() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        repository.obtenerUsuario(uid) { usuario, errorMsg ->
            if (errorMsg != null) {
                _error.value = errorMsg
            } else {
                _usuario.value = usuario
            }
        }
    }
    //Cargar plan seleccionado
    fun cargarPlanSeleccionado(planId: String) {
        planRepository.obtenerPlanPorId(planId) { plan, errorMsg ->
            if (errorMsg != null) {
                _error.value = errorMsg
            } else {
                _planSeleccionado.value = plan
                calcularSaldo(planId)
            }
        }
    }
    //Calcular saldo
    private fun calcularSaldo(planId: String) {
        ingresoRepository.obtenerTotalIngresos(planId) { totalIngresos ->
            gastoRepository.obtenerTotalGastos(planId) { totalGastos ->
                _saldo.postValue(totalIngresos - totalGastos)
            }
        }
    }
    //Funcion para volver a llamar a calcularSaldo
    fun actualizarSaldo(planId: String) {
        calcularSaldo(planId) // función privada ya existente
    }



    fun cerrarSesion() {
        authRepository.logout()
    }
}
