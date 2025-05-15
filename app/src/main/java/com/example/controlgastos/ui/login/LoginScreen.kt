package com.example.controlgastos.ui.login


import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.controlgastos.R
import com.example.controlgastos.ui.theme.AppColors
import com.example.controlgastos.ui.theme.ControlGastosTheme
import kotlinx.coroutines.launch

//Componente de la pantalla configuracion general
@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    navigateToRegister: () -> Unit,
    navigateToHome: () -> Unit
) { //Para poder acceder a los estados de LoginViewModel
    val loginSuccess: Boolean by viewModel.loginSuccess.observeAsState(initial = false)
    val errorMessage: String? by viewModel.errorMessage.observeAsState()
    val context = LocalContext.current //Saber el contexto actual de la UI para pasarlo a Toast

    LaunchedEffect(loginSuccess, errorMessage) {
        when {
            loginSuccess == true && errorMessage == null -> {
                navigateToHome()
            }

            errorMessage != null -> {
                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                // Opcional: limpiar el mensaje para no mostrarlo varias veces
                //viewModel.clearError()
            }
        }
    }
    Box(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Login(Modifier.align(Alignment.Center), viewModel, navigateToRegister) // Pasar los estados a todos los composes

    }

}

//Componente que llamara a los demás componentes y les posiciona en la pantalla
@Composable
fun Login(modifier: Modifier, viewModel: LoginViewModel, navigateToRegister: () -> Unit) {

    val email: String by viewModel.email.observeAsState(initial = "")
    val password: String by viewModel.password.observeAsState(initial = "")
    val loginEnable: Boolean by viewModel.loginEnable.observeAsState(initial = false)
    val isLoading: Boolean by viewModel.isLoading.observeAsState(initial = false)
    val coroutineScope = rememberCoroutineScope()

    if (isLoading) {
        Box(Modifier.fillMaxSize()) {
            CircularProgressIndicator(Modifier.align(Alignment.Center))
        }
    } else {
        Column(modifier = modifier) {
            HeaderImage(Modifier.align(Alignment.CenterHorizontally))
            Spacer(modifier = Modifier.padding(16.dp))
            EmailField(email) { viewModel.onLoginChanged(it, password) }
            Spacer(modifier = Modifier.padding(4.dp))
            PasswordField(password) { viewModel.onLoginChanged(email, it) }
            Spacer(modifier = Modifier.padding(8.dp))
            ForgotPassword(Modifier.align(Alignment.Start))
            Spacer(modifier = Modifier.padding(16.dp))
            LoginButton(loginEnable) {
                coroutineScope.launch { viewModel.onLoginSelected() }
            }
            Spacer(modifier = Modifier.padding(16.dp))
            RegisterButton(onRegisterClick = navigateToRegister)
        }

    }

}

//Componente boton de login
@Composable
fun LoginButton(loginEnable: Boolean, onLoginSelected: () -> Unit) {
    Button(
        onClick = { onLoginSelected() }, modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = AppColors.primary,
            disabledContainerColor = AppColors.primary.copy(alpha = 0.5f),
            contentColor = Color.White,
            disabledContentColor = Color.White.copy(alpha = 0.5f)
        ), enabled = loginEnable
    ) {
        Text("LOGIN", color = MaterialTheme.colorScheme.onPrimary)
    }
}
//Componente de boton de registro
@Composable
fun RegisterButton(onRegisterClick: () -> Unit) {
    Text(
        text = "¿No tienes cuenta? Regístrate",
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onRegisterClick() }
            .padding(vertical = 8.dp),
        color = AppColors.primary,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center
    )
}

//Componente ForgotPassword
@Composable
fun ForgotPassword(modifier: Modifier) {
    Text(
        "¿Olvidaste la contraseña?",
        modifier = modifier.clickable { },
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF483CBB),
    )
}

//Componente Password
@Composable
fun PasswordField(password: String, onTextFieldChanged: (String) -> Unit) {
    TextField(
        value = password, onValueChange = { onTextFieldChanged(it) },
        placeholder = { Text(text = "Password") },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        singleLine = true,
        maxLines = 1,
        colors = TextFieldDefaults.colors(
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.DarkGray,
            focusedIndicatorColor = AppColors.primary,
            unfocusedIndicatorColor = AppColors.secondary,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color(0xFFF5F5F5)
        )
    )
}

//Componente Logo
@Composable
fun HeaderImage(modifier: Modifier) {
    Image(
        painter = painterResource(id = R.drawable.login_logo_plansave),
        contentDescription = "Imagen de inicio de sesión", // ¡Un ContentDescription descriptivo es importante para la accesibilidad!
        modifier = modifier.size(200.dp)
    )
}

//Componente email
@Composable
fun EmailField(email: String, onTextFieldChanged: (String) -> Unit) {

    TextField(
        value = email,
        onValueChange = { onTextFieldChanged(it) },
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(text = "Correo electronico") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        singleLine = true,
        maxLines = 1,
        colors = TextFieldDefaults.colors(
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.DarkGray,
            focusedIndicatorColor = AppColors.primary,
            unfocusedIndicatorColor = AppColors.secondary,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color(0xFFF5F5F5)
        )
    )
}
