package com.example.pagoflex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.pagoflex.navigation.AppNavegacion
import com.example.pagoflex.notificaciones.AvisosVencimiento
import com.example.pagoflex.ui.theme.PagoFlexTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        AvisosVencimiento.crearCanal(this)
        AvisosVencimiento.programar(this)

        setContent {
            PagoFlexTheme {
                AppNavegacion()
            }
        }
    }
}
