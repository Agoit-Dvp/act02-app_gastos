package com.example.controlgastos.ui.login


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.controlgastos.R
import com.example.controlgastos.ui.theme.ControlGastosTheme


@Composable
fun LoginScreen() {
    Box(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Login(Modifier.align(Alignment.Center))

    }

}

@Composable
fun Login(modifier: Modifier) {
    Column(modifier = modifier) {
        HeaderImage(Modifier.align(Alignment.CenterHorizontally))
        Spacer(modifier = Modifier.padding(16.dp))
        EmailField()
        Spacer(modifier = Modifier.padding(4.dp))
        PasswordField()
    }

}

@Composable
fun PasswordField() {
    TODO("Not yet implemented")
}

@Composable
fun HeaderImage(modifier: Modifier) {
    Image(
        painter = painterResource(id = R.drawable.ic_login_48),
        contentDescription = "Imagen de inicio de sesión", // ¡Un ContentDescription descriptivo es importante para la accesibilidad!
        modifier = modifier
    )
}

@Composable
fun EmailField() {
    TextField(
        value = "", onValueChange = {}, modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(text = "Correo electronico") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        singleLine = true,
        maxLines = 1,
        colors = TextFieldDefaults.colors(
            focusedTextColor = Color(0xFF0E0E0E),
            unfocusedTextColor = Color(0xFF0E0E0E),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedContainerColor = Color(0xFF2F2E2E),
            unfocusedContainerColor = Color(0xFF2F2E2E)
        )
    )
}

@Preview(showBackground = true)
@Composable
fun LoginPreview() {
    ControlGastosTheme {
        LoginScreen()
    }
}



