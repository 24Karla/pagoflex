package com.example.pagoflex.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

val PrimarioClaro = Color(0xFF006C4C)
val FondoClaro = Color(0xFFF6FBF4)
val PrimarioSuave = Color(0xFFBDF2D5)
val TextoClaro = Color(0xFF191C1E)

val PrimarioOscuro = Color(0xFF6CDBAC)
val FondoOscuro = Color(0xFF0F1412)
val PrimarioProfundo = Color(0xFF005138)
val TextoOscuro = Color(0xFFE1E3DF)

val Blanco = Color(0xFFFFFFFF)

private val ColoresOscuros = darkColorScheme(
    primary = PrimarioOscuro,
    background = FondoOscuro,
    primaryContainer = PrimarioProfundo,
    onBackground = TextoOscuro,
    onPrimary = Blanco,
    onTertiary = Blanco
)

private val ColoresClaros = lightColorScheme(
    primary = PrimarioClaro,
    background = FondoClaro,
    primaryContainer = PrimarioSuave,
    onBackground = TextoClaro,
    onSecondary = Blanco,
    onTertiary = Blanco
)

private val Formas = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(20.dp)
)

@Composable
fun PagoFlexTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Cambiado a false para priorizar tus colores corporativos
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> ColoresOscuros
        else -> ColoresClaros
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        shapes = Formas,
        content = content
    )
}