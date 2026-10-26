package com.example.pagoflex.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import com.example.pagoflex.model.Compromiso
import com.example.pagoflex.model.Empresa
import com.example.pagoflex.model.ResultadoOperacion
import com.example.pagoflex.ui.components.BotonSecundario
import com.example.pagoflex.ui.components.TarjetaSeccion
import com.example.pagoflex.ui.theme.Dimens
import com.example.pagoflex.ui.theme.PagoFlexTheme
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EjecutivoCarteraScreen(
    compromisos: List<Compromiso> = emptyList(),
    empresas: List<Empresa> = emptyList(),
    onCargarCompromisos: () -> List<Compromiso> = { compromisos },
    onCrearCompromiso: (Long, String, String, Int, Int, Long, Long) -> ResultadoOperacion = { _, _, _, _, _, _, _ -> ResultadoOperacion(false, "No disponible") },
    onAnularCompromiso: (Long, String) -> ResultadoOperacion = { _, _ -> ResultadoOperacion(false, "No disponible") },
    onVolver: () -> Unit = {}
) {
    var registros by remember(compromisos) { mutableStateOf(compromisos) }
    var estadoSeleccionado by remember { mutableStateOf("Todos") }
    var mesSeleccionado by remember { mutableStateOf("Todos los meses") }
    var menuMesAbierto by remember { mutableStateOf(false) }
    var crearAbierto by remember { mutableStateOf(false) }
    var empresaMenuAbierto by remember { mutableStateOf(false) }
    var empresaSeleccionada by remember(empresas) { mutableStateOf(empresas.firstOrNull()) }
    var correoCliente by remember { mutableStateOf("") }
    var concepto by remember { mutableStateOf("") }
    var cuotaActual by remember { mutableStateOf("1") }
    var cuotasTotales by remember { mutableStateOf("1") }
    var monto by remember { mutableStateOf("") }
    var vencimientoMillis by remember { mutableStateOf<Long?>(null) }
    var selectorFechaAbierto by remember { mutableStateOf(false) }
    var mensaje by remember { mutableStateOf<String?>(null) }
    var compromisoAAnular by remember { mutableStateOf<Compromiso?>(null) }
    var motivoAnulacion by remember { mutableStateOf("") }
    var detalleOtro by remember { mutableStateOf("") }
    var menuMotivoAbierto by remember { mutableStateOf(false) }

    val formatoMonto = NumberFormat.getIntegerInstance(Locale("es", "CL"))
    val formatoMes = SimpleDateFormat("MMMM yyyy", Locale("es", "CL"))
    val meses = remember(registros) {
        registros.map { compromiso ->
            Calendar.getInstance().apply { timeInMillis = compromiso.vencimientoMillis }.let { cal ->
                val clave = "${cal.get(Calendar.YEAR)}-${(cal.get(Calendar.MONTH) + 1).toString().padStart(2, '0')}"
                clave to formatoMes.format(cal.time).replaceFirstChar { it.uppercase() }
            }
        }.distinctBy { it.first }.sortedByDescending { it.first }
    }
    val filtrados = registros.filter { compromiso ->
        val pasaEstado = estadoSeleccionado == "Todos" || compromiso.estado == estadoSeleccionado
        val cal = Calendar.getInstance().apply { timeInMillis = compromiso.vencimientoMillis }
        val pasaMes = mesSeleccionado == "Todos los meses" || "${cal.get(Calendar.YEAR)}-${(cal.get(Calendar.MONTH) + 1).toString().padStart(2, '0')}" == mesSeleccionado
        pasaEstado && pasaMes
    }
    val formatoFecha = SimpleDateFormat("d MMM yyyy", Locale("es", "CL"))

    if (crearAbierto) {
        AlertDialog(
            onDismissRequest = { crearAbierto = false },
            title = { Text("Registrar compromiso", fontWeight = FontWeight.Bold) },
            text = {
                Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Se asignará a una cuenta de Usuario Final ya registrada.", style = MaterialTheme.typography.bodySmall)
                    Box {
                        TextButton(onClick = { empresaMenuAbierto = true }, modifier = Modifier.fillMaxWidth()) {
                            Text(empresaSeleccionada?.nombre ?: "Selecciona una organización")
                        }
                        DropdownMenu(expanded = empresaMenuAbierto, onDismissRequest = { empresaMenuAbierto = false }) {
                            empresas.forEach { empresa ->
                                DropdownMenuItem(text = { Text(empresa.nombre) }, onClick = { empresaSeleccionada = empresa; empresaMenuAbierto = false })
                            }
                        }
                    }
                    OutlinedTextField(correoCliente, { correoCliente = it }, label = { Text("Correo del usuario") }, modifier = Modifier.fillMaxWidth(), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email))
                    OutlinedTextField(concepto, { concepto = it }, label = { Text("Concepto") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(cuotaActual, { cuotaActual = it.filter(Char::isDigit) }, label = { Text("Cuota") }, modifier = Modifier.weight(1f), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                        OutlinedTextField(cuotasTotales, { cuotasTotales = it.filter(Char::isDigit) }, label = { Text("Total cuotas") }, modifier = Modifier.weight(1f), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                    }
                    OutlinedTextField(monto, { monto = it.filter(Char::isDigit) }, label = { Text("Monto en CLP") }, modifier = Modifier.fillMaxWidth(), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                    OutlinedTextField(
                        value = vencimientoMillis?.let { formatoFecha.format(Date(it)) }.orEmpty(),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Vencimiento") },
                        placeholder = { Text("Selecciona una fecha") },
                        modifier = Modifier.fillMaxWidth().clickable { selectorFechaAbierto = true },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    val fechaMillis = vencimientoMillis
                    val resultado = when {
                        empresaSeleccionada == null -> ResultadoOperacion(false, "Selecciona una organización.")
                        correoCliente.isBlank() || concepto.isBlank() -> ResultadoOperacion(false, "Completa el correo del usuario y el concepto.")
                        cuotaActual.toIntOrNull() == null || cuotasTotales.toIntOrNull() == null -> ResultadoOperacion(false, "Las cuotas deben ser números válidos.")
                        monto.toLongOrNull() == null || monto.toLong() <= 0L -> ResultadoOperacion(false, "Ingresa un monto mayor que cero.")
                        fechaMillis == null -> ResultadoOperacion(false, "Selecciona una fecha de vencimiento.")
                        else -> onCrearCompromiso(
                            empresaSeleccionada!!.id, correoCliente.trim(), concepto.trim(),
                            cuotaActual.toInt(), cuotasTotales.toInt(), monto.toLong(), fechaMillis
                        )
                    }
                    mensaje = resultado.mensaje
                    if (resultado.exitoso) {
                        registros = onCargarCompromisos()
                        crearAbierto = false
                        correoCliente = ""; concepto = ""; cuotaActual = "1"; cuotasTotales = "1"; monto = ""; vencimientoMillis = null
                    }
                }) { Text("Guardar") }
            },
            dismissButton = { TextButton(onClick = { crearAbierto = false }) { Text("Cancelar") } }
        )
    }

    if (selectorFechaAbierto) {
        val fechaPickerState = rememberDatePickerState(
            initialSelectedDateMillis = vencimientoMillis?.let(::fechaLocalMillisAFechaUtc) ?: fechaActualUtcMillis()
        )
        DatePickerDialog(
            onDismissRequest = { selectorFechaAbierto = false },
            confirmButton = {
                TextButton(onClick = {
                    fechaPickerState.selectedDateMillis?.let { seleccion ->
                        vencimientoMillis = fechaPickerUtcAMillisLocal(seleccion)
                        selectorFechaAbierto = false
                    }
                }) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { selectorFechaAbierto = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = fechaPickerState)
        }
    }
    compromisoAAnular?.let { compromiso ->
        AlertDialog(
            onDismissRequest = { compromisoAAnular = null },
            title = { Text("Anular compromiso", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("${compromiso.empresaNombre} · ${compromiso.concepto}")
                    Box {
                        TextButton(onClick = { menuMotivoAbierto = true }) { Text(motivoAnulacion.ifBlank { "Selecciona un motivo" }) }
                        DropdownMenu(expanded = menuMotivoAbierto, onDismissRequest = { menuMotivoAbierto = false }) {
                            listOf("Repactación", "Error", "Pagado directo", "Otro").forEach { motivo ->
                                DropdownMenuItem(text = { Text(motivo) }, onClick = { motivoAnulacion = motivo; menuMotivoAbierto = false })
                            }
                        }
                    }
                    if (motivoAnulacion == "Otro") {
                        OutlinedTextField(detalleOtro, { detalleOtro = it }, label = { Text("Describe el motivo") }, modifier = Modifier.fillMaxWidth())
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    val motivo = if (motivoAnulacion == "Otro") "Otro: ${detalleOtro.trim()}" else motivoAnulacion
                    val resultado = if (motivoAnulacion.isBlank() || (motivoAnulacion == "Otro" && detalleOtro.isBlank())) {
                        ResultadoOperacion(false, "Selecciona y completa el motivo de anulación.")
                    } else onAnularCompromiso(compromiso.id, motivo)
                    mensaje = resultado.mensaje
                    if (resultado.exitoso) {
                        registros = onCargarCompromisos()
                        compromisoAAnular = null; motivoAnulacion = ""; detalleOtro = ""
                    }
                }) { Text("Confirmar anulación") }
            },
            dismissButton = { TextButton(onClick = { compromisoAAnular = null }) { Text("Volver") } }
        )
    }

    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("Cartera de Empresa", fontWeight = FontWeight.Bold) }) },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(Dimens.espacioPantalla).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(Dimens.espacioMedio)
        ) {
            Text("Compromisos de las organizaciones asignadas a tu cuenta.", style = MaterialTheme.typography.bodyMedium)
            if (!mensaje.isNullOrBlank()) Text(mensaje!!, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodyMedium)
            Button(onClick = { mensaje = null; crearAbierto = true }, enabled = empresas.isNotEmpty(), modifier = Modifier.fillMaxWidth()) {
                Text("Registrar compromiso")
            }
            Text("Estado", fontWeight = FontWeight.SemiBold)
            Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf("Todos", "Pendiente", "Vencido", "Pagado", "En revisión", "Anulado").forEach { estado ->
                    FilterChip(selected = estadoSeleccionado == estado, onClick = { estadoSeleccionado = estado }, label = { Text(estado, style = MaterialTheme.typography.labelSmall) })
                }
            }
            Box {
                FilterChip(selected = mesSeleccionado != "Todos los meses", onClick = { menuMesAbierto = true }, label = { Text("Mes: $mesSeleccionado") })
                DropdownMenu(expanded = menuMesAbierto, onDismissRequest = { menuMesAbierto = false }) {
                    DropdownMenuItem(text = { Text("Todos los meses") }, onClick = { mesSeleccionado = "Todos los meses"; menuMesAbierto = false })
                    meses.forEach { (clave, etiqueta) ->
                        DropdownMenuItem(text = { Text(etiqueta) }, onClick = { mesSeleccionado = clave; menuMesAbierto = false })
                    }
                }
            }
            if (filtrados.isEmpty()) {
                TarjetaSeccion(titulo = "Sin resultados") {
                    Text(if (registros.isEmpty()) "Aún no hay compromisos asociados a las organizaciones de esta cuenta." else "No hay compromisos para los filtros seleccionados.")
                }
            } else {
                filtrados.forEach { compromiso ->
                    TarjetaSeccion(titulo = compromiso.empresaNombre) {
                        Text(compromiso.concepto, fontWeight = FontWeight.SemiBold)
                        Text("Cliente: ${compromiso.clienteNombre}", style = MaterialTheme.typography.bodySmall)
                        Text("Cuota ${compromiso.cuotaActual} de ${compromiso.cuotasTotales} · Vence ${formatoFecha.format(Date(compromiso.vencimientoMillis))}", style = MaterialTheme.typography.bodySmall)
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(compromiso.estado, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium)
                            Text("$${formatoMonto.format(compromiso.montoClp)} CLP", fontWeight = FontWeight.Bold)
                        }
                        if (compromiso.recargoClp > 0) Text("Recargo: $${formatoMonto.format(compromiso.recargoClp)} CLP", style = MaterialTheme.typography.bodySmall)
                        if (compromiso.estado != "Pagado" && compromiso.estado != "Anulado") {
                            Spacer(Modifier.height(4.dp))
                            TextButton(onClick = { compromisoAAnular = compromiso; motivoAnulacion = ""; detalleOtro = ""; mensaje = null }) {
                                Text("Anular compromiso")
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(Dimens.espacioChico))
            BotonSecundario(texto = "Volver", onClick = onVolver)
        }
    }
}


