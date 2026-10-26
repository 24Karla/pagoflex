package com.example.pagoflex.viewmodel

import android.app.Application
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import com.example.pagoflex.data.PagoUsuario
import com.example.pagoflex.data.SesionPreferencias
import com.example.pagoflex.data.UsuarioDbHelper
import com.example.pagoflex.model.Rol
import com.example.pagoflex.model.Compromiso
import com.example.pagoflex.model.ComprobantePago
import com.example.pagoflex.model.ReporteIncidencia
import com.example.pagoflex.model.RecomendacionOrganizacion
import com.example.pagoflex.model.ResultadoPago
import com.example.pagoflex.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class LoginUiState(
    val cuenta: String = "",
    val contrasena: String = "",
    val errorCuenta: String? = null,
    val errorContrasena: String? = null,
    val errorGeneral: String? = null,
    val loginExitoso: Boolean = false,
    val rolSeleccionado: String? = null,
    val rolDestino: String = "usuario",
    val usuarioLogueado: Usuario? = null
)

data class RegistroUiState(
    val nombre: String = "",
    val cuenta: String = "",
    val ciudad: String = "",
    val rut: String = "",
    val contrasena: String = "",
    val confirmarContrasena: String = "",
    val rolId: Long = Rol.ID_USUARIO,
    val errorNombre: String? = null,
    val errorCuenta: String? = null,
    val errorCiudad: String? = null,
    val errorRut: String? = null,
    val errorContrasena: String? = null,
    val errorConfirmarContrasena: String? = null,
    val errorGeneral: String? = null,
    val registroExitoso: Boolean = false,
    val rolDestino: String = "usuario",
    val usuarioCreado: Usuario? = null
)

class UsuariosViewModel(application: Application) : AndroidViewModel(application) {
    private val dbHelper: UsuarioDbHelper? by lazy {
        try {
            UsuarioDbHelper(getApplication())
        } catch (e: Exception) {
            null
        }
    }
    private val sesionPrefs: SesionPreferencias? by lazy {
        try {
            SesionPreferencias(getApplication())
        } catch (e: Exception) {
            null
        }
    }

    private val _loginUiState = MutableStateFlow(LoginUiState())
    val loginUiState: StateFlow<LoginUiState> = _loginUiState.asStateFlow()

    private val _registroUiState = MutableStateFlow(RegistroUiState())
    val registroUiState: StateFlow<RegistroUiState> = _registroUiState.asStateFlow()

    fun actualizarRutCuenta(valor: String): com.example.pagoflex.model.ResultadoOperacion {
        val rut = normalizarRutChile(valor)
            ?: return com.example.pagoflex.model.ResultadoOperacion(false, "Ingresa un RUT chileno válido (módulo 11).")
        val usuarioId = sesionPrefs?.leer() ?: -1L
        if (usuarioId <= 0L) return com.example.pagoflex.model.ResultadoOperacion(false, "Inicia sesión para actualizar tu RUT.")
        return dbHelper?.actualizarRutUsuario(usuarioId, rut)
            ?: com.example.pagoflex.model.ResultadoOperacion(false, "No se pudo abrir la base de datos.")
    }
    fun marcarConsultaDatosLocales(): Long = sesionPrefs?.marcarConsultaLocal() ?: System.currentTimeMillis()

    fun comprobarSesionActiva(): Usuario? {
        val prefs = sesionPrefs ?: return null
        val db = dbHelper ?: return null
        val idSesion = prefs.leer()
        if (idSesion != -1L) {
            val usuario = db.buscarPorId(idSesion)
            if (usuario != null) {
                return usuario
            }
        }
        return null
    }

    fun obtenerUsuarioSesion(): Usuario? {
        val prefs = sesionPrefs ?: return null
        val db = dbHelper ?: return null
        val idSesion = prefs.leer()
        if (idSesion != -1L) {
            return db.buscarPorId(idSesion)
        }
        return null
    }

    fun onLoginCuentaCambia(valor: String) {
        _loginUiState.value = _loginUiState.value.copy(
            cuenta = valor,
            errorCuenta = null,
            errorGeneral = null
        )
    }

    fun onLoginContrasenaCambia(valor: String) {
        _loginUiState.value = _loginUiState.value.copy(
            contrasena = valor,
            errorContrasena = null,
            errorGeneral = null
        )
    }

