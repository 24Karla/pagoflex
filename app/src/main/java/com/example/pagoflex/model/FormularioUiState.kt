package com.example.pagoflex.model

enum class EstadoPago(val estado: String){
    PAGADO("Pagado"),
    VENCIDO("Vencido"),
    PENDIENTE("Pendiente"),
    EN_REVISION("En revision")
}

data class FormularioUiState(
    val nombre: String = "",
    val correo: String = "",
    val contrasena: String = "",
    val ciudad: String = "Santiago",
    val ciudades: List<String> = listOf("Santiago", "Valparaíso", "Concepción", "Temuco"),
    val estado: EstadoPago = EstadoPago.EN_REVISION,
    val recibirNoticias: Boolean = false,
    val aceptaTerminos: Boolean = false,

    val contrasenaVisible: Boolean = false,

    val errorNombre: String? = null,
    val errorCorreo: String? = null,
    val errorContrasena: String? = null,
    val errorTerminos: String? = null,

    val registroExitoso: Boolean = false
)

