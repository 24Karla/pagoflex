package com.example.pagoflex.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pagoflex.ui.theme.Dimens
import com.example.pagoflex.ui.theme.PagoFlexTheme

data class ReporteAgente(
    val id: String,
    val usuario: String,
    val motivo: String,
    val empresa: String,
    val diasAbierto: Int,
    val estado: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgenteReportesScreen(
    onTomarCaso: (String) -> Unit = {}
) {
    val reportes = listOf(
        ReporteAgente("REP-101", "Jesús Gonzales", "No reconozco cobro", "Gimnasio Smartfit", 3, "ABIERTO"),
        ReporteAgente("REP-102", "Camila Rojas", "Ya pagué por caja", "Colegio San Jorge", 1, "ABIERTO")
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Módulo Agente: Reportes", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(Dimens.espacioPantalla)
        ) {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(Dimens.espacioChico)) {
                items(reportes) { reporte ->
                    val esCritico = reporte.diasAbierto >= 2 && reporte.estado == "ABIERTO"

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (esCritico) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(modifier = Modifier.padding(Dimens.espacioPantalla)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = reporte.usuario,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Surface(
                                    color = if (esCritico) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primaryContainer,
                                    shape = RoundedCornerShape(Dimens.espacioMini)
                                ) {
                                    Text(
                                        text = if (esCritico) "CRÍTICO (${reporte.diasAbierto}d)" else "ABIERTO",
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (esCritico) MaterialTheme.colorScheme.onError else MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(Dimens.espacioMini))
                            Text(text = "Motivo: ${reporte.motivo}", style = MaterialTheme.typography.bodyMedium)
                            Text(text = "Empresa: ${reporte.empresa}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                            Spacer(modifier = Modifier.height(Dimens.espacioChico))

                            Button(
                                onClick = { onTomarCaso(reporte.id) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Text("Tomar y Responder Caso", color = MaterialTheme.colorScheme.onPrimary)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun AgenteReportesScreenPreview() {
    PagoFlexTheme {
        AgenteReportesScreen()
    }
}