package com.example.pagoflex.data.remote

import com.example.pagoflex.model.Usuario

data class LoginRequest(
    val cuenta: String,
    val contrasena: String
)

data class RegistroRequest(
    val nombre: String,
    val cuenta: String,
    val contrasena: String,
    val ciudad: String,
    val rut: String,
    val rolId: Long
)

data class ActualizarUsuarioRequest(
    val nombre: String,
    val cuenta: String,
    val ciudad: String,
    val rut: String,
    val rolId: Long
)

data class CambiarContrasenaRequest(
    val actual: String,
    val nueva: String
)

data class UsuarioResponse(
    val id: Long,
    val nombre: String,
    val cuenta: String,
    val ciudad: String,
    val rolId: Long,
    val rolNombre: String,
    val rut: String,
    val avatarUrl: String?
)

data class RolResponse(
    val id: Long,
    val nombre: String
)

fun UsuarioResponse.aUsuario() = Usuario(
    nombre = nombre,
    cuenta = cuenta,
    ciudad = ciudad,
    id = id,
    rolId = rolId,
    rolNombre = rolNombre,
    rut = rut
)
