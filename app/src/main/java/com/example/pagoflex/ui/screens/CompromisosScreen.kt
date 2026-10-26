package com.example.pagoflex.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
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
import com.example.pagoflex.model.Compromiso
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
fun CompromisosScreen(
    userId: Long = 1L,
    compromisos: List<Compromiso> = emptyList(),
    onVolver: () -> Unit = {},
    onVerDetalle: (Long) -> Unit = {},
    onIrAPagar: () -> Unit = {}
) {
    var filtro by remember { mutableStateOf("Todos") }
    var ordenarPorMora by remember { mutableStateOf(false) }
    val ahora = System.currentTimeMillis()
    val limiteTresMeses = Calendar.getInstance().apply { timeInMillis = ahora; add(Calendar.MONTH, 3) }.timeInMillis
    val formatoPesos = remember { NumberFormat.getIntegerInstance(Locale("es", "CL")) }
    val listaBase = when (filtro) {
        "Vencidos" -> compromisos.filter { it.estado == "Vencido" }
        "Pendientes" -> compromisos.filter { it.estado == "Pendiente" || it.estado == "En revisión" }
        "Próx. 3 meses" -> compromisos.filter { (it.estado == "Pendiente" || it.estado == "Vencido") && it.vencimientoMillis >= ahora && it.vencimientoMillis < limiteTresMeses }
        else -> compromisos
    }
    val lista = if (ordenarPorMora) listaBase.sortedByDescending {
        if (it.estado == "Vencido" && it.vencimientoMillis < ahora) (ahora - it.vencimientoMillis) / 86_400_000L else 0L
    } else listaBase.sortedBy { it.vencimientoMillis }
    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("Compromisos", fontWeight = FontWeight.Bold) }) },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(Dimens.espacioPantalla).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(Dimens.espacioMedio)
        ) {
            Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(Dimens.espacioChico)) {
                listOf("Todos", "Vencidos", "Pendientes", "Próx. 3 meses").forEach { opcion ->
                    FilterChip(selected = filtro == opcion, onClick = { filtro = opcion }, label = { Text(opcion) })
                }
                FilterChip(selected = ordenarPorMora, onClick = { ordenarPorMora = !ordenarPorMora }, label = { Text("Mayor mora") })
            }
            if (lista.isEmpty()) {
                TarjetaSeccion(titulo = if (compromisos.isEmpty()) "Sin compromisos" else "Sin resultados") {
                    Text(if (compromisos.isEmpty()) "Cuando una organización registre un compromiso, aparecerá aquí." else "No hay compromisos en este filtro.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                lista.forEach { compromiso ->
                    TarjetaSeccion(
                        modifier = Modifier.clickable { onVerDetalle(compromiso.id) },
                        titulo = compromiso.empresaNombre
                    ) {
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("${compromiso.concepto} · ${compromiso.cuotaActual} de ${compromiso.cuotasTotales}", color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
                            Text(compromiso.estado, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                        }
                        Text("Vence ${SimpleDateFormat("d MMM yyyy", Locale("es", "CL")).format(Date(compromiso.vencimientoMillis))}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        if (compromiso.estado == "Vencido" && compromiso.vencimientoMillis < ahora) {
                            val diasAtraso = ((ahora - compromiso.vencimientoMillis) / 86_400_000L).coerceAtLeast(1L)
                            Text("Vencido hace $diasAtraso día(s)", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.SemiBold)
                        }
                        Text("$${formatoPesos.format(compromiso.montoClp)}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }
            Spacer(modifier = Modifier.padding(Dimens.espacioChico))
            BotonSecundario(texto = "Volver", onClick = onVolver)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CompromisosScreenPreview() { PagoFlexTheme { CompromisosScreen() } }
