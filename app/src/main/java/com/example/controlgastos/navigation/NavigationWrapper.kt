package com.example.controlgastos.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.controlgastos.ui.login.LoginScreen
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.controlgastos.ui.categoria.CategoriaScreen
import com.example.controlgastos.ui.gasto.GastosScreen
import com.example.controlgastos.ui.home.HomeScreen
import com.example.controlgastos.ui.ingreso.IngresosScreen
import com.example.controlgastos.ui.planfinanciero.PlanesListadoEntryPoint
import com.example.controlgastos.ui.planfinanciero.PlanesListadoScreen
import com.example.controlgastos.ui.planfinanciero.PlanesUsuarioScreen
import com.example.controlgastos.ui.planfinanciero.PlanesViewModel
import com.example.controlgastos.ui.signup.RegisterScreen
import com.example.controlgastos.ui.usuario.ListaUsuariosScreen
import com.example.controlgastos.ui.usuario.UsuarioScreen
import com.google.firebase.auth.FirebaseAuth


@Composable
fun NavigationWrapper() {
    val navController =
        rememberNavController() //Se encarga de controlar el flujo de navegacion entre todas la pantallas
    NavHost(
        navController = navController,
        startDestination = if (FirebaseAuth.getInstance().currentUser != null) Home("3GOA7c0N0f5yV70aOQct") else Login
    ) {
/*        composable<Login> { //Version sin id
            //Si la función solo tiene como parametro una función Lambda podemos quitar los parentesis
            LoginScreen(
                viewModel = viewModel(),
                navigateToRegister = {
                    navController.navigate(Register)
                },
                navigateToHome = {
                    navController.navigate(Home) {//Pasamos el objeto Home de Screens.kt
                        popUpTo(Login) { inclusive = true }
                    }
                }
            )
        }*/

        composable<Login> { //Version sin id
            //Si la función solo tiene como parametro una función Lambda podemos quitar los parentesis
            LoginScreen(
                viewModel = viewModel(),
                navigateToRegister = {
                    navController.navigate(Register)
                },
                navigateToHome = {
                    val planId = "3GOA7c0N0f5yV70aOQct" //Solo para probar, lo correcto es obtenerlo
                    navController.navigate(Home(planId)) {//Pasamos data class Home con argumento
                        popUpTo(Login) { inclusive = true }
                    }
                }
            )
        }

        composable<Home> {backStackEntry ->
            val args = backStackEntry.toRoute<Home>()
            HomeScreen(
                planId = args.planId, //pasando parametro de id del plan
                onNavigateToIngresos = { navController.navigate(Ingresos) },
                onNavigateToGastos = { navController.navigate(Gastos) },
                onNavigateToUsuarios = { navController.navigate(Usuarios) },
                onNavigateToPerfil = { navController.navigate(Perfil) },
                onNavigateToCategorias = { navController.navigate(Categorias) },
                onNavigateToPlanesUsuario = {
                    navController.navigate(PlanesListado)
                },
                onLogout = {
                    navController.navigate(Login) {
                        popUpTo(Home) { inclusive = true }
                    }
                }
            )
        }

        composable<Gastos> {
            GastosScreen()
        }

        composable<Ingresos> {
            IngresosScreen()
        }

        composable<Usuarios> {
            ListaUsuariosScreen()
        }

        composable<Perfil> {
            UsuarioScreen()
        }

        composable<Register> {
            RegisterScreen(navigateToHome = { navController.navigate(Home) })
        }

        composable<Categorias> {
            CategoriaScreen()
        }

        composable<PlanesUsuario> {backStackEntry -> //Recebir el parametro que devuelve esta función
            val planesUsuario = backStackEntry.toRoute<PlanesUsuario>()
            PlanesUsuarioScreen( viewModel = viewModel(), planesUsuario.usuarioId)
        }

        composable<PlanesListado> {
            val usuarioId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
            PlanesListadoEntryPoint(usuarioId)
        }


// Ejemplos de uso enviando y recibiendo parametro
//        composable<Home> {
//            HomeScreen {name -> navController.navigate(Detail(name = name))} //Parametro string siendo enviado desde HomeScreen
//        }
//
//        composable<Detail> { backStackEntry -> //Recebir el parametro que devuelve esta función
//            val detail = backStackEntry.toRoute<Detail>()
//            DetailScreen(detail.name){ navController.navigate(Login)}
//        }
    }

}