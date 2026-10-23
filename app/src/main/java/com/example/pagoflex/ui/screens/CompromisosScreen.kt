package com.example.pagoflex.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.pagoflex.ui.components.BotonPrincipal
import com.example.pagoflex.ui.components.BotonSecundario
import com.example.pagoflex.ui.components.TarjetaSeccion
import com.example.pagoflex.ui.theme.Dimens
import com.example.pagoflex.ui.theme.PagoFlexTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompromisosScreen(
    onVolver: () -> Unit = {},
    onVerDetalle: (Long) -> Unit = {},
    onIrAPagar: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Mis Compromisos", fontWeight = FontWeight.Bold) }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(Dimens.espacioPantalla)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(Dimens.espacioMedio)
        ) {
            TarjetaSeccion(titulo = "Cuota #1 - Crédito Consumo") {
                Text("Monto: $45.000")
                Text("Vencimiento: 15/10/2026")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Dimens.espacioChico)
                ) {
                    BotonSecundario(texto = "Ver Detalle", onClick = { onVerDetalle(1L) }, modifier = Modifier.weight(1f))
                    BotonPrincipal(texto = "Pagar", onClick = onIrAPagar, modifier = Modifier.weight(1f))
                }
            }

            TarjetaSeccion(titulo = "Cuota #2 - Tarjeta PagoFlex") {
                Text("Monto: $28.500")
                Text("Vencimiento: 30/10/2026")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Dimens.espacioChico)
                ) {
                    BotonSecundario(texto = "Ver Detalle", onClick = { onVerDetalle(2L) }, modifier = Modifier.weight(1f))
                    BotonPrincipal(texto = "Pagar", onClick = onIrAPagar, modifier = Modifier.weight(1f))
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            BotonSecundario(texto = "Volver", onClick = onVolver)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CompromisosScreenPreview() {
    PagoFlexTheme { CompromisosScreen() }
}