package com.example.pagoflex.viewmodel

import com.example.pagoflex.data.GestorFotos
import com.example.pagoflex.model.ResultadoOperacion
import android.app.Application
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pagoflex.data.PagoUsuario
import com.example.pagoflex.data.SesionPreferencias
import com.example.pagoflex.data.UsuarioDbHelper
import com.example.pagoflex.data.UsuarioRepository
import com.example.pagoflex.model.Rol
import com.example.pagoflex.model.Compromiso
import com.example.pagoflex.model.ComprobantePago
import com.example.pagoflex.model.ReporteIncidencia
import com.example.pagoflex.model.RecomendacionOrganizacion
import com.example.pagoflex.model.ResultadoPago
import com.example.pagoflex.model.Usuario
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class LoginUiState(
    val cuenta: String = "",
    val contrasena: String = "",
    val errorCuenta: String? = null,
    val errorContrasena: String? = null,
    val errorGeneral: String? = null,
    val iniciandoSesion: Boolean = false,
    val requierePermisoRed: Boolean = false,
    val loginExitoso: Boolean = false,
    val rolSeleccionado: String? = "usuario",
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
    val errorNombre: String? = null,
    val errorCuenta: String? = null,
    val errorCiudad: String? = null,
    val errorRut: String? = null,
    val errorContrasena: String? = null,
    val errorConfirmarContrasena: String? = null,
    val errorGeneral: String? = null,
    val registroExitoso: Boolean = false,
    val registrando: Boolean = false,
    val rolDestino: String = "usuario",
    val usuarioCreado: Usuario? = null
)

class UsuariosViewModel(application: Application) : AndroidViewModel(application) {
    private val usuarioRepository by lazy { UsuarioRepository() }
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

    fun actualizarNombre(nuevoNombre: String): ResultadoOperacion {
        val nombreLimpio = nuevoNombre.trim()
        if (nombreLimpio.isBlank() || !nombreLimpio.all { it.isLetter() || it.isWhitespace() }) {
            return ResultadoOperacion(false, "El nombre solo debe contener letras y espacios.")
        }
        val usuario = obtenerUsuarioSesion() ?: return ResultadoOperacion(false, "Inicia sesión para actualizar tu nombre.")
        val usuarioActualizado = usuario.copy(nombre = nombreLimpio)
        val filas = dbHelper?.actualizarUsuario(usuarioActualizado) ?: 0
        return if (filas > 0) ResultadoOperacion(true, "Nombre actualizado correctamente.") else ResultadoOperacion(false, "No se pudo actualizar el nombre.")
    }

    fun actualizarRutCuenta(valor: String): com.example.pagoflex.model.ResultadoOperacion {
        val rut = normalizarRutChile(valor)
            ?: return com.example.pagoflex.model.ResultadoOperacion(false, "Ingresa un RUT chileno válido (módulo 11).")
        val usuarioId = sesionPrefs?.leer() ?: -1L
        if (usuarioId <= 0L) return com.example.pagoflex.model.ResultadoOperacion(false, "Inicia sesión para actualizar tu RUT.")
        return dbHelper?.actualizarRutUsuario(usuarioId, rut)
            ?: com.example.pagoflex.model.ResultadoOperacion(false, "No se pudo abrir la base de datos.")
    }

    fun cambiarContrasena(actual: String, nueva: String): ResultadoOperacion {
        val usuario = obtenerUsuarioSesion() ?: return ResultadoOperacion(false, "Inicia sesión para cambiar tu contraseña.")
        if (dbHelper?.validarLogin(usuario.cuenta, actual, usuario.rolId) == null) return ResultadoOperacion(false, "La contraseña actual es incorrecta.")
        if (!contrasenaCumpleRequisitos(nueva)) return ResultadoOperacion(false, "La contraseña debe tener mínimo 8 caracteres, mayúscula, minúscula, número y carácter especial.")
        return if (dbHelper?.actualizarContrasena(usuario.id, nueva) == true) ResultadoOperacion(true, "Contraseña actualizada.") else ResultadoOperacion(false, "No se pudo actualizar la contraseña.")
    }

