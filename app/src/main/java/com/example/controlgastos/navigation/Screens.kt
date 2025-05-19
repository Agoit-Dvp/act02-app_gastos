package com.example.controlgastos.navigation

import kotlinx.serialization.Serializable

//Permite pasar todas las pantallas al NavHost como objeto serailizable
//Definir aqui todas las vistas

@Serializable //Permite pasar los objetos como parametro de navegacion
object Login

@Serializable
data class Home(val planId: String)

/*@Serializable
object Home()*/ //Sin pasar id

@Serializable
object Ingresos

@Serializable
data class Gastos(val planId: String)

@Serializable
object Usuarios

@Serializable
object Perfil

@Serializable
object Register

@Serializable
object Categorias

@Serializable
object PlanesListado

@Serializable
data class PlanesUsuario(val usuarioId: String)

//Pasar parametros, debemos usar dataclass
//@Serializable
//data class Detail(val name: String)