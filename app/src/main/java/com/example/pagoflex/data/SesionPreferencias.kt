package com.example.pagoflex.data

import android.content.Context
import androidx.core.content.edit

class SesionPreferencias (context: Context) {
    private val prefs = context.getSharedPreferences("sesion", Context.MODE_PRIVATE)

    companion object{
        private const val CLAVE_ID_USUARIO = "id_usuario"
        private const val SIN_SESION = -1L
        private const val CLAVE_ULTIMA_CONSULTA_LOCAL = "ultima_consulta_local"
    }

    fun guardar(idUsuario: Long){
        prefs.edit { putLong(CLAVE_ID_USUARIO, idUsuario) }
    }

    fun leer(): Long = prefs.getLong(CLAVE_ID_USUARIO, SIN_SESION)

    fun marcarConsultaLocal(): Long {
        val ahora = System.currentTimeMillis()
        prefs.edit { putLong(CLAVE_ULTIMA_CONSULTA_LOCAL, ahora) }
        return ahora
    }

    fun leerConsultaLocal(): Long = prefs.getLong(CLAVE_ULTIMA_CONSULTA_LOCAL, 0L)

    fun borrar(){
        prefs.edit {remove(CLAVE_ID_USUARIO)}
    }

}