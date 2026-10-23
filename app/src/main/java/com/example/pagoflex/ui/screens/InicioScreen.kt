package com.example.pagoflex.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.pagoflex.ui.components.BotonPrincipal
import com.example.pagoflex.ui.components.BotonSecundario
import com.example.pagoflex.ui.components.TarjetaSeccion
import com.example.pagoflex.ui.theme.Dimens
import com.example.pagoflex.ui.theme.PagoFlexTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InicioScreen(
    rol: String = "usuario",
    onNavegarCompromisos: () -> Unit = {},
    onNavegarHistorial: () -> Unit = {},
    onNavegarFormulario: () -> Unit = {},
    onNavegarPerfil: () -> Unit = {},
    onNavegarAgente: () -> Unit = {},
    onNavegarEjecutivo: () -> Unit = {},
    onCerrarSesion: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("PagoFlex · ${rol.replaceFirstChar { it.uppercase() }}", fontWeight = FontWeight.Bold) }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(Dimens.espacioPantalla)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(Dimens.espacioMedio)
        ) {
            if (rol == "usuario") {
                Text("Hola, Jesús", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("Aquí van tus pagos de este mes", color = MaterialTheme.colorScheme.onSurfaceVariant)
                TarjetaSeccion(titulo = "Total por pagar este mes") {
                    Text("\$120.690", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    Text("Próximo vencimiento: 15 de octubre")
                }
            }
            when (rol) {
                "agente" -> TarjetaSeccion(titulo = "Agente PagoFlex") {
                    BotonPrincipal(texto = "Reportes de cobranza", onClick = onNavegarAgente)
                }
                "ejecutivo" -> TarjetaSeccion(titulo = "Ejecutivo de Empresa") {
                    BotonPrincipal(texto = "Resumen ejecutivo", onClick = onNavegarEjecutivo)
                }
                else -> TarjetaSeccion(titulo = "Compromisos") {
                    BotonPrincipal(texto = "Mis Compromisos", onClick = onNavegarCompromisos)
                    BotonPrincipal(texto = "Registrar Pago", onClick = onNavegarFormulario)
                    BotonPrincipal(texto = "Historial de Pagos", onClick = onNavegarHistorial)
                    BotonSecundario(texto = "Mi Perfil", onClick = onNavegarPerfil)
                }
            }
            BotonSecundario(texto = "Cerrar Sesión", onClick = onCerrarSesion)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun InicioScreenPreview() {
    PagoFlexTheme { InicioScreen() }
}
