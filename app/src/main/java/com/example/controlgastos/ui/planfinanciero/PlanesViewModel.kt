package com.example.controlgastos.ui.planfinanciero

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.controlgastos.data.initializer.FirestoreInitializer
import com.example.controlgastos.data.model.AccesoPlanFinanciero
import com.example.controlgastos.data.model.PlanFinanciero
import com.example.controlgastos.data.repository.AccesoPlanFinancieroRepository
import com.example.controlgastos.data.repository.CategoriaRepository
import com.example.controlgastos.data.repository.GastoRepository
import com.example.controlgastos.data.repository.IngresoRepository
import com.example.controlgastos.data.repository.PlanFinancieroRepository
import com.example.controlgastos.data.repository.UsuarioRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Date

class PlanesViewModel : ViewModel() {

    private val repoPlanes = PlanFinancieroRepository()
    private val repoAccesos = AccesoPlanFinancieroRepository()
    private val repoUsuarios = UsuarioRepository()
    private val repoGastos = GastoRepository()
    private val repoIngresos = IngresoRepository()
    private val repoCategorias = CategoriaRepository()

    private val _planes = MutableStateFlow<List<PlanFinanciero>>(emptyList())
    val planes: StateFlow<List<PlanFinanciero>> = _planes

    private val _planesInvitaciones = MutableStateFlow<List<PlanFinanciero>>(emptyList())
    val planesInvitaciones: StateFlow<List<PlanFinanciero>> = _planesInvitaciones

    private val _accesos = MutableStateFlow<List<AccesoPlanFinanciero>>(emptyList())
    val accesos: StateFlow<List<AccesoPlanFinanciero>> = _accesos

    private val _invitaciones = MutableStateFlow<List<AccesoPlanFinanciero>>(emptyList())
    val invitaciones: StateFlow<List<AccesoPlanFinanciero>> = _invitaciones

    private val _nombresCreadores = MutableStateFlow<Map<String, String>>(emptyMap())
    val nombresCreadores: StateFlow<Map<String, String>> = _nombresCreadores

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje

    private val _hayInvitacionesPendientes = MutableStateFlow(false)
    val hayInvitacionesPendientes: StateFlow<Boolean> = _hayInvitacionesPendientes

    private val currentUserId: String
        get() = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()

    fun cargarDatos() {
        cargarPlanesDelUsuario()
        cargarInvitacionesPendientes()
    }

    fun cargarPlanesDelUsuario() {
        viewModelScope.launch {
            _isLoading.value = true
            Log.d("PlanesVM", "Planes cargados:")
            val planesList = repoPlanes.obtenerPlanesDeUsuario(currentUserId)
            _planes.value = planesList

            val accesos = repoAccesos.obtenerAccesosDeUsuario(currentUserId)
            _accesos.value = accesos // saber qué accesos tiene el usuario

            cargarNombresCreadores(planesList)

            _isLoading.value = false
        }
    }

    private fun cargarNombresCreadores(planes: List<PlanFinanciero>) {
        val actuales = _nombresCreadores.value?.toMutableMap() ?: mutableMapOf()

        val uidsFaltantes = planes.map { it.creadorId }
            .distinct()
            .filter { !actuales.containsKey(it) && it.isNotBlank() }

        if (uidsFaltantes.isEmpty()) return

        uidsFaltantes.forEach { uid ->
            repoUsuarios.obtenerNombrePorUid(uid) { nombre ->
                if (nombre != null) {
                    actuales[uid] = nombre
                    _nombresCreadores.value = actuales
                }
            }
        }
    }

    fun cargarInvitacionesPendientes() {
        repoAccesos.obtenerInvitacionesPendientes(currentUserId) { accesosList ->
            _invitaciones.value = accesosList

            //Actualizamos el flag de invitaciones pendientes
            _hayInvitacionesPendientes.value = accesosList.isNotEmpty()

            // Cargar los planes de las invitaciones
            val planIds = accesosList.map { it.planId }.distinct()
            if (planIds.isEmpty()) return@obtenerInvitacionesPendientes

            viewModelScope.launch {
                try {
                    val planes = repoPlanes.obtenerPlanesPorIds(planIds)
                    _planesInvitaciones.value = planes
                    cargarNombresCreadores(planes)
                } catch (e: Exception) {
                    Log.e("PlanesVM", "Error al cargar nombres de creadores de invitaciones", e)
                }
            }
        }
    }

