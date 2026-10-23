package com.example.pagoflex.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
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
import com.example.pagoflex.ui.screens.UsuarioScreen

@Composable
fun AppNavegacion() {
    val navController = rememberNavController()
    var ultimoMontoSolicitud by rememberSaveable { mutableStateOf("") }
    var ultimoConceptoSolicitud by rememberSaveable { mutableStateOf("") }

    NavHost(navController = navController, startDestination = Ruta.Login.ruta) {
        composable(Ruta.Login.ruta) {
            UsuarioScreen { rol ->
                navController.navigate(Ruta.Inicio.crear(rol)) {
                    popUpTo(Ruta.Login.ruta) { inclusive = true }
                }
            }
        }

        composable(
            route = Ruta.Inicio.ruta,
            arguments = listOf(navArgument("rol") { type = NavType.StringType })
        ) { entrada ->
            val rol = entrada.arguments?.getString("rol") ?: "usuario"
            InicioScreen(
                rol = rol,
                onNavegarCompromisos = { navController.navigate(Ruta.Compromisos.ruta) },
                onNavegarHistorial = { navController.navigate(Ruta.HistorialPagos.ruta) },
                onNavegarFormulario = { navController.navigate(Ruta.Formulario.ruta) },
                onNavegarPerfil = { navController.navigate(Ruta.Perfil.ruta) },
                onNavegarAgente = { navController.navigate(Ruta.AgenteReportes.ruta) },
                onNavegarEjecutivo = { navController.navigate(Ruta.EjecutivoResumen.ruta) },
                onCerrarSesion = {
                    navController.navigate(Ruta.Login.ruta) {
                        popUpTo(Ruta.Inicio.ruta) { inclusive = true }
                    }
                }
            )
        }

        composable(Ruta.Perfil.ruta) {
            PerfilScreen(
                onVolver = { navController.navigateUp() },
                onIrConfiguraciones = { navController.navigate(Ruta.Configuraciones.ruta) }
            )
        }

        composable(Ruta.Configuraciones.ruta) {
            ConfiguracionesScreen(onVolver = { navController.navigateUp() })
        }

        composable(Ruta.Compromisos.ruta) {
            CompromisosScreen(
                onVolver = { navController.navigateUp() },
                onVerDetalle = { id -> navController.navigate(Ruta.DetalleCompromiso.crear(id)) },
                onIrAPagar = { navController.navigate(Ruta.Formulario.ruta) }
            )
        }

        composable(
            route = Ruta.DetalleCompromiso.ruta,
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { entrada ->
            val id = entrada.arguments?.getLong("id") ?: return@composable
            DetalleCompromisoScreen(
                compromisoId = id,
                onVolver = { navController.navigateUp() },
                onPagar = { navController.navigate(Ruta.Formulario.ruta) }
            )
        }

        composable(Ruta.HistorialPagos.ruta) {
            HistorialPagosScreen(
                onVolver = { navController.navigateUp() },
                montoSolicitudReciente = ultimoMontoSolicitud,
                conceptoSolicitudReciente = ultimoConceptoSolicitud
            )
        }

        composable(Ruta.Formulario.ruta) {
            FormularioScreen(
                onVolver = { navController.navigateUp() },
                onSolicitudRegistrada = { monto, concepto ->
                    ultimoMontoSolicitud = monto
                    ultimoConceptoSolicitud = concepto
                    navController.navigate(Ruta.HistorialPagos.ruta) {
                        popUpTo(Ruta.Inicio.ruta)
                    }
                }
            )
        }

        composable(Ruta.AgenteReportes.ruta) {
            AgenteReportesScreen(
                onVolver = { navController.navigateUp() },
                onVerRecomendaciones = { navController.navigate(Ruta.AgenteRecomendaciones.ruta) }
            )
        }

        composable(Ruta.AgenteRecomendaciones.ruta) {
            AgenteRecomendacionesScreen(onVolver = { navController.navigateUp() })
        }

        composable(Ruta.EjecutivoResumen.ruta) {
            EjecutivoResumenScreen(
                onVolver = { navController.navigateUp() },
                onVerCartera = { navController.navigate(Ruta.EjecutivoCartera.ruta) }
            )
        }

        composable(Ruta.EjecutivoCartera.ruta) {
            EjecutivoCarteraScreen(onVolver = { navController.navigateUp() })
        }
    }
}