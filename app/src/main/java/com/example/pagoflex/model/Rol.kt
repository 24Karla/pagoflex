package com.example.pagoflex.model

data class Rol(
    val id: Long,
    val nombre: String
) {
    companion object {
        const val ID_AGENTE = 1L
        const val ID_EJECUTIVO = 2L
        const val ID_USUARIO = 3L

        fun rolIdARutaString(rolId: Long): String {
            return when (rolId) {
                ID_AGENTE -> "agente"
                ID_EJECUTIVO -> "ejecutivo"
                else -> "usuario"
            }
        }

        fun rutaStringARolId(rolString: String): Long {
            return when (rolString.lowercase()) {
                "agente" -> ID_AGENTE
                "ejecutivo" -> ID_EJECUTIVO
                else -> ID_USUARIO
            }
        }
    }
}
