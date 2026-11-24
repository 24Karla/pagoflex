package com.example.pagoflex.data

import com.example.pagoflex.data.remote.ActualizarUsuarioRequest
import com.example.pagoflex.data.remote.CambiarContrasenaRequest
import com.example.pagoflex.data.remote.LoginRequest
import com.example.pagoflex.data.remote.RegistroRequest
import com.example.pagoflex.data.remote.RetrofitClient
import com.example.pagoflex.data.remote.RolApi
import com.example.pagoflex.data.remote.UsuarioApi
import com.example.pagoflex.data.remote.aUsuario
import com.example.pagoflex.model.Rol
import com.example.pagoflex.model.Usuario
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException

class UsuarioRepository(
    private val usuarioApi: UsuarioApi = RetrofitClient.usuarioApi,
    private val rolApi: RolApi = RetrofitClient.rolApi
) {

    suspend fun iniciarSesion(cuenta: String, contrasena: String): Usuario =
        llamar { usuarioApi.iniciarSesion(LoginRequest(cuenta, contrasena)) }.aUsuario()

    suspend fun registrar(usuario: Usuario): Usuario {
        val datos = RegistroRequest(
            nombre = usuario.nombre,
            cuenta = usuario.cuenta,
            contrasena = usuario.contrasena,
            ciudad = usuario.ciudad,
            rut = usuario.rut,
            rolId = usuario.rolId
        )
        return llamar {
            try {
                usuarioApi.registrar(datos).aUsuario()
            } catch (conflicto: HttpException) {
                if (conflicto.code() != 409) throw conflicto
                // Un intento anterior pudo guardarse aunque el teléfono no recibiera la respuesta.
                val existente = try {
                    usuarioApi.iniciarSesion(LoginRequest(usuario.cuenta, usuario.contrasena)).aUsuario()
                } catch (e: HttpException) {
                    if (e.code() == 401 || e.code() == 404) throw conflicto
                    throw e
                }
                if (existente.rut != usuario.rut || existente.rolId != usuario.rolId) throw conflicto
                existente
            }
        }
    }

    suspend fun obtener(id: Long): Usuario =
        llamar { usuarioApi.obtener(id) }.aUsuario()

    suspend fun listar(buscar: String): List<Usuario> =
        llamar { usuarioApi.listar(buscar.ifBlank { null }) }.map { it.aUsuario() }

    suspend fun actualizar(usuario: Usuario): Usuario {
        val datos = ActualizarUsuarioRequest(
            nombre = usuario.nombre,
            cuenta = usuario.cuenta,
            ciudad = usuario.ciudad,
            rut = usuario.rut,
            rolId = usuario.rolId
        )
        return llamar { usuarioApi.actualizar(usuario.id, datos) }.aUsuario()
    }

    suspend fun cambiarContrasena(id: Long, actual: String, nueva: String) =
        llamar { usuarioApi.cambiarContrasena(id, CambiarContrasenaRequest(actual, nueva)) }

    suspend fun eliminar(id: Long) =
        llamar { usuarioApi.eliminar(id) }

    suspend fun listarRoles(): List<Rol> =
        llamar { rolApi.listar() }.map { Rol(id = it.id, nombre = it.nombre) }

    private suspend fun <T> llamar(peticion: suspend () -> T): T = try {
        peticion()
    } catch (e: HttpException) {
        throw Exception(mensajeDelServidor(e))
    } catch (e: IOException) {
        throw Exception("No se pudo conectar con el servidor")
    }

    private fun mensajeDelServidor(e: HttpException): String {
        val cuerpo = e.response()?.errorBody()?.string()
        val mensaje = try {
            JSONObject(cuerpo ?: "").optString("message")
        } catch (ex: Exception) {
            ""
        }
        return mensaje.ifBlank { "Error del servidor (${e.code()})" }
    }
}
