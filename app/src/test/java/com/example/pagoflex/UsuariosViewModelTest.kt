package com.example.pagoflex

import org.junit.Assert.*
import org.junit.Test

class UsuariosViewModelTest {

    private fun contrasenaCumpleRequisitos(valor: String): Boolean =
        valor.length >= 8 && valor.any(Char::isUpperCase) && valor.any(Char::isLowerCase) &&
            valor.any(Char::isDigit) && valor.any { !it.isLetterOrDigit() }

    @Test
    fun contrasena_debil_falla_requisitos() {
        val claveDebil = "12345"
        assertFalse(contrasenaCumpleRequisitos(claveDebil))
    }

    @Test
    fun contrasena_fuerte_cumple_requisitos() {
        val claveFuerte = "Password123*"
        assertTrue(contrasenaCumpleRequisitos(claveFuerte))
    }

    @Test
    fun nombre_con_numeros_o_simbolos_falla() {
        val nombreInvalido = "Juan123"
        val esValido = nombreInvalido.all { it.isLetter() || it.isWhitespace() }
        assertFalse(esValido)
    }
}
