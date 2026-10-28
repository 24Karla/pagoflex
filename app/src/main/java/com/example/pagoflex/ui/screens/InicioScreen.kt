package com.example.pagoflex.ui.screens

import com.example.pagoflex.ui.components.BotonSecundario
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import java.text.NumberFormat
import java.util.Locale
import com.example.pagoflex.model.ResumenCumplimiento
import com.example.pagoflex.ui.components.BotonPrincipal
import com.example.pagoflex.ui.components.TarjetaSeccion
import com.example.pagoflex.ui.theme.Dimens
import com.example.pagoflex.ui.theme.PagoFlexTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InicioScreen(
    rol: String = "usuario",
    nombreUsuario: String = "",
    totalPorPagarMes: Long = 0L,
    cantidadCompromisos: Int = 0,
    compromisosVencidos: Int = 0,
    cumplimiento: ResumenCumplimiento? = null,
    reconocimientos: List<String> = emptyList(),
    consultaLocalMillis: Long = 0L,
    proximoVencimiento: String? = null,
    reportesAbiertos: Int = 0,
    reportesEnGestion: Int = 0,
    reportesAtrasados: Int = 0,
    onNavegarCompromisos: () -> Unit = {},
    onNavegarHistorial: () -> Unit = {},
    onNavegarPerfil: () -> Unit = {},
    onNavegarAgente: () -> Unit = {},
    onNavegarRecomendaciones: () -> Unit = {},
    onNavegarEjecutivo: () -> Unit = {},
    onNavegarReportar: () -> Unit = {}
) {
    val saludoNombre = nombreUsuario.ifBlank { "Usuario" }
    val formato = NumberFormat.getIntegerInstance(Locale("es", "CL"))
    val deudaFormateada = formato.format(totalPorPagarMes.coerceAtLeast(0L))
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("PagoFlex", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onNavegarPerfil, modifier = Modifier.semantics { contentDescription = "Abrir perfil" }) {
                        Text("👤", fontSize = 20.sp, color = MaterialTheme.colorScheme.primary)
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(Dimens.espacioPantalla).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(Dimens.espacioMedio)
        ) {
            Text("Hola, $saludoNombre", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text("Datos guardados en este dispositivo · consulta local: ${java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("es", "CL")).format(java.util.Date(consultaLocalMillis))}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            when (rol) {
                "agente" -> {
                    TarjetaSeccion(titulo = "Mis reportes") {
                        Text("$reportesAbiertos abiertos · $reportesEnGestion en gestión", fontWeight = FontWeight.SemiBold)
                        Text(
                            if (reportesAtrasados == 0) "No tienes casos abiertos atrasados." else "$reportesAtrasados caso(s) requieren atención prioritaria.",
                            color = if (reportesAtrasados > 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        BotonPrincipal(texto = "Abrir bandeja", onClick = onNavegarAgente)
                    }
                    TarjetaSeccion(titulo = "Recomendaciones") {
                        Text("Continúa el seguimiento de organizaciones referidas.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        BotonPrincipal(texto = "Ver recomendaciones", onClick = onNavegarRecomendaciones)
                    }
                }
                "ejecutivo" -> {
                    TarjetaSeccion(titulo = "Resumen de empresa") { Text("Revisa organizaciones y compromisos de tu cartera.") }
                    BotonPrincipal(texto = "Resumen ejecutivo", onClick = onNavegarEjecutivo)
                }
                else -> {
                    Text("Así van tus pagos de este mes", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Card(
                        shape = RoundedCornerShape(Dimens.radioTarjeta),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.fillMaxWidth().padding(Dimens.espacioTarjeta), verticalArrangement = Arrangement.spacedBy(Dimens.espacioChico)) {
                            Text("Total por pagar este mes", color = MaterialTheme.colorScheme.onPrimary)
                            Text("$$deudaFormateada", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                            Text(
                                proximoVencimiento?.let { "Próximo vencimiento: $it" } ?: "No tienes vencimientos este mes",
                                style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                    TarjetaSeccion(titulo = "Por regularizar") {
                        Text(if (cantidadCompromisos == 0) "No tienes compromisos pendientes." else "$cantidadCompromisos compromiso(s) por pagar", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        BotonPrincipal(texto = "Ver compromisos", onClick = onNavegarCompromisos)
                    }
                    TarjetaSeccion(titulo = "Mi cumplimiento") {
                        val resumen = cumplimiento
                        Text(if (resumen == null || resumen.compromisosEvaluados < 3) "Historial insuficiente: se necesitan 3 compromisos evaluables." else "${String.format(Locale("es", "CL"), "%.1f", resumen.porcentaje)}% · ${resumen.categoria}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                        Text("${resumen?.pagosATiempo ?: 0} pagos puntuales de ${resumen?.compromisosEvaluados ?: 0} compromisos evaluados en 12 meses.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        if (compromisosVencidos > 0) Text("Tienes $compromisosVencidos compromiso(s) vencido(s). Puedes revisar opciones de pago.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    if (reconocimientos.isNotEmpty()) TarjetaSeccion(titulo = "Reconocimientos") { reconocimientos.forEach { Text("• $it") } }
                    BotonPrincipal(texto = "Historial de Pagos", onClick = onNavegarHistorial)
                    BotonSecundario(texto = "Reportar un problema", onClick = onNavegarReportar)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun InicioScreenPreview() { PagoFlexTheme { InicioScreen() } }
