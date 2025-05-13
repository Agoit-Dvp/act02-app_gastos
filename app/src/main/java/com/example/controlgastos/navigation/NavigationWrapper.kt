package com.example.controlgastos.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.controlgastos.ui.login.LoginScreen
import androidx.navigation.compose.composable
import com.example.controlgastos.ui.home.HomeScreen
import com.example.controlgastos.ui.login.LoginViewModel


@Composable
fun NavigationWrapper(){
    val navController = rememberNavController() //Se encarga de controlar el flujo de navegacion entre todas la pantallas
    NavHost(navController = navController, startDestination = Login){
        composable<Login>{
            //Si la función solo tiene como parametro una función Lambda podemos quitar los parentesis
            LoginScreen(
                viewModel { LoginViewModel() },
                navigateToHome = { navController.navigate(Home) }
            )    //Pasamos el objeto Home de Screens.kt
        }

        composable<Home> {
            HomeScreen()
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