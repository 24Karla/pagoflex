package com.example.pagoflex.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pagoflex.model.ReporteIncidencia
import com.example.pagoflex.ui.components.BotonPrincipal
import com.example.pagoflex.ui.components.BotonSecundario
import com.example.pagoflex.ui.components.FotoReporte
import com.example.pagoflex.ui.components.TarjetaSeccion
import com.example.pagoflex.ui.theme.Dimens
import com.example.pagoflex.ui.theme.PagoFlexTheme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgenteReportesScreen(
    userId: Long = 1L,
    reportes: List<ReporteIncidencia> = emptyList(),
    onCargarReportes: () -> List<ReporteIncidencia> = { reportes },
    onActualizarReporte: (Long, String, String?) -> Boolean = { _, _, _ -> false },
    onVolver: () -> Unit = {},
    onVerRecomendaciones: () -> Unit = {},
    onVerPerfil: () -> Unit = {},
) {
    var listaReportes by remember(userId) { mutableStateOf(reportes) }
    var filtro by remember { mutableStateOf("Todos") }
    var seleccionado by remember { mutableStateOf<ReporteIncidencia?>(null) }
    var respuesta by remember(seleccionado?.id) { mutableStateOf(seleccionado?.respuesta.orEmpty()) }
    var errorAccion by remember { mutableStateOf<String?>(null) }
    val opciones = listOf("Todos", "Abiertos", "En gestión", "Resueltos")
    val visibles = when (filtro) {
        "Abiertos" -> listaReportes.filter { it.estado == "Abierto" }
        "En gestión" -> listaReportes.filter { it.estado == "En gestión" }
        "Resueltos" -> listaReportes.filter { it.estado == "Resuelto" }
        else -> listaReportes
    }
    val abiertos = listaReportes.count { it.estado == "Abierto" }
    val enGestion = listaReportes.count { it.estado == "En gestión" }
    val urgentes = listaReportes.count { it.estado == "Abierto" && diasHabilesDesde(it.fechaCreacionMillis) > 2 }
    val resueltos = listaReportes.filter { it.estado == "Resuelto" && it.fechaActualizacionMillis >= it.fechaCreacionMillis }
    val promedioResolucionHoras = resueltos.takeIf { it.isNotEmpty() }?.map { it.fechaActualizacionMillis - it.fechaCreacionMillis }?.average()?.div(3_600_000.0)
    val fecha = remember { SimpleDateFormat("d MMM yyyy · HH:mm", Locale("es", "CL")) }

    fun guardarAccion(estado: String, texto: String?) {
        val caso = seleccionado ?: return
        val actualizado = onActualizarReporte(caso.id, estado, texto)
        if (actualizado) {
            listaReportes = onCargarReportes()
            seleccionado = listaReportes.firstOrNull { it.id == caso.id }
            errorAccion = null
        } else {
            errorAccion = when {
                estado == "Resuelto" && texto.isNullOrBlank() -> "Escribe una respuesta antes de resolver el caso."
                else -> if (estado == "En gestión" && caso.estado == "En gestión" && texto.isNullOrBlank()) "Escribe una respuesta antes de guardarla." else "No se pudo guardar el cambio. Revisa el estado del caso."
            }
        }
    }

    seleccionado?.let { caso ->
        AlertDialog(
            onDismissRequest = { seleccionado = null; errorAccion = null },
            title = { Text(caso.empresaNombre.ifBlank { "Reporte #${caso.id}" }, fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier.heightIn(max = 480.dp).verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(Dimens.espacioChico)
                ) {
                    Text("${caso.tipo} · ${caso.estado}", fontWeight = FontWeight.SemiBold)
                    if (caso.agenteAsignadoId != null && caso.agenteAsignadoId != userId) Text("Asignado a otro agente. Puedes consultar el caso, pero no modificarlo.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    caso.compromisoId?.let { Text("Compromiso relacionado: #$it") }
                    Text("Recibido: ${fecha.format(Date(caso.fechaCreacionMillis))}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(caso.descripcion, color = MaterialTheme.colorScheme.onSurface)

                    // Foto adjunta al reporte dentro del detalle
                    FotoReporte(reporteId = caso.id)

                    if (caso.agenteAsignadoId != null && caso.agenteAsignadoId != userId) {
                        Text("Este caso está en seguimiento por otro agente.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    } else if (caso.estado == "Abierto") {
                        BotonPrincipal(texto = "Tomar caso", onClick = { guardarAccion("En gestión", null) })
                    } else {
                        OutlinedTextField(
                            value = respuesta,
                            onValueChange = { respuesta = it; errorAccion = null },
                            enabled = caso.estado == "En gestión",
                            label = { Text("Respuesta para el usuario") },
                            minLines = 3,
                            maxLines = 5,
                            modifier = Modifier.fillMaxWidth()
                        )
                        if (caso.estado == "En gestión") {
                            Button(onClick = { guardarAccion("En gestión", respuesta) }, modifier = Modifier.fillMaxWidth()) {
                                Text("Guardar respuesta")
                            }
                            OutlinedButton(onClick = { guardarAccion("Resuelto", respuesta) }, modifier = Modifier.fillMaxWidth()) {
                                Text("Marcar como resuelto")
                            }
                        }
                    }
                    caso.respuesta?.takeIf { it.isNotBlank() }?.let { anterior ->
                        Text("Respuesta guardada", fontWeight = FontWeight.SemiBold)
                        Text(anterior, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    errorAccion?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                }
            },
            confirmButton = { TextButton(onClick = { seleccionado = null; errorAccion = null }) { Text("Cerrar") } }
        )
    }

    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("PagoFlex", fontWeight = FontWeight.Bold) }, actions = { IconButton(onClick = onVerPerfil) { Text("👤", fontSize = 20.sp, color = MaterialTheme.colorScheme.primary) } }) },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(Dimens.espacioPantalla).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(Dimens.espacioMedio)
        ) {
            Text("Monitoreo de reportes", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            TarjetaSeccion(titulo = "Tu seguimiento") {
                Text("$abiertos abiertos · $enGestion en gestión", fontWeight = FontWeight.SemiBold)
                Text(if (urgentes == 0) "No hay casos abiertos atrasados." else "$urgentes caso(s) abierto(s) con más de 2 días hábiles", color = if (urgentes > 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant)
                Text(promedioResolucionHoras?.let { "Tiempo promedio de resolución: ${String.format(Locale("es", "CL"), "%.1f", it)} horas (${resueltos.size} casos)" } ?: "Aún no hay casos resueltos para calcular el promedio.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(Dimens.espacioChico)) {
                opciones.forEach { opcion ->
                    FilterChip(selected = filtro == opcion, onClick = { filtro = opcion }, label = { Text(opcion) })
                }
            }
            if (visibles.isEmpty()) {
                TarjetaSeccion(titulo = "Sin reportes") {
                    Text("Los reportes disponibles para monitoreo aparecerán aquí.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                visibles.forEach { reporte ->
                    val diasAbierto = if (reporte.estado == "Abierto") diasHabilesDesde(reporte.fechaCreacionMillis) else 0
                    TarjetaSeccion(
                        modifier = Modifier.clickable { seleccionado = reporte; respuesta = reporte.respuesta.orEmpty(); errorAccion = null },
                        titulo = reporte.empresaNombre.ifBlank { "Reporte #${reporte.id}" }
                    ) {
                        Text(reporte.tipo, fontWeight = FontWeight.SemiBold)
                        Text("${reporte.estado} · ${fecha.format(Date(reporte.fechaCreacionMillis))}", color = MaterialTheme.colorScheme.onSurfaceVariant)

                        // Foto adjunta al reporte en la tarjeta de la lista
                        FotoReporte(reporteId = reporte.id)

                        if (reporte.agenteAsignadoId != null && reporte.agenteAsignadoId != userId) Text("Asignado a otro agente", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        if (diasAbierto > 2) Text("Prioridad: abierto hace $diasAbierto días hábiles", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.SemiBold)
                        else Text("Toca para revisar el caso", color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
            BotonPrincipal(texto = "Recomendaciones de organizaciones", onClick = onVerRecomendaciones)
            BotonSecundario(texto = "Volver", onClick = onVolver)
        }
    }
}

private fun diasHabilesDesde(fechaMillis: Long): Int {
    val inicio = Calendar.getInstance().apply {
        timeInMillis = fechaMillis
        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        add(Calendar.DAY_OF_MONTH, 1)
    }
    val hoy = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
    }
    var dias = 0
    while (inicio.before(hoy)) {
        if (inicio.get(Calendar.DAY_OF_WEEK) !in setOf(Calendar.SATURDAY, Calendar.SUNDAY)) dias++
        inicio.add(Calendar.DAY_OF_MONTH, 1)
    }
    return dias
}

@Preview(showBackground = true)
@Composable
private fun AgenteReportesScreenPreview() { PagoFlexTheme { AgenteReportesScreen() } }