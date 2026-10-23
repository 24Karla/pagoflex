package com.example.pagoflex.viewmodel

import androidx.lifecycle.ViewModel
import com.example.pagoflex.model.EstadoPago
import com.example.pagoflex.model.FormularioUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.math.BigDecimal

class FormularioViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(FormularioUiState())
    val uiState: StateFlow<FormularioUiState> = _uiState.asStateFlow()

    fun onMontoCambia(valor: String) {
        if (valor.isEmpty() || valor.matches(Regex("^\\d{0,9}([.,]\\d{0,2})?$"))) {
            _uiState.value = _uiState.value.copy(
                monto = valor,
                errorMonto = null,
                solicitudRegistrada = false
            )
        }
    }

    fun onConceptoCambia(valor: String) {
        _uiState.value = _uiState.value.copy(
            concepto = valor,
            errorConcepto = null,
            solicitudRegistrada = false
        )
    }

    fun onComprobanteAdjuntado(adjunto: Boolean) {
        _uiState.value = _uiState.value.copy(comprobanteAdjunto = adjunto)
    }

    fun limpiar() {
        _uiState.value = FormularioUiState()
    }

    fun registrarPago(): Boolean {
        val actual = _uiState.value
        val monto = actual.monto.replace(',', '.').toBigDecimalOrNull()
        val errorMonto = when {
            monto == null -> "Ingresa un monto válido."
            monto <= BigDecimal.ZERO -> "El monto debe ser mayor que cero."
            else -> null
        }
        val errorConcepto =
            if (actual.concepto.isBlank()) "Ingresa el motivo del pago." else null
        val valido = errorMonto == null && errorConcepto == null

        _uiState.value = actual.copy(
            estado = EstadoPago.EN_REVISION,
            errorMonto = errorMonto,
            errorConcepto = errorConcepto,
            solicitudRegistrada = valido
        )
        return valido
    }
}