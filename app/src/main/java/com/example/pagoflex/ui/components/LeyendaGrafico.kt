package com.example.pagoflex.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.pagoflex.model.DatoGrafico
import com.example.pagoflex.ui.theme.Dimens
import com.example.pagoflex.ui.theme.PagoFlexTheme

@Composable
fun LeyendaGrafico(
    datos: List<DatoGrafico>,
    colores: List<Color>,
    modifier: Modifier = Modifier
) {
    val total = datos.sumOf { it.valor }.coerceAtLeast(1)

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Dimens.espacioChico)
    ) {
        datos.forEachIndexed { indice, dato ->
            val porcentaje = dato.valor * 100f / total
            Row(verticalAlignment = Alignment.CenterVertically) {
                Spacer(
                    modifier = Modifier
                        .size(Dimens.espacioMedio)
                        .background(colores[indice % colores.size], CircleShape)
                )
                Spacer(modifier = Modifier.width(Dimens.espacioChico))
                Text(
                    text = "${dato.etiqueta}: ${dato.valor} (%.1f%%)".format(porcentaje),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LeyendaGraficoPreview() {
    PagoFlexTheme {
        LeyendaGrafico(
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