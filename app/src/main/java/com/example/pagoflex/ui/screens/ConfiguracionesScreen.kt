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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.pagoflex.ui.components.BotonSecundario
import com.example.pagoflex.ui.components.ControlesSeleccion
import com.example.pagoflex.ui.components.TarjetaSeccion
import com.example.pagoflex.ui.theme.Dimens
import com.example.pagoflex.ui.theme.PagoFlexTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfiguracionesScreen(
    onVolver: () -> Unit = {}
) {
    var notificaciones by remember { mutableStateOf(true) }
    var modoOscuro by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Configuraciones", fontWeight = FontWeight.Bold) }
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
            TarjetaSeccion(titulo = "Preferencias de la App") {
                ControlesSeleccion(
                    etiqueta = "Notificaciones Push",
                    seleccionado = notificaciones,
                    onSelecciónCambiada = { notificaciones = it }
                )
                ControlesSeleccion(
                    etiqueta = "Modo Oscuro",
                    seleccionado = modoOscuro,
                    onSelecciónCambiada = { modoOscuro = it }
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            BotonSecundario(texto = "Volver", onClick = onVolver)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ConfiguracionesScreenPreview() {
    PagoFlexTheme { ConfiguracionesScreen() }
}