    fun seleccionarRol(rol: String) {
        _loginUiState.value = _loginUiState.value.copy(
            rolSeleccionado = rol,
            rolDestino = rol,
            loginExitoso = false,
            errorGeneral = null,
            errorCuenta = null,
            errorContrasena = null
        )
    }

    fun iniciarSesion(): Boolean {
        val estado = _loginUiState.value
        val cuenta = estado.cuenta.trim().lowercase()
        val contrasena = estado.contrasena.trim()

        val errCuenta = when {
            cuenta.isBlank() -> "Ingresa tu correo"
            !Patterns.EMAIL_ADDRESS.matcher(cuenta).matches() -> "Ingresa un correo válido"
            else -> null
        }
        val errContrasena = if (contrasena.isBlank()) "Ingresa tu contraseña" else null

        if (errCuenta != null || errContrasena != null) {
            _loginUiState.value = estado.copy(
                errorCuenta = errCuenta,
                errorContrasena = errContrasena
            )
            return false
        }

        val rolSeleccionado = estado.rolSeleccionado
        if (rolSeleccionado == null) {
            _loginUiState.value = estado.copy(errorGeneral = "Primero selecciona tu tipo de usuario.")
            return false
        }

        val db = dbHelper
        if (db == null) {
            _loginUiState.value = estado.copy(errorGeneral = "No se pudo abrir la base local de cuentas.")
            return false
        }
        val cuentaEncontrada = db.buscarPorCuenta(cuenta)
        if (cuentaEncontrada == null) {
            _loginUiState.value = estado.copy(errorGeneral = "No existe una cuenta registrada con ese correo en este dispositivo.")
            return false
        }
        if (cuentaEncontrada.rolId != Rol.rutaStringARolId(rolSeleccionado)) {
            val rolRegistrado = Rol.rolIdARutaString(cuentaEncontrada.rolId)
            val nombreRol = when (rolRegistrado) {
                "agente" -> "Agente PagoFlex"
                "ejecutivo" -> "Ejecutivo de Empresa"
                else -> "Usuario Final"
            }
            _loginUiState.value = estado.copy(errorGeneral = "Esta cuenta está registrada como $nombreRol. Selecciona ese tipo de usuario.")
            return false
        }

        val usuario = db.validarLogin(cuenta, contrasena, cuentaEncontrada.rolId)
        if (usuario == null) {
            _loginUiState.value = estado.copy(errorGeneral = "La contraseña no coincide con esta cuenta.")
            return false
        }

        sesionPrefs?.guardar(usuario.id)
        val rolRuta = Rol.rolIdARutaString(usuario.rolId)

        _loginUiState.value = estado.copy(
            loginExitoso = true,
            rolDestino = rolRuta,
            usuarioLogueado = usuario,
            errorGeneral = null
        )
        return true
    }

    fun onRegistroNombreCambia(valor: String) {
        _registroUiState.value = _registroUiState.value.copy(nombre = valor, errorNombre = null)
    }

    fun onRegistroCuentaCambia(valor: String) {
        _registroUiState.value = _registroUiState.value.copy(cuenta = valor, errorCuenta = null)
    }

    fun onRegistroRutCambia(valor: String) {
        _registroUiState.value = _registroUiState.value.copy(rut = valor, errorRut = null)
    }

    fun onRegistroCiudadCambia(valor: String) {
        _registroUiState.value = _registroUiState.value.copy(ciudad = valor, errorCiudad = null)
    }

    fun onRegistroContrasenaCambia(valor: String) {
        _registroUiState.value = _registroUiState.value.copy(contrasena = valor, errorContrasena = null)
    }

    fun onRegistroConfirmarContrasenaCambia(valor: String) {
        _registroUiState.value = _registroUiState.value.copy(confirmarContrasena = valor, errorConfirmarContrasena = null)
    }

    fun onRegistroRolCambia(rolId: Long) {
        _registroUiState.value = _registroUiState.value.copy(rolId = rolId)
    }

