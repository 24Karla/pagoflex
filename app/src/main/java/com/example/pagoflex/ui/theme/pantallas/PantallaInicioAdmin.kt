package com.example.myapplication.ui.theme.pantallas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.theme.components.AvatarCircular

// Modelo simple para representar reportes de incidencias
data class ReporteItem(
    val id: String,
    val rutUsuario: String,
    val motivo: String,
    val diasAbierto: Int,
    val estado: String // "Abierto", "En gestión", "Resuelto"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaInicioAdmin(
    nombreAdmin: String = "Agente PagoFlex",
    reportes: List<ReporteItem>,
    onNavegarAConfiguraciones: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panel Administrador") },
                actions = {
                    TextButton(onClick = onNavegarAConfiguraciones) {
                        Text("Configuración")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Perfil Administrador
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                AvatarCircular()
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = nombreAdmin, style = MaterialTheme.typography.titleMedium)
                    AssistChip(
                        onClick = { },
                        label = { Text("Rol: Agente / Admin") }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Monitoreo de Reportes (RF-14)", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            // Lista de reportes con alerta si supera los 2 días hábiles (RF-14)
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(reportes) { reporte ->
                    val esCritico = reporte.diasAbierto > 2 && reporte.estado == "Abierto"

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = if (esCritico) Color(0xFFFFEBEE) else MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            if (esCritico) {
                                Text(
                                    text = "⚠️ Más de 2 días hábiles en estado Abierto",
                                    color = Color.Red,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(text = "Reporte #${reporte.id} - RUT: ${reporte.rutUsuario}")
                            Text(text = "Motivo: ${reporte.motivo}")
                            Text(text = "Estado: ${reporte.estado}")
                        }
                    }
                }
            }
        }
    }
}