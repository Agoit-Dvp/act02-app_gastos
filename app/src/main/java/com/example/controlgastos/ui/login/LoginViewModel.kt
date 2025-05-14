package com.example.controlgastos.ui.login

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.controlgastos.data.AuthRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class LoginViewModel(private val authRepository: AuthRepository = AuthRepository()) : ViewModel() {

    private val _email = MutableLiveData<String>() //Estado privado
    val email: LiveData<String> = _email // Modificar el estado privado solo desde el loginViewModel

    private val _password = MutableLiveData<String>()
    val password: LiveData<String> = _password

    private val _loginEnable = MutableLiveData<Boolean>()
    val loginEnable: LiveData<Boolean> = _loginEnable

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _loginSuccess = MutableLiveData<Boolean>()
    val loginSuccess: LiveData<Boolean> = _loginSuccess

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

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
            _isLoading.value = true
            authRepository.loginUser(email, password) { success, error ->
                _isLoading.value = false
                if (success) {
                    _loginSuccess.value = true
                    _errorMessage.value = null
                } else {
                    _loginSuccess.value = false
                    _errorMessage.value = error
                }
            }
        }
    }

}
