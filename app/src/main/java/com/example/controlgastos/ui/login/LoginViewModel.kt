package com.example.controlgastos.ui.login

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.controlgastos.data.repository.AccesoPlanFinancieroRepository
import com.example.controlgastos.data.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class LoginViewModel(private val authRepository: AuthRepository = AuthRepository()) : ViewModel() {

    private val accesoRepo = AccesoPlanFinancieroRepository()

    private val _email = MutableLiveData<String>() //Estado privado
    val email: LiveData<String> = _email // Modificar el estado privado solo desde el loginViewModel

    private val _password = MutableLiveData<String>()
    val password: LiveData<String> = _password

    private val _loginEnable = MutableLiveData<Boolean>()
    val loginEnable: LiveData<Boolean> = _loginEnable

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    private val _planId = MutableStateFlow<String?>(null)
    val planId: StateFlow<String?> = _planId.asStateFlow()

    fun onLoginChanged(email: String, password: String) {
        _email.value = email
        _password.value = password
        _loginEnable.value = isValidEmail(email) && isValidPassword(password)
    }

    private fun isValidEmail(email: String): Boolean =
        Patterns.EMAIL_ADDRESS.matcher(email).matches() //Confirmar si es un email valido

    private fun isValidPassword(password: String): Boolean =
        password.length > 5 //Prueba sencilla para probar componente

    fun onLoginSelected() {
        val email = _email.value ?: return
        val password = _password.value ?: return

        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try {
                authRepository.loginUser(email, password) // suspend
                _loginState.value = LoginState.Success

                val uid = FirebaseAuth.getInstance().currentUser?.uid
                uid?.let {
                    val planId = accesoRepo.obtenerPrimerPlanIdDeUsuario(it)
                    _planId.value = planId
                }

            } catch (e: Exception) {
                _loginState.value = LoginState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun resetLoginState() {
        _loginState.value = LoginState.Idle
    }
}

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    object Success : LoginState()
    data class Error(val message: String) : LoginState()
}
