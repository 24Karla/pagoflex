package com.example.pagoflex.ui.styles

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.example.pagoflex.ui.theme.Dimens

// Extension Function para fijar el alto del botón según Dimens
fun Modifier.estiloAltoBoton(): Modifier = this.height(Dimens.alturaBoton)

object EstilosBoton {
    val forma: Shape = RoundedCornerShape(Dimens.radioBoton)
    val relleno = PaddingValues(horizontal = Dimens.espacioMedio, vertical = Dimens.espacioChico)

    @Composable
    fun coloresPrincipal(): ButtonColors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
        disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
    )

    @Composable
    fun coloresSecundario(): ButtonColors = ButtonDefaults.outlinedButtonColors(
        contentColor = MaterialTheme.colorScheme.primary
    )

    @Composable
    fun bordeSecundario(): BorderStroke = BorderStroke(
        width = Dimens.bordeBoton,
        color = MaterialTheme.colorScheme.primary
    )

    @Composable
    fun elevacion(): ButtonElevation = ButtonDefaults.buttonElevation(
        defaultElevation = Dimens.elevacionBoton,
        pressedElevation = 1.dp
    )
}