    fun registrarCuenta(): Boolean {
        val state = _registroUiState.value
        val nombre = state.nombre.trim()
        val cuenta = state.cuenta.trim().lowercase()
        val ciudad = state.ciudad.trim()
        val rut = normalizarRutChile(state.rut)
        val contrasena = state.contrasena.trim()
        val confirmar = state.confirmarContrasena.trim()

        val errNombre = if (nombre.isBlank()) "El nombre es obligatorio" else null
        val errCuenta = when {
            cuenta.isBlank() -> "El correo es obligatorio"
            !Patterns.EMAIL_ADDRESS.matcher(cuenta).matches() -> "Ingresa un correo válido"
            else -> null
        }
        val errCiudad = if (ciudad.isBlank()) "La ciudad es obligatoria" else null
        val errRut = if (rut == null) "Ingresa un RUT chileno válido (módulo 11)." else null
        val errContrasena = when {
            contrasena.isBlank() -> "Ingresa una contraseña"
            contrasena.length < 4 -> "La contraseña debe tener al menos 4 caracteres"
            else -> null
        }
        val errConfirmar = if (confirmar != contrasena) "Las contraseñas no coinciden" else null

        if (errNombre != null || errCuenta != null || errCiudad != null || errRut != null || errContrasena != null || errConfirmar != null) {
            _registroUiState.value = state.copy(
                errorNombre = errNombre,
                errorCuenta = errCuenta,
                errorCiudad = errCiudad,
                errorRut = errRut,
                errorContrasena = errContrasena,
                errorConfirmarContrasena = errConfirmar
            )
            return false
        }

        if (dbHelper?.buscarPorCuenta(cuenta) != null) {
            _registroUiState.value = state.copy(
                errorCuenta = "Ya existe una cuenta registrada con este correo"
            )
            return false
        }

        if (rut == null) return false
        if (dbHelper?.buscarPorRut(rut) != null) {
            _registroUiState.value = state.copy(errorRut = "Ya existe una cuenta con ese RUT.")
            return false
        }

        val nuevoUsuario = Usuario(
            nombre = nombre,
            cuenta = cuenta,
            ciudad = ciudad,
            rut = rut,
            contrasena = contrasena,
            rolId = state.rolId
        )

        val idNuevo = dbHelper?.crearCuenta(nombre, cuenta, ciudad, rut, contrasena, state.rolId) ?: -1L
        if (idNuevo <= 0) {
            _registroUiState.value = state.copy(
                errorGeneral = "Error al guardar el usuario en la base de datos."
            )
            return false
        }

        sesionPrefs?.guardar(idNuevo)
        val rolRuta = Rol.rolIdARutaString(state.rolId)

        _registroUiState.value = state.copy(
            registroExitoso = true,
            rolDestino = rolRuta,
            usuarioCreado = nuevoUsuario.copy(id = idNuevo, contrasena = "")
        )
        return true
    }

    fun listarCompromisosDeUsuario(userId: Long): List<Compromiso> =
        if (userId > 0L) dbHelper?.listarCompromisosDeUsuario(userId).orEmpty() else emptyList()

    fun listarEmpresasDeEjecutivo(ejecutivoId: Long): List<com.example.pagoflex.model.Empresa> =
        if (ejecutivoId > 0L) dbHelper?.listarEmpresasDeCuenta(ejecutivoId).orEmpty().filter { it.ejecutivoId == ejecutivoId } else emptyList()

    fun actualizarContactoEmpresa(ejecutivoId: Long, empresaId: Long, telefono: String, correo: String): com.example.pagoflex.model.ResultadoOperacion =
        dbHelper?.actualizarContactoEmpresa(ejecutivoId, empresaId, telefono, correo)
            ?: com.example.pagoflex.model.ResultadoOperacion(false, "No se pudo abrir la base de datos.")

    fun listarCompromisosDeEjecutivo(ejecutivoId: Long): List<Compromiso> =
        if (ejecutivoId > 0L) dbHelper?.listarCompromisosDeEjecutivo(ejecutivoId).orEmpty() else emptyList()

    fun crearCompromisoDeEjecutivo(
        ejecutivoId: Long, empresaId: Long, cuentaCliente: String, concepto: String,
        cuotaActual: Int, cuotasTotales: Int, montoClp: Long, vencimientoMillis: Long
    ): com.example.pagoflex.model.ResultadoOperacion = dbHelper?.crearCompromisoDeEjecutivo(
        ejecutivoId, empresaId, cuentaCliente, concepto, cuotaActual, cuotasTotales, montoClp, vencimientoMillis
    ) ?: com.example.pagoflex.model.ResultadoOperacion(false, "No se pudo abrir la base de datos.")

