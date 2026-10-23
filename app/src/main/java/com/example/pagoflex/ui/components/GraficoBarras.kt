package com.example.pagoflex.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.pagoflex.model.DatoGrafico

@Composable
fun GraficoBarras(
    datos: List<DatoGrafico> = emptyList(),
    modifier: Modifier = Modifier,
    colorBarra: Color = MaterialTheme.colorScheme.primary
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
    ) {
        if (datos.isEmpty()) return@Canvas
        val maxValor = (datos.maxOfOrNull { it.valor } ?: 1).toFloat()
        val anchoEspacio = size.width / datos.size
        val anchoBarra = anchoEspacio * 0.5f

        datos.forEachIndexed { indice, dato ->
            val altoBarra = (dato.valor / maxValor) * (size.height * 0.8f)
            val x = (indice * anchoEspacio) + (anchoEspacio - anchoBarra) / 2
            val y = size.height - altoBarra

            drawRoundRect(
                color = colorBarra,
                topLeft = Offset(x, y),
                size = Size(anchoBarra, altoBarra),
                cornerRadius = CornerRadius(8f, 8f)
            )
        }
    }
}