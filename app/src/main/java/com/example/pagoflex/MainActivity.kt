package com.example.pagoflex

import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.DisposableEffect
import com.example.pagoflex.navigation.AppNavegacion
import com.example.pagoflex.notificaciones.AvisosVencimiento
import com.example.pagoflex.ui.theme.PagoFlexTheme
import com.example.pagoflex.ui.theme.PreferenciasTema

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AvisosVencimiento.crearCanal(this)
        AvisosVencimiento.programar(this)

        val sistemaOscuro = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) ==
                Configuration.UI_MODE_NIGHT_YES
        PreferenciasTema.iniciar(this, sistemaOscuro)

        setContent {
            val oscuro = PreferenciasTema.modoOscuro

            // Ajusta los íconos de la barra de estado y navegación al tema de la app
            // (si no, con el teléfono en claro y la app en oscuro, no se leerían).
            DisposableEffect(oscuro) {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT) { oscuro },
                    navigationBarStyle = SystemBarStyle.auto(
                        Color.argb(0xe6, 0xFF, 0xFF, 0xFF),
                        Color.argb(0x80, 0x1b, 0x1b, 0x1b)
                    ) { oscuro }
                )
                onDispose {}
            }

            PagoFlexTheme(darkTheme = oscuro) {
                AppNavegacion()
            }
        }
    }
}