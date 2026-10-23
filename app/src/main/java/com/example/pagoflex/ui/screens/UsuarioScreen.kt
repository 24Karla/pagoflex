package com.example.pagoflex.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.example.pagoflex.ui.components.BotonSecundario
import com.example.pagoflex.ui.theme.Dimens
import com.example.pagoflex.ui.theme.PagoFlexTheme

@Composable
fun UsuarioScreen(
    onLoginExitoso: (String) -> Unit = {}
) {
    Scaffold(containerColor = MaterialTheme.colorScheme.background) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(Dimens.espacioPantalla),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "PagoFlex",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(Dimens.espacioChico))
            Text(
                text = "¿Cómo quieres ingresar hoy?",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(Dimens.espacioGrande))
            BotonSecundario(
                texto = "Soy Usuario",
                onClick = { onLoginExitoso("usuario") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(Dimens.espacioChico))
            BotonSecundario(
                texto = "Soy agente PagoFlex",
                onClick = { onLoginExitoso("agente") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(Dimens.espacioChico))
            BotonSecundario(
                texto = "Soy Ejecutivo de Empresa",
                onClick = { onLoginExitoso("ejecutivo") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun UsuarioScreenPreview() {
    PagoFlexTheme { UsuarioScreen() }
}
