package com.example.pagoflex.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Esquema de Colores - Modo Claro
private val LightColorScheme = lightColorScheme(
    primary = PagoFlexGreen,
    onPrimary = Color.White,
    primaryContainer = PagoFlexGreenContainer,
    onPrimaryContainer = PagoFlexGreenDark,

    secondary = PagoFlexGreenLight,
    onSecondary = Color.White,

    background = FondoGrisApp,
    onBackground = TextoPrincipal,

    surface = SuperficieBlanca,
    onSurface = TextoPrincipal,
    surfaceVariant = Color(0xFFEEEEEE),
    onSurfaceVariant = TextoSecundario,

    error = EstadoVencido,
    onError = Color.White,

    outline = Color(0xFFE0E0E0)
)

// Esquema de Colores - Modo Oscuro
private val DarkColorScheme = darkColorScheme(
    primary = PagoFlexGreenLight,
    onPrimary = Color.Black,
    primaryContainer = PagoFlexGreenDark,
    onPrimaryContainer = PagoFlexGreenContainer,

    secondary = PagoFlexGreen,
    onSecondary = Color.White,

    background = Color(0xFF121212),
    onBackground = Color(0xFFE1E1E1),

    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFE1E1E1),
    surfaceVariant = Color(0xFF2C2C2C),
    onSurfaceVariant = Color(0xFFA0A0A0),

    error = Color(0xFFCF6679),
    onError = Color.Black,

    outline = Color(0xFF3E3E3E)
)

@Composable
fun MiRegistroTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}