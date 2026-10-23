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
import com.example.pagoflex.ui.components.GraficoBarras
import com.example.pagoflex.ui.components.TarjetaSeccion
import com.example.pagoflex.ui.theme.Dimens
import com.example.pagoflex.ui.theme.PagoFlexTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EjecutivoResumenScreen(
    onVolver: () -> Unit = {},
    onVerCartera: () -> Unit = {}
) {
    val datosBarras = listOf(
        DatoGrafico("Ene", 40),
        DatoGrafico("Feb", 65),
        DatoGrafico("Mar", 80),
        DatoGrafico("Abr", 55)
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Resumen Ejecutivo", fontWeight = FontWeight.Bold) }
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
            TarjetaSeccion(titulo = "Recaudación Mensual ($)") {
                GraficoBarras(
                    datos = datosBarras,
                    colorBarra = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            BotonPrincipal(texto = "Ver Cartera General", onClick = onVerCartera)
            BotonSecundario(texto = "Volver", onClick = onVolver)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EjecutivoResumenScreenPreview() {
    PagoFlexTheme { EjecutivoResumenScreen() }
}