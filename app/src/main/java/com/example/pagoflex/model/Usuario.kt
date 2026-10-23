package com.example.pagoflex.model

data class Usuario (    val nombre: String,
                        val cuenta: String,
                        val ciudad: String,
                        val id: Long = 0,
                        val rolId: Long = Rol.ID_USUARIO,
                        val rolNombre: String = "")

