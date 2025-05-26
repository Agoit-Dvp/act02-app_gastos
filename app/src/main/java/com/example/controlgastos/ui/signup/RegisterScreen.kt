package com.example.controlgastos.ui.signup

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.controlgastos.ui.theme.AppColors

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel = viewModel(),
    navigateToLogin: () -> Unit
) {
    val nombre by viewModel.nombre.observeAsState("")
    val email by viewModel.email.observeAsState("")
    val password by viewModel.password.observeAsState("")
    val telefono by viewModel.telefono.observeAsState("")

    //Estados de confirmación y error
    val success by viewModel.registerSuccess.observeAsState(false)
    val error by viewModel.errorMessage.observeAsState()

    val context = LocalContext.current

    LaunchedEffect(success) {
        if (success) {
            Toast.makeText(context, "Registro exitoso", Toast.LENGTH_SHORT).show()
            navigateToLogin()
        }
    }

    LaunchedEffect(error) {
        error?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        Spacer(modifier = Modifier.height(32.dp))
        TextField(
            value = nombre,
            onValueChange = { viewModel.nombre.value = it },
            placeholder = { Text("Nombre completo") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = email,
            onValueChange = { viewModel.email.value = it },
            placeholder = { Text("Correo electrónico") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = password,
            onValueChange = { viewModel.password.value = it },
            placeholder = { Text("Contraseña (mínimo 6 caracteres)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = telefono,
            onValueChange = { viewModel.telefono.value = it },
            placeholder = { Text("Teléfono (opcional)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { viewModel.onRegisterClick() },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AppColors.primary)
        ) {
            Text("REGISTRARSE")
        }
    }
}
