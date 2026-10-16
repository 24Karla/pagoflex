package com.example.pagoflex.model

data class Perfil(
    val nombre:String,
    val ciudad:String,
    val cuenta:String,
    val ntele:Int
)

data class PerfilUiState(
    val perfil: Perfil,
    val contacto: Boolean = false //si es que es un contacto guardado e antes
)