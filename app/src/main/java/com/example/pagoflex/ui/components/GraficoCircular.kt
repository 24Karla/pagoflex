package com.example.pagoflex.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pagoflex.model.DatoGrafico
import com.example.pagoflex.ui.theme.Dimens
import com.example.pagoflex.ui.theme.PagoFlexTheme

@Composable
fun GraficoCircular(
    datos: List<DatoGrafico>,
    colores: List<Color>,
    modifier: Modifier = Modifier
) {
    val colorSeparador = MaterialTheme.colorScheme.surface

    val progreso = remember(datos) { Animatable(0f) }
    LaunchedEffect(datos) {
        progreso.animateTo(1f, animationSpec = tween(durationMillis = 800))
    }

    Canvas(modifier = modifier.size(160.dp)) {
        val total = datos.sumOf { it.valor }.toFloat()
        if (total == 0f) return@Canvas

        var anguloInicio = -90f

        datos.forEachIndexed { indice, dato ->
            val barrido = (dato.valor / total) * 360f * progreso.value

            drawArc(
                color = colores[indice % colores.size],
                startAngle = anguloInicio,
                sweepAngle = barrido,
                useCenter = true
            )
            drawArc(
                color = colorSeparador,
                startAngle = anguloInicio,
                sweepAngle = barrido,
                useCenter = true,
                style = Stroke(width = 2.dp.toPx())
            )

            anguloInicio += barrido
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun GraficoCircularPreview() {
    PagoFlexTheme {
        GraficoCircular(
            datos = listOf(
                DatoGrafico("Pagadas", 142),
                DatoGrafico("Pendientes", 24)
            ),
            colores = listOf(
                MaterialTheme.colorScheme.primary,
                MaterialTheme.colorScheme.error
            )
        )
    }
}