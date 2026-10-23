package com.example.pagoflex.model

data class Perfil(
    val nombre:String,
    val ciudad:String,
    val cuenta:String,
    val ntele:Int,
    val avisosActivos: Boolean = false,
    val diasAviso: List<Int> = listOf(1, 3, 7),
    val orgRecom: String,
    val orgContac: Boolean = false,
    val reportAbierto: Int,
    val reportActual: String

)

data class PerfilUiState(
    val perfil: Perfil,
    val contacto: Boolean = false //si es que es un contacto guardado e antes
)