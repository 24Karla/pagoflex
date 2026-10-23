package com.example.pagoflex.model

enum class EstadoPago(val estado: String){
    PAGADO("Pagado"),
    VENCIDO("Vencido"),
    PENDIENTE("Pendiente"),
    EN_REVISION("En revision")
}

data class FormularioUiState(
    val monto: String = "",
    val concepto: String = "",
    val estado: EstadoPago = EstadoPago.EN_REVISION,
    val comprobanteAdjunto: Boolean = false,
    val solicitudRegistrada: Boolean = false,
    val errorMonto: String? = null,
    val errorConcepto: String? = null
)

