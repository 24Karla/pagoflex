package com.example.pagoflex.ui.theme

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object PreferenciasTema {
    private const val ARCHIVO = "pagoflex_tema"
    private const val CLAVE_OSCURO = "modo_oscuro"

    // Estado observable: al cambiarlo, toda la app se recompone con el nuevo tema.
    var modoOscuro by mutableStateOf(false)
        private set

    // Primera vez: sigue el tema del sistema. Después: usa lo que el usuario eligió.
    fun iniciar(context: Context, sistemaOscuro: Boolean) {
        val prefs = context.applicationContext.getSharedPreferences(ARCHIVO, Context.MODE_PRIVATE)
        modoOscuro = if (prefs.contains(CLAVE_OSCURO)) prefs.getBoolean(CLAVE_OSCURO, sistemaOscuro) else sistemaOscuro
    }

    fun cambiar(context: Context, oscuro: Boolean) {
        modoOscuro = oscuro
        context.applicationContext
            .getSharedPreferences(ARCHIVO, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(CLAVE_OSCURO, oscuro)
            .apply()
    }
}