    fun crearNuevoPlan(nombre: String, descripcion: String, presupuesto: Double) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val nuevoPlan = PlanFinanciero(
                    nombre = nombre,
                    descripcion = descripcion,
                    presupuestoMensual = presupuesto,
                    creadorId = currentUserId,
                    fechaCreacion = Date()
                )

                // Crear el plan y obtener su ID
                val planId = repoPlanes.crearPlan(nuevoPlan)

                // Inicializar categorías por defecto para ese plan
                val initializer = FirestoreInitializer(currentUserId)
                initializer.inicializarCategoriasSuspend(planId)

                // Recargar planes tras crear
                cargarPlanesDelUsuario()

            } catch (e: Exception) {
                Log.e("PlanesVM", "Error al crear plan", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun eliminarAcceso(usuarioId: String, planId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val exito = repoAccesos.eliminarAcceso(usuarioId, planId)
            if (exito) {
                cargarPlanesDelUsuario()
            } else {
                _isLoading.value = false
            }
        }
    }

    fun aceptarInvitacion(planId: String) {
        repoAccesos.actualizarEstado(currentUserId, planId, "aceptado") { exito ->
            if (exito) {
                cargarPlanesDelUsuario()
                cargarInvitacionesPendientes()
            }
        }
    }

    fun rechazarInvitacion(planId: String) {
        repoAccesos.actualizarEstado(currentUserId, planId, "rechazado") { exito ->
            if (exito) cargarInvitacionesPendientes()
        }
    }

    //Actualizar plan
    fun actualizarPlan(plan: PlanFinanciero) {
        _isLoading.value = true
        repoPlanes.actualizarPlan(plan) { exito ->
            if (exito) {
                cargarPlanesDelUsuario()
            } else {
                _isLoading.value = false
            }
        }
    }

    //Eliminar Plan si es un plan propietario sin más usuarios. Y si hay más planes disponibles para el mismo usuario
    fun eliminarPlan(planId: String) {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                val planesDelUsuario = _planes.value

                // Verifica si solo tiene ese plan
                if (planesDelUsuario.size <= 1) {
                    _isLoading.value = false
                    Log.w("PlanesVM", "No se puede eliminar: es el único plan del usuario.")
                    mostrarMensaje("No se puede eliminar: es el único plan disponible.")
                    return@launch
                }

                // Verifica si es propietario y único acceso
                val accesosDelPlan = repoAccesos.obtenerAccesosPorPlan(planId)
                val accesoActual = accesosDelPlan.find { it.usuarioId == currentUserId }

                val esPropietarioUnico = accesoActual?.esPropietario == true && accesosDelPlan.size == 1
                if (!esPropietarioUnico) {
                    _isLoading.value = false
                    Log.w("PlanesVM", "No se puede eliminar: no es propietario único.")
                    mostrarMensaje("No se puede eliminar: el plan es compartido")
                    return@launch
                }

                // Elimina el acceso y el plan
                val exitoAcceso = repoAccesos.eliminarAcceso(currentUserId, planId)
                val exitoGastos = repoGastos.eliminarGastosPorPlan(planId)
                val exitoIngresos = repoIngresos.eliminarIngresosPorPlan(planId)
                val exitoCategorias = repoCategorias.eliminarCategoriasPorPlan(planId)
                val exitoPlan = repoPlanes.eliminarPlan(planId)

                if (exitoAcceso && exitoGastos && exitoIngresos && exitoCategorias && exitoPlan) {
                    mostrarMensaje("Plan y datos relacionados eliminados correctamente.")
                    cargarPlanesDelUsuario()
                } else {
                    mostrarMensaje("Error al eliminar uno o más elementos del plan.")
                    _isLoading.value = false
                }

            } catch (e: Exception) {
                _isLoading.value = false
                mostrarMensaje("Error: ${e.message}")
            }
        }
    }

    //Funciones auxiliares
    fun mostrarMensaje(mensaje: String) {
        _mensaje.value = mensaje
    }

    fun limpiarMensaje() {
        _mensaje.value = null
    }

    //Limpiar estados
    //Quitarlo si no se usa
/*    fun limpiar() {
        _planes.value = emptyList()
        _accesos.value = emptyList()
        _invitaciones.value = emptyList()
        _nombresCreadores.value = emptyMap()
        _isLoading.value = false
    }*/
}