    fun recuperarContrasena(correo: String, rutIngresado: String, nueva: String): ResultadoOperacion {
        val correoValido = correo.trim().lowercase()
        val rut = normalizarRutChile(rutIngresado)
        if (!Patterns.EMAIL_ADDRESS.matcher(correoValido).matches() || rut == null || !contrasenaCumpleRequisitos(nueva)) {
            return ResultadoOperacion(false, "No se pudo completar la recuperación. Revisa los datos y requisitos de la contraseña.")
        }
        val db = dbHelper ?: return ResultadoOperacion(false, "No se pudo completar la recuperación. Revisa los datos y requisitos de la contraseña.")
        val usuario = db.buscarPorCuenta(correoValido)
        if (usuario == null || usuario.rut != rut) return ResultadoOperacion(false, "No se pudo completar la recuperación. Revisa los datos y requisitos de la contraseña.")
        return if (db.actualizarContrasena(usuario.id, nueva)) ResultadoOperacion(true, "Contraseña actualizada. Ya puedes iniciar sesión.") else ResultadoOperacion(false, "No se pudo completar la recuperación. Revisa los datos y requisitos de la contraseña.")
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

    fun eliminarCuentaActual(): Boolean {
        val usuario = obtenerUsuarioSesion() ?: return false
        val eliminado = dbHelper?.eliminarUsuario(usuario.id) ?: 0
        if (eliminado > 0) {
            sesionPrefs?.borrar()
            return true
        }
        return false
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

    fun informarPermisoLoginDenegado() {
        _loginUiState.value = _loginUiState.value.copy(
            requierePermisoRed = false,
            errorGeneral = "Permite el acceso a la red local para iniciar sesión con tu cuenta del servidor."
        )
    }

    fun iniciarSesion(puedeConsultarServidor: Boolean = true) {
        val estado = _loginUiState.value
        if (estado.iniciandoSesion) return
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
            return
        }

        _loginUiState.value = estado.copy(
            iniciandoSesion = true,
            requierePermisoRed = false,
            errorGeneral = null
        )
        viewModelScope.launch {
            try {
                val db = dbHelper ?: error("No se pudo abrir la base local de cuentas.")
                val prefs = sesionPrefs ?: error("No se pudo preparar la sesión local.")
                val usuarioLocal = withContext(Dispatchers.IO) {
                    db.validarLogin(cuenta, contrasena)
                        ?: run {
                            check(db.buscarPorCuenta(cuenta) == null) { "Correo o contraseña incorrectos" }
                            null
                        }
                }
                if (usuarioLocal == null && !puedeConsultarServidor) {
                    _loginUiState.value = _loginUiState.value.copy(
                        iniciandoSesion = false,
                        requierePermisoRed = true
                    )
                    return@launch
                }
                val usuario = usuarioLocal ?: run {
                    val remoto = usuarioRepository.iniciarSesion(cuenta, contrasena)
                    check(remoto.cuenta.equals(cuenta, ignoreCase = true) &&
                        remoto.rolId in Rol.ID_AGENTE..Rol.ID_USUARIO
                    ) { "El servidor devolvió datos de cuenta inválidos." }
                    withContext(Dispatchers.IO) {
                        val idLocal = db.crearCuenta(
                            remoto.nombre, remoto.cuenta, remoto.ciudad,
                            remoto.rut, contrasena, remoto.rolId
                        )
                        db.buscarPorId(idLocal) ?: error("No se pudo guardar la cuenta en este teléfono.")
                    }
                }
                prefs.guardar(usuario.id)
                _loginUiState.value = _loginUiState.value.copy(
                    iniciandoSesion = false,
                    loginExitoso = true,
                    rolDestino = Rol.rolIdARutaString(usuario.rolId),
                    usuarioLogueado = usuario.copy(contrasena = "", contrasenaSalt = ""),
                    contrasena = ""
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _loginUiState.value = _loginUiState.value.copy(
                    iniciandoSesion = false,
                    errorGeneral = e.message ?: "No se pudo iniciar sesión."
                )
            }
        }
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

    fun informarPermisoRedDenegado() {
        _registroUiState.value = _registroUiState.value.copy(
            errorGeneral = "Permite el acceso a la red local en los ajustes de Android para conectar con PagoFlex."
        )
    }

    fun consumirRegistroExitoso() {
        _registroUiState.value = RegistroUiState()
    }

    fun registrarCuenta() {
        val state = _registroUiState.value
        if (state.registrando || state.registroExitoso) return
        val nombre = state.nombre.trim()
        val cuenta = state.cuenta.trim().lowercase()
        val ciudad = state.ciudad.trim()
        val rut = normalizarRutChile(state.rut)
        val contrasena = state.contrasena.trim()
        val confirmar = state.confirmarContrasena.trim()

        val errNombre = when {
            nombre.isBlank() -> "El nombre es obligatorio"
            !nombre.matches(Regex("[\\p{L}]+(?: [\\p{L}]+)*")) -> "Usa solo letras y espacios en el nombre"
            else -> null
        }
        val errCuenta = when {
            cuenta.isBlank() -> "El correo es obligatorio"
            !Patterns.EMAIL_ADDRESS.matcher(cuenta).matches() -> "Ingresa un correo válido"
            else -> null
        }
        val errCiudad = when {
            ciudad.isBlank() -> "La comuna es obligatoria"
            else -> null
        }
        val errRut = if (rut == null) "Ingresa un RUT chileno válido (módulo 11)." else null
        val errContrasena = when {
            contrasena.isBlank() -> "Ingresa una contraseña"
            !contrasenaCumpleRequisitos(contrasena) -> "La contraseña debe tener mínimo 8 caracteres, mayúscula, minúscula, número y carácter especial"
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
                errorConfirmarContrasena = errConfirmar,
                errorGeneral = null
            )
            return
        }

        val nuevoUsuario = Usuario(
            nombre = nombre,
            cuenta = cuenta,
            ciudad = ciudad,
            rut = rut ?: return,
            contrasena = contrasena,
            rolId = Rol.rolIdParaRegistro(cuenta)
        )

        _registroUiState.value = state.copy(registrando = true, errorGeneral = null)
        viewModelScope.launch {
            var registroRemotoConfirmado = false
            try {
                val db = dbHelper ?: error("No se pudo abrir la base local de cuentas.")
                val prefs = sesionPrefs ?: error("No se pudo preparar la sesión local.")
                withContext(Dispatchers.IO) {
                    check(db.buscarPorCuenta(cuenta) == null) { "Ya existe una cuenta local con este correo." }
                    check(db.buscarPorRut(nuevoUsuario.rut) == null) { "Ya existe una cuenta local con ese RUT." }
                }
                val usuario = usuarioRepository.registrar(nuevoUsuario)
                registroRemotoConfirmado = true
                val idLocal = withContext(Dispatchers.IO) {
                    db.crearCuenta(
                        usuario.nombre, usuario.cuenta, usuario.ciudad,
                        usuario.rut, contrasena, usuario.rolId
                    )
                }
                check(idLocal > 0)
                prefs.guardar(idLocal)
                _registroUiState.value = _registroUiState.value.copy(
                    registrando = false,
                    registroExitoso = true,
                    rolDestino = Rol.rolIdARutaString(usuario.rolId),
                    usuarioCreado = usuario.copy(id = idLocal, contrasena = ""),
                    contrasena = "",
                    confirmarContrasena = ""
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                val mensaje = if (registroRemotoConfirmado) {
                    "La cuenta se guardó en el servidor, pero no se pudo preparar en este teléfono. Vuelve a intentar con los mismos datos."
                } else {
                    e.message ?: "No se pudo registrar la cuenta."
                }
                _registroUiState.value = _registroUiState.value.copy(registrando = false, errorGeneral = mensaje)
            }
        }
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

    fun crearReporte(compromisoId: Long?, tipo: String, descripcion: String, foto: java.io.File?): ResultadoOperacion {
        val userId = sesionPrefs?.leer() ?: return ResultadoOperacion(false, "Inicia sesión para enviar un reporte.")
        if (userId <= 0L) return ResultadoOperacion(false, "Inicia sesión para enviar un reporte.")
        val db = dbHelper ?: return ResultadoOperacion(false, "No se pudo abrir la base de datos.")

        val (reporteId, mensaje) = db.crearReporteDeUsuario(userId, compromisoId, tipo, descripcion)
        if (reporteId <= 0L) return ResultadoOperacion(false, mensaje)

        var textoFinal = "Reporte enviado. Un agente lo revisará pronto."
        if (foto != null && foto.exists()) {
            val adjuntada = try {
                foto.copyTo(GestorFotos.archivoReporte(getApplication(), reporteId), overwrite = true)
                true
            } catch (e: Exception) {
                false
            }
            foto.delete()
            if (!adjuntada) textoFinal = "Reporte enviado, pero no se pudo adjuntar la foto."
        }
        return ResultadoOperacion(true, textoFinal)
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

private fun contrasenaCumpleRequisitos(valor: String): Boolean =
    valor.length >= 8 && valor.any(Char::isUpperCase) && valor.any(Char::isLowerCase) &&
        valor.any(Char::isDigit) && valor.any { !it.isLetterOrDigit() }
