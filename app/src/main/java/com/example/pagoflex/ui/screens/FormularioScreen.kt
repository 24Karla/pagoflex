package com.example.pagoflex.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pagoflex.ui.theme.Dimens
import com.example.pagoflex.ui.theme.PagoFlexTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioScreen(
    folioCompromiso: String = "CP-2026-0101",
    onEnviadoExitoso: () -> Unit = {},
    onVolver: () -> Unit = {}
) {
    var motivoSeleccionado by remember { mutableStateOf("Ya pagué por otro medio") }
    var comentario by remember { mutableStateOf("") }
    var expandedMenu by remember { mutableStateOf(false) }

    val opcionesMotivos = listOf(
        "Ya pagué por otro medio",
        "No reconozco este cobro",
        "El monto no corresponde",
        "Otro"
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Reportar Problema", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
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
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(Dimens.espacioPantalla)) {
                    Text(
                        text = "¿Algo no cuadra con tus pagos?",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(Dimens.espacioMini))
                    Text(
                        text = "Indica el motivo de la incidencia para la cuota $folioCompromiso.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(Dimens.espacioMedio))

                    ExposedDropdownMenuBox(
                        expanded = expandedMenu,
                        onExpandedChange = { expandedMenu = !expandedMenu }
                    ) {
                        OutlinedTextField(
                            value = motivoSeleccionado,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Motivo de la incidencia") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedMenu) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )

                        ExposedDropdownMenu(
                            expanded = expandedMenu,
                            onDismissRequest = { expandedMenu = false }
                        ) {
                            opcionesMotivos.forEach { opcion ->
                                DropdownMenuItem(
                                    text = { Text(opcion) },
                                    onClick = {
                                        motivoSeleccionado = opcion
                                        expandedMenu = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(Dimens.espacioChico))

                    OutlinedTextField(
                        value = comentario,
                        onValueChange = { comentario = it },
                        label = { Text("Detalle o comentario adicional") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp),
                        maxLines = 3
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onEnviadoExitoso,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                enabled = motivoSeleccionado != "Otro" || comentario.isNotBlank()
            ) {
                Text("Enviar Reporte", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
            }

            OutlinedButton(
                onClick = onVolver,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("Cancelar", color = MaterialTheme.colorScheme.onBackground)
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun FormularioScreenPreview() {
    PagoFlexTheme {
        FormularioScreen()
    }
}