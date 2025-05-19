package com.example.controlgastos.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.controlgastos.ui.login.LoginScreen
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.controlgastos.data.repository.PlanFinancieroRepository
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
    val navController = rememberNavController() //controlar el flujo de navegacion entre pantallas
    val planRepo = remember { PlanFinancieroRepository() }

    val user = FirebaseAuth.getInstance().currentUser

    val planIdState = produceState<String?>(initialValue = null, user) {
        if (user != null) {
            planRepo.obtenerPlanesDeUsuario(user.uid) { planes ->
                value = planes.firstOrNull()?.id
            }
        }
    }

    val planId = planIdState.value

    if (user != null && planId == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    NavHost(
        navController = navController,
        startDestination =  when {
            user == null -> Login
            planId != null -> Home(planId)
            else -> Login // fallback seguro en caso de que el usuario no tenga planes
        } // Ir home con el primer planId del usuario
    ) {

        composable<Login> {
            //Si la función solo tiene como parametro una función Lambda podemos quitar los parentesis
            LoginScreen(
                viewModel = viewModel(),
                navigateToRegister = {
                    navController.navigate(Register)
                },
                navigateToHome = {
                    planId?.let {
                        navController.navigate(Home(it)) {
                            popUpTo(Login) { inclusive = true }
                        }
                    }
                }

            )
        }

        composable<Home> {backStackEntry ->
            val args = backStackEntry.toRoute<Home>()
            HomeScreen(
                planId = args.planId, //pasando parametro de id del plan
                onNavigateToIngresos = { navController.navigate(Ingresos(planId = args.planId)) },
                onNavigateToGastos = { navController.navigate(Gastos(
                    planId = args.planId
                )) },
                onNavigateToUsuarios = { navController.navigate(Usuarios) },
                onNavigateToPerfil = { navController.navigate(Perfil) },
                onNavigateToCategorias = { navController.navigate(Categorias) },
                onNavigateToPlanesUsuario = {
                    navController.navigate(PlanesListado)
                },
                onLogout = {
                    navController.navigate(Login) {
                        popUpTo(Login) { inclusive = true }
                    }
                }
            )
        }

        composable<Gastos> { backStackEntry ->
            val gastos = backStackEntry.toRoute<Gastos>()
            GastosScreen(planId = gastos.planId)
        }

        composable<Ingresos> { backStackEntry ->
            val ingresos = backStackEntry.toRoute<Ingresos>()
            IngresosScreen(planId = ingresos.planId)
        }

        composable<Usuarios> {
            ListaUsuariosScreen()
        }

        composable<Perfil> {
            UsuarioScreen()
        }

        composable<Register> {
            RegisterScreen(navigateToLogin = {
                navController.navigate(Login) {
                    popUpTo(Register) { inclusive = true }
                }
            })
        }

        composable<Categorias> {
            CategoriaScreen()
        }

        composable<PlanesUsuario> {backStackEntry -> //Recebir el parametro que devuelve esta función
            val planesUsuario = backStackEntry.toRoute<PlanesUsuario>()
            PlanesUsuarioScreen( viewModel = viewModel(), planesUsuario.usuarioId)
        }

        composable<PlanesListado> {
            val usuarioId = user?.uid.orEmpty()
            PlanesListadoEntryPoint(usuarioId, navController = navController)
        }

    }

}