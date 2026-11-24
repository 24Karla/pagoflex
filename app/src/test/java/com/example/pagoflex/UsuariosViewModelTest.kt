package com.example.pagoflex

import com.example.pagoflex.model.Rol
import org.junit.Assert.*
import org.junit.Test

class UsuariosViewModelTest {

    @Test
    fun registro_asigna_el_rol_segun_el_dominio_exacto() {
        assertEquals(Rol.ID_AGENTE, Rol.rolIdParaRegistro("hola@agente.cl"))
        assertEquals(Rol.ID_EJECUTIVO, Rol.rolIdParaRegistro(" Hola@EJECUTIVO.CL "))
        assertEquals(Rol.ID_EJECUTIVO, Rol.rolIdParaRegistro("67@gerente.cl"))
        assertEquals(Rol.ID_USUARIO, Rol.rolIdParaRegistro("hola@gerente.cl.ejemplo.com"))
        assertEquals(Rol.ID_USUARIO, Rol.rolIdParaRegistro("hola@agente.cl.ejemplo.com"))
        assertEquals(Rol.ID_USUARIO, Rol.rolIdParaRegistro("hola@ejemplo.com"))
    }

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
