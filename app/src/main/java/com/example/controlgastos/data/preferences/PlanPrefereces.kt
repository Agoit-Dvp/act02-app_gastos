package com.example.controlgastos.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

//DataStore usando delegación de extensión
val Context.dataStore by preferencesDataStore(name = "plan_prefs")

object PlanPreferences {

    private fun keyUsuario(userId: String) = stringPreferencesKey("last_plan_id_$userId")

    // Guardar planId junto al usuario actual
    suspend fun guardarUltimoPlan(context: Context, userId: String, planId: String) {
        context.dataStore.edit { prefs ->
            prefs[keyUsuario(userId)] = planId
        }
    }

    suspend fun obtenerUltimoPlan(context: Context, userId: String): String? {
        val prefs = context.dataStore.data.first()
        return prefs[keyUsuario(userId)]
    }

    suspend fun borrarUltimoPlan(context: Context, userId: String) {
        context.dataStore.edit { prefs ->
            prefs.remove(keyUsuario(userId))
        }
    }
}