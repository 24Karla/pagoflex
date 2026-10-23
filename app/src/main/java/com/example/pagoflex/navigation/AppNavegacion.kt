package com.example.pagoflex.navigation

import androidx.compose.runtime.Composable
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

    NavHost(
        navController = navController,
        startDestination = Ruta.Login.ruta
    ) {
        // --- AUTENTICACIÓN E INICIO ---
        composable(Ruta.Login.ruta) {
            UsuarioScreen(
                onLoginExitoso = {
                    navController.navigate(Ruta.Inicio.ruta) {
                        popUpTo(Ruta.Login.ruta) { inclusive = true }
                    }
                }
            )
        }

        composable(Ruta.Inicio.ruta) {
            InicioScreen(
                onNavegarCompromisos = { navController.navigate(Ruta.Compromisos.ruta) },
                onNavegarHistorial = { navController.navigate(Ruta.HistorialPagos.ruta) },
                onNavegarFormulario = { navController.navigate(Ruta.Formulario.ruta) },
                onNavegarPerfil = { navController.navigate(Ruta.Perfil.ruta) },
                onNavegarAgente = { navController.navigate(Ruta.AgenteReportes.ruta) },
                onNavegarEjecutivo = { navController.navigate(Ruta.EjecutivoResumen.ruta) },
                onCerrarSesion = {
                    navController.navigate(Ruta.Login.ruta) {
                        popUpTo(0) { inclusive = true }
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
            ConfiguracionesScreen(
                onVolver = { navController.navigateUp() }
            )
        }

        // --- ROL USUARIO (R-01) ---
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
            val id = entrada.arguments?.getLong("id") ?: 0L
            DetalleCompromisoScreen(
                compromisoId = id,
                onVolver = { navController.navigateUp() },
                onPagar = { navController.navigate(Ruta.Formulario.ruta) }
            )
        }

        composable(Ruta.HistorialPagos.ruta) {
            HistorialPagosScreen(
                onVolver = { navController.navigateUp() }
            )
        }

        composable(Ruta.Formulario.ruta) {
            FormularioScreen(
                onVolver = { navController.navigateUp() },
                onPagoExitoso = {
                    navController.navigate(Ruta.HistorialPagos.ruta) {
                        popUpTo(Ruta.Inicio.ruta)
                    }
                }
            )
        }

        // --- ROL AGENTE (R-02) ---
        composable(Ruta.AgenteReportes.ruta) {
            AgenteReportesScreen(
                onVolver = { navController.navigateUp() },
                onVerRecomendaciones = { navController.navigate(Ruta.AgenteRecomendaciones.ruta) }
            )
        }

        composable(Ruta.AgenteRecomendaciones.ruta) {
            AgenteRecomendacionesScreen(
                onVolver = { navController.navigateUp() }
            )
        }

        // --- ROL EJECUTIVO (R-03) ---
        composable(Ruta.EjecutivoResumen.ruta) {
            EjecutivoResumenScreen(
                onVolver = { navController.navigateUp() },
                onVerCartera = { navController.navigate(Ruta.EjecutivoCartera.ruta) }
            )
        }

        composable(Ruta.EjecutivoCartera.ruta) {
            EjecutivoCarteraScreen(
                onVolver = { navController.navigateUp() }
            )
        }
    }
}