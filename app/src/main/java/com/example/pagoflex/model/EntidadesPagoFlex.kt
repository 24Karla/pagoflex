@file:Suppress("unused")
package com.example.pagoflex.model

data class Usuario(
    val id: Long = 0L,
    val nombre: String,
    val correo: String,
    val rut: String,
    val comuna: String,
    val rol: String, // "Usuario final", "Agente", "Ejecutivo"
    val contrasena: String
)

data class Compromiso(
    val id: Long = 0L,
    val usuarioId: Long,
    val empresa: String,
    val monto: Double,
    val fechaVencimiento: String,
    val estado: String // "Pendiente", "Pagado"
)

data class Pago(
    val id: Long = 0L,
    val compromisoId: Long,
    val usuarioId: Long,
    val monto: Double,
    val fecha: String,
    val metodo: String
)

data class Comuna(
    val id: Long = 0L,
    val nombre: String
)
