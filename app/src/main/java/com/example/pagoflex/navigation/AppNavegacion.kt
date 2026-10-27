package com.example.pagoflex.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.pagoflex.model.Rol
import com.example.pagoflex.ui.screens.AgenteRecomendacionesScreen
import com.example.pagoflex.ui.screens.AgenteReportesScreen
import com.example.pagoflex.ui.screens.CompromisosScreen
import com.example.pagoflex.ui.screens.ConfiguracionesScreen
import com.example.pagoflex.ui.screens.DetalleCompromisoScreen
import com.example.pagoflex.ui.screens.EjecutivoCarteraScreen
import com.example.pagoflex.ui.screens.EjecutivoResumenScreen
import com.example.pagoflex.ui.screens.FormularioScreen
import com.example.pagoflex.ui.screens.HistorialPagosScreen
import com.example.pagoflex.ui.screens.InicioScreen
import com.example.pagoflex.ui.screens.PerfilScreen
import com.example.pagoflex.ui.screens.RegistroScreen
import com.example.pagoflex.ui.screens.UsuarioScreen
import com.example.pagoflex.ui.theme.PagoFlexTheme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import com.example.pagoflex.viewmodel.UsuariosViewModel
import android.app.Application
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider

@Composable
fun AppNavegacion(
    usuariosViewModel: UsuariosViewModel = run {
        val context = LocalContext.current
        val app = (context.applicationContext as? Application) ?: Application()
        viewModel(
            factory = ViewModelProvider.AndroidViewModelFactory.getInstance(app)
        )
    }
) {
    val navController = rememberNavController()
    val usuarioSesionInicial = remember { usuariosViewModel.comprobarSesionActiva() }
    val destinoInicial = if (usuarioSesionInicial != null) {
        if (usuarioSesionInicial.rolId == Rol.ID_EJECUTIVO) Ruta.EjecutivoResumen.ruta else Ruta.Inicio.crear(Rol.rolIdARutaString(usuarioSesionInicial.rolId))
    } else {
        Ruta.Login.ruta
    }

    NavHost(navController = navController, startDestination = destinoInicial) {
        composable(Ruta.Login.ruta) {
            UsuarioScreen(
                usuariosViewModel = usuariosViewModel,
                onLoginExitoso = { rol ->
                    navController.navigate(if (rol == "ejecutivo") Ruta.EjecutivoResumen.ruta else Ruta.Inicio.crear(rol)) {
                        popUpTo(Ruta.Login.ruta) { inclusive = true }
                    }
                },
                onIrARegistro = { rol ->
                    usuariosViewModel.onRegistroRolCambia(Rol.rutaStringARolId(rol))
                    navController.navigate(Ruta.Registro.ruta)
                }
            )
        }

        composable(Ruta.Registro.ruta) {
            RegistroScreen(
                usuariosViewModel = usuariosViewModel,
                onVolver = { navController.navigateUp() },
                onRegistroExitoso = { rol ->
                    navController.navigate(if (rol == "ejecutivo") Ruta.EjecutivoResumen.ruta else Ruta.Inicio.crear(rol)) {
                        popUpTo(Ruta.Login.ruta) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Ruta.Inicio.ruta,
            arguments = listOf(navArgument("rol") { type = NavType.StringType })
        ) { entrada ->
            val rol = entrada.arguments?.getString("rol") ?: "usuario"
            val usuarioActual = usuariosViewModel.obtenerUsuarioSesion()
            val compromisosUsuario = usuarioActual?.let { usuariosViewModel.listarCompromisosDeUsuario(it.id) }.orEmpty()
            val cumplimientoUsuario = usuarioActual?.let { usuariosViewModel.obtenerResumenCumplimiento(it.id) }
            val consultaLocal = remember(rol, usuarioActual?.id) { usuariosViewModel.marcarConsultaDatosLocales() }
            val reportesAgente = if (rol == "agente") usuarioActual?.let { usuariosViewModel.listarReportesDeAgente(it.id) }.orEmpty() else emptyList()
            val reportesAbiertos = reportesAgente.count { it.estado == "Abierto" }
            val reportesEnGestion = reportesAgente.count { it.estado == "En gestión" }
            val reportesAtrasados = reportesAgente.count { it.estado == "Abierto" && diasHabilesTranscurridos(it.fechaCreacionMillis) > 2 }
            val inicioMes = Calendar.getInstance().apply { set(Calendar.DAY_OF_MONTH, 1); set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0) }.timeInMillis
            val inicioProximoMes = Calendar.getInstance().apply { set(Calendar.DAY_OF_MONTH, 1); set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0); add(Calendar.MONTH, 1) }.timeInMillis
            val activos = compromisosUsuario.filter { it.estado == "Pendiente" || it.estado == "Vencido" }
            val compromisosMes = activos.filter { it.vencimientoMillis >= inicioMes && it.vencimientoMillis < inicioProximoMes }
            val fechaSiguiente = activos.filter { it.vencimientoMillis >= System.currentTimeMillis() }.minByOrNull { it.vencimientoMillis }?.let { SimpleDateFormat("d MMM", Locale("es", "CL")).format(Date(it.vencimientoMillis)) }

            InicioScreen(
                rol = rol,
                nombreUsuario = usuarioActual?.nombre ?: "",
                totalPorPagarMes = compromisosMes.sumOf { it.montoClp + it.recargoClp },
                cantidadCompromisos = activos.size,
                compromisosVencidos = compromisosUsuario.count { it.estado == "Vencido" },
                cumplimiento = cumplimientoUsuario,
                consultaLocalMillis = consultaLocal,
                reconocimientos = buildList {
                    val puntuales = cumplimientoUsuario?.pagosATiempo ?: 0
                    if (puntuales >= 3) add("Constancia: $puntuales pagos puntuales en los últimos 12 meses")
                    if (puntuales >= 6) add("Buen hábito: al menos 6 pagos a tiempo")
                },
                proximoVencimiento = fechaSiguiente,
                reportesAbiertos = reportesAbiertos,
                reportesEnGestion = reportesEnGestion,
                reportesAtrasados = reportesAtrasados,
                onNavegarCompromisos = { navController.navigate(Ruta.Compromisos.ruta) },
                onNavegarHistorial = { navController.navigate(Ruta.HistorialPagos.ruta) },
                onNavegarPerfil = { navController.navigate(Ruta.Perfil.ruta) },
                onNavegarAgente = { navController.navigate(Ruta.AgenteReportes.ruta) },
                onNavegarRecomendaciones = { navController.navigate(Ruta.AgenteRecomendaciones.ruta) },
                onNavegarEjecutivo = { navController.navigate(Ruta.EjecutivoResumen.ruta) }
            )
        }

        composable(Ruta.Perfil.ruta) {
            PerfilScreen(
                usuariosViewModel = usuariosViewModel,
                onVolver = { navController.navigateUp() },
                onIrConfiguraciones = { navController.navigate(Ruta.Configuraciones.ruta) },
                onCerrarSesion = {
                    usuariosViewModel.cerrarSesion()
                    navController.navigate(Ruta.Login.ruta) {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                }
            )
        }

        composable(Ruta.Configuraciones.ruta) {
            ConfiguracionesScreen(onVolver = { navController.navigateUp() })
        }

        composable(Ruta.Compromisos.ruta) {
            val usuario = usuariosViewModel.obtenerUsuarioSesion()
            val compromisos = usuario?.let { usuariosViewModel.listarCompromisosDeUsuario(it.id) }.orEmpty()
            CompromisosScreen(
                userId = usuario?.id ?: 0L,
                compromisos = compromisos,
                onVolver = { navController.navigateUp() },
                onVerDetalle = { id -> navController.navigate(Ruta.DetalleCompromiso.crear(id)) }
            )
        }

        composable(
            route = Ruta.DetalleCompromiso.ruta,
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { entrada ->
            val id = entrada.arguments?.getLong("id") ?: return@composable
            val usuarioId = usuariosViewModel.obtenerUsuarioSesion()?.id ?: 0L
            val compromiso = usuariosViewModel.buscarCompromisoDeUsuario(usuarioId, id)
            DetalleCompromisoScreen(
                compromiso = compromiso,
                onVolver = { navController.navigateUp() },
                onPagar = { compromisoId, canal -> usuariosViewModel.pagarCompromiso(compromisoId, canal) },
                onVerHistorial = { navController.navigate(Ruta.HistorialPagos.ruta) }
            )
        }
        composable(Ruta.HistorialPagos.ruta) {
            val usuario = usuariosViewModel.obtenerUsuarioSesion()
            val pagos = remember(usuario?.id) {
                usuario?.let { usuariosViewModel.listarSolicitudesDeUsuario(it.id) }.orEmpty()
            }
            val comprobantes = remember(usuario?.id) {
                usuario?.let { usuariosViewModel.listarComprobantesDeUsuario(it.id) }.orEmpty()
            }
            HistorialPagosScreen(
                onVolver = { navController.navigateUp() },
                pagos = pagos,
                comprobantes = comprobantes
            )
        }

        composable(Ruta.Formulario.ruta) {
            FormularioScreen(
                onVolver = { navController.navigateUp() },
                onSolicitudRegistrada = { monto, concepto ->
                    val guardado = usuariosViewModel.guardarSolicitudPago(monto, concepto)
                    if (guardado) {
                        navController.navigate(Ruta.HistorialPagos.ruta) {
                            popUpTo(Ruta.Inicio.ruta)
                        }
                    }
                    guardado
                }
            )
        }

        composable(Ruta.AgenteReportes.ruta) {
            val agenteId = usuariosViewModel.obtenerUsuarioSesion()?.id ?: 0L
            val reportes = usuariosViewModel.listarReportesDeAgente(agenteId)
            AgenteReportesScreen(
                userId = agenteId,
                reportes = reportes,
                onCargarReportes = { usuariosViewModel.listarReportesDeAgente(agenteId) },
                onActualizarReporte = { id, estado, respuesta -> usuariosViewModel.actualizarReporteDeAgente(agenteId, id, estado, respuesta) },
                onVolver = { navController.navigateUp() },
                onVerRecomendaciones = { navController.navigate(Ruta.AgenteRecomendaciones.ruta) },
                onVerPerfil = { navController.navigate(Ruta.Perfil.ruta) }
            )
        }

        composable(Ruta.AgenteRecomendaciones.ruta) {
            val agenteId = usuariosViewModel.obtenerUsuarioSesion()?.id ?: 0L
            val recomendaciones = usuariosViewModel.listarRecomendacionesDeAgente(agenteId)
            AgenteRecomendacionesScreen(
                userId = agenteId,
                recomendaciones = recomendaciones,
                onCargarRecomendaciones = { usuariosViewModel.listarRecomendacionesDeAgente(agenteId) },
                onActualizarEstado = { id, estado -> usuariosViewModel.actualizarRecomendacionDeAgente(agenteId, id, estado) },
                onVolver = { navController.navigateUp() },
                onVerPerfil = { navController.navigate(Ruta.Perfil.ruta) }
            )
        }

        composable(Ruta.EjecutivoResumen.ruta) {
            val ejecutivoId = usuariosViewModel.obtenerUsuarioSesion()?.id ?: 0L
            val calendarioMes = Calendar.getInstance().apply {
                set(Calendar.DAY_OF_MONTH, 1)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val inicioMes = calendarioMes.timeInMillis
            calendarioMes.add(Calendar.MONTH, 1)
            val finMes = calendarioMes.timeInMillis
            val inicioReconocimientos = Calendar.getInstance().apply { timeInMillis = System.currentTimeMillis(); add(Calendar.MONTH, -12) }.timeInMillis
            val pagosPuntuales = usuariosViewModel.listarPagosDeCartera(ejecutivoId, inicioReconocimientos, System.currentTimeMillis() + 1L).filter { it.aTiempo }
            EjecutivoResumenScreen(
                empresas = usuariosViewModel.listarEmpresasDeEjecutivo(ejecutivoId),
                compromisos = usuariosViewModel.listarCompromisosDeEjecutivo(ejecutivoId),
                pagosMes = usuariosViewModel.listarPagosDeCartera(ejecutivoId, inicioMes, finMes),
                pagosPuntuales = pagosPuntuales,
                onVerPerfil = { navController.navigate(Ruta.Perfil.ruta) },
                onCargarEmpresas = { usuariosViewModel.listarEmpresasDeEjecutivo(ejecutivoId) },
                onActualizarContacto = { empresaId, telefono, correo -> usuariosViewModel.actualizarContactoEmpresa(ejecutivoId, empresaId, telefono, correo) },
                onVolver = { navController.navigateUp() },
                onVerCartera = { navController.navigate(Ruta.EjecutivoCartera.ruta) }
            )
        }
        composable(Ruta.EjecutivoCartera.ruta) {
            val ejecutivoId = usuariosViewModel.obtenerUsuarioSesion()?.id ?: 0L
            EjecutivoCarteraScreen(
                compromisos = usuariosViewModel.listarCompromisosDeEjecutivo(ejecutivoId),
                empresas = usuariosViewModel.listarEmpresasDeEjecutivo(ejecutivoId),
                onCargarCompromisos = { usuariosViewModel.listarCompromisosDeEjecutivo(ejecutivoId) },
                onCrearCompromiso = { empresaId, cuenta, concepto, cuota, cuotas, monto, vence ->
                    usuariosViewModel.crearCompromisoDeEjecutivo(ejecutivoId, empresaId, cuenta, concepto, cuota, cuotas, monto, vence)
                },
                onAnularCompromiso = { compromisoId, motivo ->
                    usuariosViewModel.anularCompromisoDeEjecutivo(ejecutivoId, compromisoId, motivo)
                },
                onVolver = { navController.navigateUp() }
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun AppNavegacionPreview() {
    PagoFlexTheme {
        AppNavegacion()
    }
}
private fun diasHabilesTranscurridos(fechaMillis: Long): Int {
    val inicio = Calendar.getInstance().apply {
        timeInMillis = fechaMillis
        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        add(Calendar.DAY_OF_MONTH, 1)
    }
    val hoy = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
    }
    var dias = 0
    while (inicio.before(hoy)) {
        if (inicio.get(Calendar.DAY_OF_WEEK) !in setOf(Calendar.SATURDAY, Calendar.SUNDAY)) dias++
        inicio.add(Calendar.DAY_OF_MONTH, 1)
    }
    return dias
}
