package com.example.controlgastos.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

// 1. Crea el DataStore usando delegación de extensión
val Context.dataStore by preferencesDataStore(name = "plan_prefs")

object PlanPreferences {

    private val LAST_PLAN_ID = stringPreferencesKey("last_plan_id")

    // 2. Guardar último planId
    suspend fun guardarUltimoPlan(context: Context, planId: String) {
        context.dataStore.edit { prefs ->
            prefs[LAST_PLAN_ID] = planId
        }
    }

    // 3. Obtener último planId
    suspend fun obtenerUltimoPlan(context: Context): String? {
        val prefs = context.dataStore.data.first()
        return prefs[LAST_PLAN_ID]
    }

    // 4. Borrar planId guardado
    suspend fun borrarUltimoPlan(context: Context) {
        context.dataStore.edit { prefs ->
            prefs.remove(LAST_PLAN_ID)
        }
    }
}