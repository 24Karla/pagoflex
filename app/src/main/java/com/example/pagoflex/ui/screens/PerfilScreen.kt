package com.example.pagoflex.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.pagoflex.ui.components.AvatarCircular
import com.example.pagoflex.ui.components.BotonPrincipal
import com.example.pagoflex.ui.components.BotonSecundario
import com.example.pagoflex.ui.components.TarjetaSeccion
import com.example.pagoflex.ui.theme.Dimens
import com.example.pagoflex.ui.theme.PagoFlexTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    onVolver: () -> Unit = {},
    onIrConfiguraciones: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Mi Perfil", fontWeight = FontWeight.Bold) }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(Dimens.espacioPantalla),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimens.espacioMedio)
        ) {
            // Uso correcto de AvatarCircular con 'iniciales'
            AvatarCircular(iniciales = "PF")

            Text(text = "Juan Pérez", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(text = "juan.perez@pagoflex.cl", style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(Dimens.espacioMedio))

            TarjetaSeccion(titulo = "Información Personal") {
                Text("RUT: 12.345.678-9")
                Text("Teléfono: +56 9 8765 4321")
                Text("Dirección: Av. Siempreviva 123")
            }

            Spacer(modifier = Modifier.weight(1f))

            BotonPrincipal(texto = "Configuraciones", onClick = onIrConfiguraciones)
            BotonSecundario(texto = "Volver", onClick = onVolver)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PerfilScreenPreview() {
    PagoFlexTheme { PerfilScreen() }
}