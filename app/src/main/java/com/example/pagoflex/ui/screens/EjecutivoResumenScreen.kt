package com.example.pagoflex.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pagoflex.model.Compromiso
import com.example.pagoflex.model.Empresa
import com.example.pagoflex.model.PagoCartera
import com.example.pagoflex.model.ResultadoOperacion
import com.example.pagoflex.ui.components.BotonPrincipal
import com.example.pagoflex.ui.components.BotonSecundario
import com.example.pagoflex.ui.components.TarjetaSeccion
import com.example.pagoflex.ui.theme.Dimens
import com.example.pagoflex.ui.theme.EstadoPagado
import com.example.pagoflex.ui.theme.EstadoPendiente
import com.example.pagoflex.ui.theme.EstadoVencido
import com.example.pagoflex.ui.theme.GrisDeshabilitado
import com.example.pagoflex.ui.theme.PagoFlexGreen
import com.example.pagoflex.ui.theme.PagoFlexTheme
import java.text.NumberFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EjecutivoResumenScreen(
    empresas: List<Empresa> = emptyList(),
    compromisos: List<Compromiso> = emptyList(),
    pagosMes: List<PagoCartera> = emptyList(),
    pagosPuntuales: List<PagoCartera> = emptyList(),
    onVolver: () -> Unit = {},
    onVerPerfil: () -> Unit = {},
    onCargarEmpresas: () -> List<Empresa> = { empresas },
    onActualizarContacto: (Long, String, String) -> ResultadoOperacion = { _, _, _ -> ResultadoOperacion(false, "No disponible") },
    onVerCartera: () -> Unit = {}
) {
    var listaEmpresas by remember(empresas) { mutableStateOf(empresas) }
    var empresaEditando by remember { mutableStateOf<Empresa?>(null) }
    var telefonoEditando by remember { mutableStateOf("") }
    var correoEditando by remember { mutableStateOf("") }
    var errorContacto by remember { mutableStateOf<String?>(null) }
    val calendario = Calendar.getInstance()
    val inicioMes = Calendar.getInstance().apply {
        set(Calendar.DAY_OF_MONTH, 1); set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
    }.timeInMillis
    calendario.timeInMillis = inicioMes
    calendario.add(Calendar.MONTH, 1)
    val finMes = calendario.timeInMillis
    val delMes = compromisos.filter { it.vencimientoMillis >= inicioMes && it.vencimientoMillis < finMes && it.estado != "Anulado" }
    val pagados = delMes.count { it.estado == "Pagado" }
    val vencidos = delMes.count { it.estado == "Vencido" }
    val pendientes = delMes.count { it.estado == "Pendiente" || it.estado == "En revisión" }
    val total = delMes.size
    val tasa = if (total == 0) 0.0 else (pagados * 100.0 / total)
    val tasaTexto = String.format(Locale("es", "CL"), "%.1f", tasa).removeSuffix(",0")
    val recaudado = pagosMes.sumOf { it.montoClp }
    val formato = NumberFormat.getIntegerInstance(Locale("es", "CL"))
    val mesNombre = java.text.SimpleDateFormat("MMMM yyyy", Locale("es", "CL")).format(Calendar.getInstance().time)

    empresaEditando?.let { empresa ->
        AlertDialog(
            onDismissRequest = { empresaEditando = null },
            title = { Text("Contacto de ${empresa.nombre}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(telefonoEditando, { telefonoEditando = it; errorContacto = null }, label = { Text("Teléfono") }, singleLine = true)
                    OutlinedTextField(correoEditando, { correoEditando = it; errorContacto = null }, label = { Text("Correo") }, singleLine = true)
                    errorContacto?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                }
            },
            confirmButton = {
                Button(onClick = {
                    val resultado = onActualizarContacto(empresa.id, telefonoEditando, correoEditando)
                    if (resultado.exitoso) {
                        listaEmpresas = onCargarEmpresas()
                        empresaEditando = null
                    } else errorContacto = resultado.mensaje
                }) { Text("Guardar") }
            },
            dismissButton = { TextButton(onClick = { empresaEditando = null }) { Text("Cancelar") } }
        )
    }
    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("Resumen ejecutivo", fontWeight = FontWeight.Bold) }, actions = { IconButton(onClick = onVerPerfil) { Text("👤") } }) },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(Dimens.espacioPantalla).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(Dimens.espacioMedio)
        ) {
            Text(mesNombre.replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IndicadorEjecutivo("Organizaciones", listaEmpresas.size.toString(), Modifier.weight(1f))
                IndicadorEjecutivo("Pagados", pagados.toString(), Modifier.weight(1f), EstadoPagado)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IndicadorEjecutivo("Pendientes", pendientes.toString(), Modifier.weight(1f), EstadoPendiente)
                IndicadorEjecutivo("Vencidos", vencidos.toString(), Modifier.weight(1f), EstadoVencido)
            }
            if (empresas.isNotEmpty()) TarjetaSeccion(titulo = "Organizaciones y contacto") {
                empresas.forEach { empresa ->
                    Text(empresa.nombre, fontWeight = FontWeight.SemiBold)
                    Text(if (empresa.telefonoContacto.isBlank()) "Teléfono: no informado" else "Teléfono: ${empresa.telefonoContacto}", style = MaterialTheme.typography.bodySmall)
                    Text(if (empresa.correoContacto.isBlank()) "Correo: no informado" else "Correo: ${empresa.correoContacto}", style = MaterialTheme.typography.bodySmall)
                    TextButton(onClick = { empresaEditando = empresa; telefonoEditando = empresa.telefonoContacto; correoEditando = empresa.correoContacto; errorContacto = null }) { Text("Editar contacto") }
                }
            }
            TarjetaSeccion(titulo = "Reconocimientos de la cartera") {
                val porEmpresa = pagosPuntuales.groupingBy { it.empresaNombre }.eachCount().toList().sortedByDescending { it.second }
                if (porEmpresa.isEmpty()) Text("Aún no hay pagos puntuales registrados en los últimos 12 meses.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                else porEmpresa.forEach { (empresa, cantidad) -> Text("$empresa: $cantidad pago(s) a tiempo") }
                Text("Reconocimientos basados en pagos realizados antes del vencimiento.", style = MaterialTheme.typography.bodySmall)
            }
            TarjetaSeccion(titulo = "Monto recaudado este mes") {
                Text("$${formato.format(recaudado)} CLP", style = MaterialTheme.typography.headlineSmall, color = PagoFlexGreen, fontWeight = FontWeight.Bold)
                Text("Suma de pagos registrados en el período", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            TarjetaSeccion(titulo = "Tasa de cobranza") {
                Text("$tasaTexto%", style = MaterialTheme.typography.headlineMedium, color = PagoFlexGreen, fontWeight = FontWeight.Bold)
                Text("Pagados / compromisos no anulados del mes ($total)", style = MaterialTheme.typography.bodySmall)
            }
            TarjetaSeccion(titulo = "Estado de la cartera") {
                if (total == 0) Text("No hay compromisos para este mes.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                else {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
                        Box(contentAlignment = Alignment.Center) {
                            Canvas(Modifier.size(154.dp)) {
                                val grosor = 20.dp.toPx()
                                drawArc(GrisDeshabilitado.copy(alpha = 0.25f), -90f, 360f, false, style = Stroke(grosor, cap = StrokeCap.Round))
                                var angulo = -90f
                                val valores = listOf(pagados, pendientes, vencidos)
                                val colores = listOf(EstadoPagado, EstadoPendiente, EstadoVencido)
                                valores.forEachIndexed { indice, valor ->
                                    if (valor > 0) {
                                        val barrido = 360f * valor / total
                                        drawArc(colores[indice], angulo, barrido, false, style = Stroke(grosor, cap = StrokeCap.Butt))
                                        angulo += barrido
                                    }
                                }
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(total.toString(), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                                Text("compromisos", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            LeyendaEjecutivo("Pagados", pagados, EstadoPagado)
                            LeyendaEjecutivo("Pendientes", pendientes, EstadoPendiente)
                            LeyendaEjecutivo("Vencidos", vencidos, EstadoVencido)
                        }
                    }
                }
            }
            TarjetaSeccion(titulo = "Compromisos por estado") {
                if (total == 0) Text("Los datos aparecerán cuando haya compromisos en el período.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                else {
                    val maximo = maxOf(pagados, pendientes, vencidos, 1)
                    Row(Modifier.fillMaxWidth().height(150.dp), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.Bottom) {
                        BarraEjecutivo("Pagados", pagados, maximo, EstadoPagado, Modifier.weight(1f))
                        BarraEjecutivo("Pendientes", pendientes, maximo, EstadoPendiente, Modifier.weight(1f))
                        BarraEjecutivo("Vencidos", vencidos, maximo, EstadoVencido, Modifier.weight(1f))
                    }
                }
            }
            BotonPrincipal(texto = "Ver cartera", onClick = onVerCartera)

        }
    }
}

@Composable
private fun IndicadorEjecutivo(titulo: String, valor: String, modifier: Modifier = Modifier, color: Color = PagoFlexGreen) {
    TarjetaSeccion(modifier = modifier, titulo = titulo) {
        Text(valor, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = color)
    }
}

@Composable
private fun LeyendaEjecutivo(texto: String, cantidad: Int, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(10.dp).background(color, MaterialTheme.shapes.small))
        Spacer(Modifier.width(6.dp))
        Text("$texto: $cantidad", style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun BarraEjecutivo(etiqueta: String, valor: Int, maximo: Int, color: Color, modifier: Modifier = Modifier) {
    Column(modifier.padding(horizontal = 8.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Bottom) {
        Text(valor.toString(), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(4.dp))
        Box(Modifier.width(34.dp).height((82f * valor / maximo).coerceAtLeast(if (valor > 0) 8f else 2f).dp).background(color, MaterialTheme.shapes.small))
        Spacer(Modifier.height(6.dp))
        Text(etiqueta, style = MaterialTheme.typography.labelSmall)
    }
}

@Preview(showBackground = true)
@Composable
private fun EjecutivoResumenScreenPreview() {
    PagoFlexTheme { EjecutivoResumenScreen() }
}
