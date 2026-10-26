package com.example.pagoflex.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.FilterChip
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
import androidx.compose.ui.unit.dp
import com.example.pagoflex.model.Compromiso
import com.example.pagoflex.model.ResultadoPago
import com.example.pagoflex.ui.components.BotonPrincipal
import com.example.pagoflex.ui.components.BotonSecundario
import com.example.pagoflex.ui.components.TarjetaSeccion
import com.example.pagoflex.ui.theme.Dimens
import com.example.pagoflex.ui.theme.PagoFlexTheme
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleCompromisoScreen(
    compromiso: Compromiso? = null,
    onVolver: () -> Unit = {},
    onVerHistorial: () -> Unit = {},
    onPagar: (Long, String) -> ResultadoPago = { _, _ -> ResultadoPago(false, "No se pudo procesar el pago.") }
) {
    var resultado by remember(compromiso?.id) { mutableStateOf<ResultadoPago?>(null) }
    var canalSeleccionado by remember(compromiso?.id) { mutableStateOf("App") }
    val formatoPesos = remember { NumberFormat.getIntegerInstance(Locale("es", "CL")) }
    val disponible = compromiso?.estado == "Pendiente" || compromiso?.estado == "Vencido"
    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("Detalle", fontWeight = FontWeight.Bold) }) },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(Dimens.espacioPantalla).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(Dimens.espacioMedio)
        ) {
            if (compromiso == null) {
                TarjetaSeccion(titulo = "Compromiso no disponible") {
                    Text("No encontramos este compromiso en tu cuenta.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                TarjetaSeccion(titulo = compromiso.empresaNombre) {
                    Text("${compromiso.concepto} · Cuota ${compromiso.cuotaActual} de ${compromiso.cuotasTotales}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Vence ${SimpleDateFormat("d MMM yyyy", Locale("es", "CL")).format(Date(compromiso.vencimientoMillis))}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Estado: ${if (resultado?.exitoso == true) "Pagado" else compromiso.estado}", fontWeight = FontWeight.SemiBold, color = when { resultado?.exitoso == true || compromiso.estado == "Pagado" -> MaterialTheme.colorScheme.primary; compromiso.estado == "Vencido" -> MaterialTheme.colorScheme.error; else -> MaterialTheme.colorScheme.onSurface })
                    FilaMonto("Monto", compromiso.montoClp, formatoPesos)
                    if (compromiso.recargoClp > 0L) FilaMonto("Recargo por atraso", compromiso.recargoClp, formatoPesos)
                    FilaMonto(if (resultado?.exitoso == true) "Total pagado" else "Total a pagar", compromiso.montoClp + compromiso.recargoClp, formatoPesos, destacado = true)
                }
                TarjetaSeccion(titulo = "Contacto de ${compromiso.empresaNombre}") {
                    Text(if (compromiso.telefonoEmpresa.isBlank()) "Teléfono: no informado" else "Teléfono: ${compromiso.telefonoEmpresa}")
                    Text(if (compromiso.correoEmpresa.isBlank()) "Correo: no informado" else "Correo: ${compromiso.correoEmpresa}")
                }
                resultado?.let { respuesta ->
                    TarjetaSeccion(titulo = if (respuesta.exitoso) "Pago registrado" else "No fue posible pagar") {
                        Text(respuesta.mensaje, color = MaterialTheme.colorScheme.onSurface)
                        respuesta.folio?.let { Text("Comprobante $it", fontWeight = FontWeight.Bold) }
                    }
                }
                if (disponible && resultado?.exitoso != true) {
                    Text("Canal de pago simulado", fontWeight = FontWeight.SemiBold)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("App", "Portal", "Agente conversacional").forEach { canal ->
                            FilterChip(selected = canalSeleccionado == canal, onClick = { canalSeleccionado = canal }, label = { Text(canal) })
                        }
                    }
                }
                BotonPrincipal(
                    texto = if (resultado?.exitoso == true) "Pago registrado" else "Pagar total",
                    onClick = { resultado = onPagar(compromiso.id, canalSeleccionado) },
                    enabled = disponible && resultado?.exitoso != true
                )
                if (resultado?.exitoso == true) {
                    BotonSecundario(texto = "Ver historial de pagos", onClick = onVerHistorial)
                }
            }
            Spacer(modifier = Modifier.padding(Dimens.espacioChico))
            BotonSecundario(texto = "Volver", onClick = onVolver)
        }
    }
}

@Composable
private fun FilaMonto(etiqueta: String, monto: Long, formato: NumberFormat, destacado: Boolean = false) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = Dimens.espacioChico), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(etiqueta, color = if (destacado) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = if (destacado) FontWeight.Bold else FontWeight.Normal)
        Text("$${formato.format(monto)}", color = MaterialTheme.colorScheme.onSurface,
            fontWeight = if (destacado) FontWeight.Bold else FontWeight.Medium)
    }
}

@Preview(showBackground = true)
@Composable
private fun DetalleCompromisoScreenPreview() { PagoFlexTheme { DetalleCompromisoScreen() } }
