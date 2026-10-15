package com.example.myapplication.ui.theme.pantallas

// Archivo: ui/pantallas/PantallaInicioUsuario.kt
// Responsabilidad: Pantalla principal/inicio para el rol de Usuario Final

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
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.components.AvatarCircular

// Modelo simple para representar cada compromiso
data class CompromisoItem(
    val empresa: String,
    val concepto: String,
    val cuota: String,
    val monto: Long,
    val fechaVencimiento: String,
    val estado: String // "Pendiente", "Vencido", "Pagado", "En revisión"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaInicioUsuario(
    nombreUsuario: String = "Juan Pérez",
    porcentajeCumplimiento: Float = 96.0f,
    compromisos: List<CompromisoItem>,
    onNavegarAConfiguraciones: () -> Unit
) {
    // Cálculos para la vista consolidada (RF-02)
    val totalAPagar = compromisos
        .filter { it.estado == "Pendiente" || it.estado == "Vencido" }
        .sumOf { it.monto }
    val cantidadVencidos = compromisos.count { it.estado == "Vencido" }

    // Categoría según RN-10/RN-11
    val categoriaCumplimiento = when {
        compromisos.size < 3 -> "Historial insuficiente"
        porcentajeCumplimiento >= 95f -> "Ejemplar 🌟"
        porcentajeCumplimiento >= 80f -> "Al día 👍"
        else -> "En camino 💪"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Inicio - PagoFlex") },
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
            // Encabezado con Avatar e Indicador de Cumplimiento (RF-08)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                AvatarCircular()
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = "¡Hola, $nombreUsuario!", style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = "Cumplimiento: $porcentajeCumplimiento% ($categoriaCumplimiento)",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Resumen de Situación (RF-02)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Resumen de Situación", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Total a pagar este mes: $$totalAPagar CLP", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text(text = "Compromisos vencidos: $cantidadVencidos", color = if (cantidadVencidos > 0) Color.Red else Color.Unspecified)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Compromisos Vigentes (RF-03)", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            // Listado de compromisos
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(compromisos) { item ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(text = item.empresa, fontWeight = FontWeight.Bold)
                            Text(text = "${item.concepto} (${item.cuota})")
                            Text(text = "Monto: $${item.monto} CLP")
                            Text(text = "Vence: ${item.fechaVencimiento} | Estado: ${item.estado}")
                        }
                    }
                }
            }
        }
    }
}