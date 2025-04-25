package com.example.controlgastos.ui.login

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.delay

class LoginViewModel : ViewModel() {

    private val _email = MutableLiveData<String>() //Estado privado
    val email: LiveData<String> = _email // Modificar el estado privado solo desde el loginViewModel

    private val _password = MutableLiveData<String>()
    val password: LiveData<String> = _password

    private val _loginEnable = MutableLiveData<Boolean>()
    val loginEnable: LiveData<Boolean> = _loginEnable

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun onLoginChanged(email: String, password: String) {
        _email.value = email
        _password.value = password
        _loginEnable.value = isValidEmail(email) && isValidPassword(password)
    }

    private fun isValidEmail(email: String): Boolean =
        Patterns.EMAIL_ADDRESS.matcher(email).matches() //Confirmar si es un email valido

    private fun isValidPassword(password: String): Boolean =
        password.length > 6 //Prueba sencilla para probar componente

    suspend fun onLoginSelected() { //Coroutine, debe tenr el suspend
        _isLoading.value = true
        delay(4000)
        _isLoading.value = false
    }

}