    fun anularCompromisoDeEjecutivo(ejecutivoId: Long, compromisoId: Long, motivo: String): com.example.pagoflex.model.ResultadoOperacion =
        dbHelper?.anularCompromisoDeEjecutivo(ejecutivoId, compromisoId, motivo)
            ?: com.example.pagoflex.model.ResultadoOperacion(false, "No se pudo abrir la base de datos.")
    fun listarPagosDeCartera(ejecutivoId: Long, inicioMillis: Long, finMillis: Long): List<com.example.pagoflex.model.PagoCartera> =
        if (ejecutivoId > 0L) dbHelper?.listarPagosDeCartera(ejecutivoId, inicioMillis, finMillis).orEmpty() else emptyList()

    fun buscarCompromisoDeUsuario(userId: Long, compromisoId: Long): Compromiso? =
        if (userId > 0L && compromisoId > 0L) dbHelper?.buscarCompromisoDeUsuario(userId, compromisoId) else null

    fun pagarCompromiso(compromisoId: Long, canal: String = "App"): ResultadoPago {
        val userId = sesionPrefs?.leer() ?: return ResultadoPago(false, "Inicia sesión para realizar el pago.")
        if (userId <= 0L) return ResultadoPago(false, "Inicia sesión para realizar el pago.")
        return dbHelper?.pagarCompromiso(userId, compromisoId, canal)
            ?: ResultadoPago(false, "No se pudo abrir la base de datos.")
    }
    fun listarReportesDeAgente(agenteId: Long): List<ReporteIncidencia> =
        if (agenteId > 0L) dbHelper?.listarReportesDeAgente(agenteId).orEmpty() else emptyList()

    fun actualizarReporteDeAgente(agenteId: Long, reporteId: Long, nuevoEstado: String, respuesta: String? = null): Boolean =
        dbHelper?.actualizarReporteDeAgente(agenteId, reporteId, nuevoEstado, respuesta) ?: false

    fun listarRecomendacionesDeAgente(agenteId: Long): List<RecomendacionOrganizacion> =
        if (agenteId > 0L) dbHelper?.listarRecomendacionesDeAgente(agenteId).orEmpty() else emptyList()

    fun actualizarRecomendacionDeAgente(agenteId: Long, recomendacionId: Long, nuevoEstado: String): Boolean =
        dbHelper?.actualizarRecomendacionDeAgente(agenteId, recomendacionId, nuevoEstado) ?: false
    fun guardarSolicitudPago(monto: String, concepto: String): Boolean {
        val userId = sesionPrefs?.leer() ?: return false
        if (userId <= 0L) return false
        return (dbHelper?.guardarSolicitudPago(userId, monto, concepto) ?: -1L) > 0L
    }

    fun obtenerResumenCumplimiento(userId: Long): com.example.pagoflex.model.ResumenCumplimiento =
        dbHelper?.obtenerResumenCumplimiento(userId) ?: com.example.pagoflex.model.ResumenCumplimiento(0, 0, 0.0, "Historial insuficiente")

    fun listarComprobantesDeUsuario(userId: Long): List<ComprobantePago> {
        if (userId <= 0L) return emptyList()
        return dbHelper?.listarComprobantesDeUsuario(userId).orEmpty()
    }
    fun listarSolicitudesDeUsuario(userId: Long): List<PagoUsuario> {
        if (userId <= 0L) return emptyList()
        return dbHelper?.listarSolicitudesDeUsuario(userId).orEmpty()
    }

    fun cerrarSesion() {
        sesionPrefs?.borrar()
        _loginUiState.value = LoginUiState()
        _registroUiState.value = RegistroUiState()
    }
}

private fun normalizarRutChile(valor: String): String? {
    val limpio = valor.uppercase().filter { it.isDigit() || it == 'K' }
    if (limpio.length !in 2..9) return null
    val cuerpo = limpio.dropLast(1)
    val digitoIngresado = limpio.last()
    if (cuerpo.any { !it.isDigit() } || cuerpo.toLongOrNull() == null) return null
    var suma = 0
    var factor = 2
    cuerpo.reversed().forEach { digito ->
        suma += digito.digitToInt() * factor
        factor = if (factor == 7) 2 else factor + 1
    }
    val resto = 11 - (suma % 11)
    val esperado = when (resto) { 11 -> '0'; 10 -> 'K'; else -> ('0'.code + resto).toChar() }
    return if (digitoIngresado == esperado) "$cuerpo-$esperado" else null
}