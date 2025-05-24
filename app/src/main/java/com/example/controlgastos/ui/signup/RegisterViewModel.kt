package com.example.controlgastos.ui.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.controlgastos.data.initializer.FirestoreInitializer
import com.example.controlgastos.data.model.PlanFinanciero
import com.example.controlgastos.data.model.Usuario
import com.example.controlgastos.data.repository.PlanFinancieroRepository
import com.example.controlgastos.data.repository.UsuarioRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*

class RegisterViewModel(
    private val usuarioRepository: UsuarioRepository = UsuarioRepository(),
    private val planRepository: PlanFinancieroRepository = PlanFinancieroRepository()
) : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    val nombre = MutableLiveData("")
    val email = MutableLiveData("")
    val password = MutableLiveData("")
    val telefono = MutableLiveData("")

    private val _registerSuccess = MutableLiveData(false)
    val registerSuccess: LiveData<Boolean> = _registerSuccess

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    //Version suspendida
    fun onRegisterClick() {
        val emailValue = email.value ?: ""
        val passwordValue = password.value ?: ""
        val nombreValue = nombre.value ?: ""

        if (emailValue.isBlank() || passwordValue.length < 6 || nombreValue.isBlank()) {
            _errorMessage.value = "Verifica que los datos sean válidos"
            return
        }

        viewModelScope.launch {
            try {
                // Crear usuario en Firebase Auth
                val result = auth.createUserWithEmailAndPassword(emailValue, passwordValue).await()
                val uid = result.user?.uid ?: throw Exception("Error al obtener UID")

                // Guardar usuario en Firestore
                val usuario = Usuario(
                    uid = uid,
                    nombre = nombreValue,
                    email = emailValue,
                    telefono = telefono.value ?: ""
                )
                usuarioRepository.guardarUsuarioSuspendido(usuario)

                // Crear plan financiero inicial
                val nuevoPlan = PlanFinanciero(
                    nombre = "Mi primer plan",
                    descripcion = "Plan creado automáticamente",
                    creadorId = uid
                )
                val planId = planRepository.crearPlanSuspendido(nuevoPlan)

                // Inicializar categorías por defecto
                FirestoreInitializer(uid).inicializarCategoriasSuspend(planId)

                // Listo ✅
                _registerSuccess.value = true

            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error desconocido durante el registro"
            }
        }
    }
}