package com.example.pagoflex.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    var nombreEditable by remember { mutableStateOf("Juan Pérez") }
    var comunaEditable by remember { mutableStateOf("Santiago") }
    var mensaje by remember { mutableStateOf("") }

    // Immutable / blocked fields based on role: ID interno, RUT, rol
    val idInterno = "ID-99482"
    val rutBloqueado = "12.345.678-9"
    val rolBloqueado = "Usuario final"

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
            AvatarCircular(iniciales = "JP")

            Text(text = nombreEditable, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(text = "juan.perez@pagoflex.cl", style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(Dimens.espacioChico))

            OutlinedTextField(
                value = nombreEditable,
                onValueChange = { if (it.all { char -> char.isLetter() || char.isWhitespace() }) nombreEditable = it },
                label = { Text("Nombre (Editable)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = comunaEditable,
                onValueChange = { comunaEditable = it },
                label = { Text("Comuna (Editable)") },
                modifier = Modifier.fillMaxWidth()
            )

            // Blocked / hidden info according to role
            TarjetaSeccion(titulo = "Información Protegida (Bloqueada)") {
                Text("ID Interno: $idInterno (No modificable)")
                Text("RUT: $rutBloqueado (No modificable)")
                Text("Rol: $rolBloqueado (No modificable)")
            }

            if (mensaje.isNotEmpty()) {
                Text(mensaje, color = MaterialTheme.colorScheme.primary)
            }

            Spacer(modifier = Modifier.height(Dimens.espacioChico))

            BotonPrincipal(
                texto = "Guardar Cambios",
                onClick = { mensaje = "Perfil actualizado correctamente." }
            )

            BotonSecundario(texto = "Volver", onClick = onVolver)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PerfilScreenPreview() {
    PagoFlexTheme { PerfilScreen() }
}