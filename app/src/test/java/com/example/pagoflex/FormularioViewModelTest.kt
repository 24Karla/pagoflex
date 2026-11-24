package com.example.pagoflex

import com.example.pagoflex.viewmodel.FormularioViewModel
import org.junit.Assert.*
import org.junit.Test

class FormularioViewModelTest {

    @Test
    fun registrar_pago_monto_invalido_falla() {
        val viewModel = FormularioViewModel()
        viewModel.onMontoCambia("-5000")
        viewModel.onConceptoCambia("Luz y Agua")
        val resultado = viewModel.registrarPago()

        assertFalse(resultado)
        assertNotNull(viewModel.uiState.value.errorMonto)
    }

    @Test
    fun registrar_pago_valido_exitoso() {
        val viewModel = FormularioViewModel()
        viewModel.onMontoCambia("25000")
        viewModel.onConceptoCambia("Pago de Servicio")
        val resultado = viewModel.registrarPago()

        assertTrue(resultado)
        assertTrue(viewModel.uiState.value.solicitudRegistrada)
    }
}
