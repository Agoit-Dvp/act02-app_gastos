package com.example.controlgastos.ui.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.controlgastos.data.model.PlanFinanciero
import com.example.controlgastos.data.model.Usuario
import com.example.controlgastos.data.repository.PlanFinancieroRepository
import com.example.controlgastos.data.repository.UsuarioRepository
import com.google.firebase.auth.FirebaseAuth
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

    fun onRegisterClick() {
        val emailValue = email.value ?: ""
        val passwordValue = password.value ?: ""
        val nombreValue = nombre.value ?: ""

        if (emailValue.isBlank() || passwordValue.length < 6 || nombreValue.isBlank()) {
            _errorMessage.value = "Verifica que los datos sean válidos"
            return
        }

        auth.createUserWithEmailAndPassword(emailValue, passwordValue)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid ?: return@addOnSuccessListener
                val usuario = Usuario(
                    uid = uid,
                    nombre = nombreValue,
                    email = emailValue,
                    telefono = telefono.value ?: ""
                )

                usuarioRepository.guardarUsuario(usuario) { success, error ->
                    if (success) {
                        val nuevoPlan = PlanFinanciero(
                            nombre = "Mi primer plan",
                            descripcion = "Plan creado automáticamente",
                            creadorId = uid
                        )
                        planRepository.crearPlan(nuevoPlan) { creado, _ ->
                            if (creado) {
                                _registerSuccess.value = true
                            } else {
                                _errorMessage.value = "Error al crear el plan financiero"
                            }
                        }
                    } else {
                        _errorMessage.value = error
                    }
                }
            }
            .addOnFailureListener {
                _errorMessage.value = it.message
            }
    }
}