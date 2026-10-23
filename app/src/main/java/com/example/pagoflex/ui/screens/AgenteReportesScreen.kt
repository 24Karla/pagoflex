package com.example.pagoflex.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.pagoflex.model.DatoGrafico
import com.example.pagoflex.ui.components.BotonPrincipal
import com.example.pagoflex.ui.components.BotonSecundario
import com.example.pagoflex.ui.components.GraficoCircular
import com.example.pagoflex.ui.components.LeyendaGrafico
import com.example.pagoflex.ui.components.TarjetaSeccion
import com.example.pagoflex.ui.theme.Dimens
import com.example.pagoflex.ui.theme.PagoFlexTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgenteReportesScreen(
    onVolver: () -> Unit = {},
    onVerRecomendaciones: () -> Unit = {}
) {
    val datosGrafico = listOf(
        DatoGrafico("Cobradas", 120),
        DatoGrafico("Pendientes", 40)
    )
    val colores = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.error
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Reportes de Agente", fontWeight = FontWeight.Bold) }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(Dimens.espacioPantalla),
            verticalArrangement = Arrangement.spacedBy(Dimens.espacioMedio)
        ) {
            TarjetaSeccion(titulo = "Resumen de Gestiones de Cobranza") {
                GraficoCircular(datos = datosGrafico, colores = colores)
                LeyendaGrafico(datos = datosGrafico, colores = colores)
            }

            Spacer(modifier = Modifier.weight(1f))

            BotonPrincipal(texto = "Ver Recomendaciones", onClick = onVerRecomendaciones)
            BotonSecundario(texto = "Volver", onClick = onVolver)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AgenteReportesScreenPreview() {
    PagoFlexTheme { AgenteReportesScreen() }
}