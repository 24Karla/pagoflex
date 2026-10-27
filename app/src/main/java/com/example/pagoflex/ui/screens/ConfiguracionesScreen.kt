package com.example.pagoflex.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import com.example.pagoflex.notificaciones.AvisosVencimiento
import com.example.pagoflex.notificaciones.PreferenciasAvisos
import com.example.pagoflex.ui.components.BotonSecundario
import com.example.pagoflex.ui.components.ControlesSeleccion
import com.example.pagoflex.ui.components.TarjetaSeccion
import com.example.pagoflex.ui.theme.Dimens
import com.example.pagoflex.ui.theme.PagoFlexTheme
import com.example.pagoflex.ui.theme.PreferenciasTema

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfiguracionesScreen(
    onVolver: () -> Unit = {}
) {
    val context = LocalContext.current
    var notificaciones by remember { mutableStateOf(PreferenciasAvisos.activas(context)) }
    var diasAviso by remember { mutableStateOf(PreferenciasAvisos.diasAntes(context)) }
    var mensajeAvisos by remember { mutableStateOf<String?>(null) }

    val permisoNotificaciones = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) {
            notificaciones = true
            PreferenciasAvisos.guardarActivas(context, true)
            AvisosVencimiento.programar(context)
            mensajeAvisos = null
        } else {
            notificaciones = false
            PreferenciasAvisos.guardarActivas(context, false)
            AvisosVencimiento.cancelar(context)
            mensajeAvisos = "Permite las notificaciones de PagoFlex en los ajustes del dispositivo para recibir avisos."
        }
    }

    fun cambiarNotificaciones(activar: Boolean) {
        mensajeAvisos = null
        if (!activar) {
            notificaciones = false
            PreferenciasAvisos.guardarActivas(context, false)
            AvisosVencimiento.cancelar(context)
            return
        }
        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            permisoNotificaciones.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            notificaciones = true
            PreferenciasAvisos.guardarActivas(context, true)
            AvisosVencimiento.programar(context)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Configuraciones", fontWeight = FontWeight.Bold) })
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(Dimens.espacioPantalla),
            verticalArrangement = Arrangement.spacedBy(Dimens.espacioMedio)
        ) {
            TarjetaSeccion(titulo = "Preferencias de la App") {
                ControlesSeleccion(
                    etiqueta = "Avisos de vencimiento",
                    seleccionado = notificaciones,
                    onSelecciónCambiada = ::cambiarNotificaciones
                )
                if (notificaciones) {
                    Text("Avisarme:", fontWeight = FontWeight.SemiBold)
                    Row(
                        Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(Dimens.espacioChico)
                    ) {
                        listOf(7 to "7 días", 3 to "3 días", 1 to "1 día", 0 to "El mismo día").forEach { (dias, etiqueta) ->
                            FilterChip(
                                selected = dias in diasAviso,
                                onClick = {
                                    val nuevos = if (dias in diasAviso) diasAviso - dias else diasAviso + dias
                                    if (nuevos.isNotEmpty()) {
                                        diasAviso = nuevos
                                        PreferenciasAvisos.guardarDiasAntes(context, nuevos)
                                    } else {
                                        mensajeAvisos = "Deja al menos un plazo seleccionado."
                                    }
                                },
                                label = { Text(etiqueta) }
                            )
                        }
                    }
                    Text(
                        "Los avisos se revisan a las 9:00 y se silencian entre las 22:00 y las 8:00.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                mensajeAvisos?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                ControlesSeleccion(
                    etiqueta = "Modo Oscuro",
                    seleccionado = PreferenciasTema.modoOscuro,
                    onSelecciónCambiada = { PreferenciasTema.cambiar(context, it) }
                )
            }

            Spacer(modifier = Modifier.weight(1f))
            BotonSecundario(texto = "Volver", onClick = onVolver)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ConfiguracionesScreenPreview() {
    PagoFlexTheme { ConfiguracionesScreen() }
}