package com.example.pagoflex.ui.screens

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pagoflex.data.PagoUsuario
import com.example.pagoflex.model.ComprobantePago
import com.example.pagoflex.ui.components.BotonSecundario
import com.example.pagoflex.ui.components.TarjetaSeccion
import com.example.pagoflex.ui.theme.Dimens
import com.example.pagoflex.ui.theme.PagoFlexTheme
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialPagosScreen(
    onVolver: () -> Unit = {},
    pagos: List<PagoUsuario> = emptyList(),
    comprobantes: List<ComprobantePago> = emptyList()
) {
    var comprobanteSeleccionado by remember { mutableStateOf<ComprobantePago?>(null) }
    var empresaSeleccionada by remember { mutableStateOf("Todas") }
    var canalSeleccionado by remember { mutableStateOf("Todos") }
    var periodo by remember { mutableStateOf("12 meses") }
    val context = LocalContext.current
    val formatoFecha = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("es", "CL")) }
    val formatoPesos = remember { NumberFormat.getIntegerInstance(Locale("es", "CL")) }
    val empresas = pagos.map { it.empresaNombre.ifBlank { it.concepto.substringBefore(" · ") } }.filter { it.isNotBlank() }.distinct().sorted()
    val canales = pagos.map { it.canal }.filter { it.isNotBlank() }.distinct().sorted()
    val ahora = System.currentTimeMillis()
    val limite = when (periodo) {
        "30 días" -> ahora - 30L * 86_400_000L
        "12 meses" -> Calendar.getInstance().apply { timeInMillis = ahora; add(Calendar.MONTH, -12) }.timeInMillis
        else -> Long.MIN_VALUE
    }
    val filtrados = pagos.filter { pago ->
        val empresa = pago.empresaNombre.ifBlank { pago.concepto.substringBefore(" · ") }
        pago.fechaMillis >= limite && (empresaSeleccionada == "Todas" || empresa == empresaSeleccionada) && (canalSeleccionado == "Todos" || pago.canal == canalSeleccionado)
    }
    val calendarioAnual = Calendar.getInstance().apply {
        set(Calendar.DAY_OF_YEAR, 1); set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
    }
    val inicioAnio = calendarioAnual.timeInMillis
    val pagadosAnio = pagos.filter { it.estado == "Pagado" && it.fechaMillis >= inicioAnio && it.fechaMillis <= ahora }
    val montoAnual = pagadosAnio.sumOf { it.monto.toLongOrNull() ?: 0L }

    comprobanteSeleccionado?.let { comprobante ->
        AlertDialog(
            onDismissRequest = { comprobanteSeleccionado = null },
            title = { Text("Comprobante ${comprobante.folio}", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(Dimens.espacioChico)) {
                    Text("Concepto: ${comprobante.concepto}")
                    Text("Total pagado: $${formatoPesos.format(comprobante.montoClp)} CLP", fontWeight = FontWeight.Bold)
                    Text("Fecha: ${formatoFecha.format(Date(comprobante.fechaMillis))}")
                    Text("Canal: ${comprobante.canal}")
                    Text("Este comprobante está guardado en tu cuenta.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            confirmButton = { TextButton(onClick = { comprobanteSeleccionado = null }) { Text("Cerrar") } }
        )
    }

    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("Comprobantes y pagos", fontWeight = FontWeight.Bold) }) },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(Dimens.espacioPantalla).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(Dimens.espacioMedio)
        ) {
            TarjetaSeccion(titulo = "Resumen del año ${Calendar.getInstance().get(Calendar.YEAR)}") {
                Text("Pagos realizados: ${pagadosAnio.size}", fontWeight = FontWeight.SemiBold)
                Text("Monto pagado: $${formatoPesos.format(montoAnual)} CLP", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            }
            Text("Filtrar por empresa", fontWeight = FontWeight.SemiBold)
            Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                (listOf("Todas") + empresas).forEach { empresa ->
                    FilterChip(selected = empresaSeleccionada == empresa, onClick = { empresaSeleccionada = empresa }, label = { Text(empresa) })
                }
            }
            Text("Período", fontWeight = FontWeight.SemiBold)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf("Todo", "30 días", "12 meses").forEach { opcion ->
                    FilterChip(selected = periodo == opcion, onClick = { periodo = opcion }, label = { Text(opcion) })
                }
            }
            Text("Canal", fontWeight = FontWeight.SemiBold)
            Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                (listOf("Todos") + canales).forEach { canal ->
                    FilterChip(selected = canalSeleccionado == canal, onClick = { canalSeleccionado = canal }, label = { Text(canal) })
                }
            }
            Text("Comprobantes de pago", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            if (comprobantes.isEmpty()) {
                TarjetaSeccion(titulo = "Aún no tienes comprobantes") {
                    Text("Cuando completes un pago, su comprobante quedará guardado aquí.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                comprobantes.forEach { comprobante ->
                    TarjetaSeccion(titulo = comprobante.concepto.substringBefore(" · ")) {
                        Text(comprobante.concepto, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Folio ${comprobante.folio}", fontWeight = FontWeight.SemiBold)
                        Text("Total: $${formatoPesos.format(comprobante.montoClp)} CLP", fontWeight = FontWeight.Bold)
                        Text("${formatoFecha.format(Date(comprobante.fechaMillis))} · ${comprobante.canal}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Row {
                            TextButton(onClick = { comprobanteSeleccionado = comprobante }) { Text("Ver comprobante") }
                            TextButton(onClick = {
                                val texto = "PagoFlex · Comprobante ${comprobante.folio}\n${comprobante.concepto}\nTotal: $${formatoPesos.format(comprobante.montoClp)} CLP\nFecha: ${formatoFecha.format(Date(comprobante.fechaMillis))}\nCanal: ${comprobante.canal}"
                                val compartir = Intent(Intent.ACTION_SEND).apply { type = "text/plain"; putExtra(Intent.EXTRA_TEXT, texto) }
                                runCatching { context.startActivity(Intent.createChooser(compartir, "Compartir comprobante")) }
                            }) { Text("Compartir") }
                        }
                    }
                }
            }
            Text("Actividad de pagos", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            if (filtrados.isEmpty()) {
                TarjetaSeccion(titulo = "Sin movimientos") {
                    Text("No hay pagos que coincidan con los filtros seleccionados.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                filtrados.forEach { pago ->
                    TarjetaSeccion(
                        modifier = Modifier.clickable { comprobantes.firstOrNull { it.pagoId == pago.id }?.let { comprobanteSeleccionado = it } },
                        titulo = pago.empresaNombre.ifBlank { pago.concepto }
                    ) {
                        Text("Monto: $${pago.monto} CLP", fontWeight = FontWeight.SemiBold)
                        Text("Concepto: ${pago.concepto}")
                        Text("${formatoFecha.format(Date(pago.fechaMillis))} · Canal: ${pago.canal.ifBlank { "Sin canal informado" }}")
                        Text("Estado: ${pago.estado}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
            Spacer(Modifier.padding(Dimens.espacioChico))
            BotonSecundario(texto = "Volver", onClick = onVolver)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HistorialPagosScreenPreview() { PagoFlexTheme { HistorialPagosScreen() } }
