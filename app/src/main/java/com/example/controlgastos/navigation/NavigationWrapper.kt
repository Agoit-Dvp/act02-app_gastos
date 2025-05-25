package com.example.controlgastos.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.controlgastos.ui.login.LoginScreen
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.controlgastos.data.repository.AccesoPlanFinancieroRepository
import com.example.controlgastos.ui.categoria.CategoriaScreen
import com.example.controlgastos.ui.gasto.GastosScreen
import com.example.controlgastos.ui.home.HomeScreen
import com.example.controlgastos.ui.ingreso.IngresosScreen
import com.example.controlgastos.ui.planfinanciero.PlanesListadoEntryPoint
import com.example.controlgastos.ui.planfinanciero.PlanesUsuarioScreen
import com.example.controlgastos.ui.signup.RegisterScreen
import com.example.controlgastos.ui.usuario.ListaUsuariosScreen
import com.example.controlgastos.ui.usuario.UsuarioScreen
import com.google.firebase.auth.FirebaseAuth


@Composable
fun NavigationWrapper() {
    val navController = rememberNavController() //controlar el flujo de navegacion entre pantallas
    val accesoRepo = remember { AccesoPlanFinancieroRepository() }

    var planId by remember { mutableStateOf<String?>(null) }
    val currentUser = FirebaseAuth.getInstance().currentUser

    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            planId = accesoRepo.obtenerPrimerPlanIdDeUsuario(currentUser.uid)
        }
    }

    if (currentUser != null && planId == null) {
        Box(Modifier.fillMaxSize()) {
            CircularProgressIndicator(Modifier.align(Alignment.Center))
        }
        return
    }

    NavHost(
        navController = navController,
        // fallback seguro en caso de que el usuario no tenga planes
        startDestination = when {
            currentUser == null -> Login
            planId != null -> Home(planId!!)
            else -> PlanesListado
        }
    ) {

        composable<Login> {
            //Si la función solo tiene como parametro una función Lambda podemos quitar los parentesis
            LoginScreen(
                viewModel = viewModel(),
                planId = planId,
                navigateToRegister = {
                    navController.navigate(Register)
                },
                navigateToHome = { id ->
                    navController.navigate(Home(id)) {
                        popUpTo(Login) { inclusive = true }
                        launchSingleTop = true
                    }
                }

            )
        }

        composable<Home> {backStackEntry ->
            val args = backStackEntry.toRoute<Home>()
            HomeScreen(
                planId = args.planId,//pasando parametro de id del plan
                onNavigateToIngresos = { navController.navigate(Ingresos(planId = args.planId)) },
                onNavigateToGastos = { navController.navigate(Gastos(
                    planId = args.planId
                )) },
                onNavigateToUsuarios = { navController.navigate(Usuarios) },
                onNavigateToPerfil = { navController.navigate(Perfil) },
                onNavigateToCategorias = { navController.navigate(Categorias(planId = args.planId)) },
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

        composable<Categorias> { backStackEntry ->
            val categorias = backStackEntry.toRoute<Categorias>()
            CategoriaScreen(planId = categorias.planId)
        }

        composable<PlanesUsuario> {backStackEntry -> //Recebir el parametro que devuelve esta función
            val planesUsuario = backStackEntry.toRoute<PlanesUsuario>()
            PlanesUsuarioScreen( viewModel = viewModel(), planesUsuario.usuarioId)
        }

        composable<PlanesListado> {
            PlanesListadoEntryPoint(navController = navController)
        }

    }

}