private fun fechaActualUtcMillis(): Long {
    val hoy = Calendar.getInstance()
    return Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
        clear()
        set(hoy.get(Calendar.YEAR), hoy.get(Calendar.MONTH), hoy.get(Calendar.DAY_OF_MONTH))
    }.timeInMillis
}

private fun fechaPickerUtcAMillisLocal(millisUtc: Long): Long {
    val fechaUtc = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply { timeInMillis = millisUtc }
    return Calendar.getInstance().apply {
        clear()
        set(fechaUtc.get(Calendar.YEAR), fechaUtc.get(Calendar.MONTH), fechaUtc.get(Calendar.DAY_OF_MONTH), 23, 59, 59)
        set(Calendar.MILLISECOND, 999)
    }.timeInMillis
}

private fun fechaLocalMillisAFechaUtc(millisLocal: Long): Long {
    val fechaLocal = Calendar.getInstance().apply { timeInMillis = millisLocal }
    return Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
        clear()
        set(fechaLocal.get(Calendar.YEAR), fechaLocal.get(Calendar.MONTH), fechaLocal.get(Calendar.DAY_OF_MONTH))
    }.timeInMillis
}
@Preview(showBackground = true)
@Composable
private fun EjecutivoCarteraScreenPreview() {
    PagoFlexTheme { EjecutivoCarteraScreen() }
}
