package com.example.pagoflex.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val ColoresClaros = lightColorScheme(
    primary = PagoFlexGreen,
    onPrimary = SuperficieBlanca,
    background = FondoGrisApp,
    onBackground = TextoPrincipal,
    surface = SuperficieBlanca,
    onSurface = TextoPrincipal,
    secondary = PagoFlexGreenLight,
    onSecondary = SuperficieBlanca
)

private val ColoresOscuros = darkColorScheme(
    primary = PagoFlexGreenLight,
    onPrimary = TextoPrincipal,
    background = TextoPrincipal,
    onBackground = SuperficieBlanca,
    surface = TextoPrincipal,
    onSurface = SuperficieBlanca,
    secondary = PagoFlexGreen,
    onSecondary = SuperficieBlanca
)

@Composable
fun PagoFlexTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) ColoresOscuros else ColoresClaros,
        typography = Tipografia,
        content = content
    )
}