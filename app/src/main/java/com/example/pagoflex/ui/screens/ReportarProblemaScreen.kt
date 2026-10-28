package com.example.pagoflex.ui.screens

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
import androidx.compose.material3.CenterAlignedTopAppBar
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.pagoflex.model.Compromiso
import com.example.pagoflex.model.ResultadoOperacion
import com.example.pagoflex.ui.components.BotonPrincipal
import com.example.pagoflex.ui.components.BotonSecundario
import com.example.pagoflex.ui.components.FotoDeArchivo
import com.example.pagoflex.ui.components.TarjetaSeccion
import com.example.pagoflex.ui.components.rememberSelectorFoto
import com.example.pagoflex.ui.theme.Dimens
import com.example.pagoflex.ui.theme.PagoFlexTheme
import java.io.File

const val RUTA_REPORTAR_PROBLEMA = "reportar_problema"

private val TIPOS_PROBLEMA = listOf("Monto incorrecto", "Ya pagué", "No reconozco este cobro", "Otro")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportarProblemaScreen(
    borrador: File,
    compromisos: List<Compromiso> = emptyList(),
    onEnviar: (Long?, String, String, File?) -> ResultadoOperacion = { _, _, _, _ -> ResultadoOperacion(false, "No disponible") },
    onVolver: () -> Unit = {}
) {
    var compromisoId by rememberSaveable { mutableStateOf<Long?>(null) }
    var tipo by rememberSaveable { mutableStateOf("") }
    var descripcion by rememberSaveable { mutableStateOf("") }
    var versionFoto by rememberSaveable { mutableStateOf(0) }
    var mensaje by rememberSaveable { mutableStateOf<String?>(null) }
    var envioExitoso by rememberSaveable { mutableStateOf(false) }
    var menuCompromisoAbierto by remember { mutableStateOf(false) }

    val selector = rememberSelectorFoto(destino = borrador) { exito, error ->
        if (exito) {
            versionFoto++
            mensaje = null
            envioExitoso = false
        } else if (error != null) {
            mensaje = error
            envioExitoso = false
        }
    }
    val tieneFoto = remember(versionFoto) { borrador.exists() }
    val seleccionado = compromisos.firstOrNull { it.id == compromisoId }
    val textoCompromiso = seleccionado?.let { "${it.empresaNombre} · ${it.concepto}" } ?: "Ninguno en particular"

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Reportar un problema", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    TextButton(onClick = { borrador.delete(); onVolver() }) { Text("‹ Volver") }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(Dimens.espacioPantalla)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(Dimens.espacioMedio)
        ) {
            Text(
                "Cuéntanos qué pasó y un agente revisará tu caso.",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            TarjetaSeccion(titulo = "¿Con qué compromiso?") {
                Box {
                    TextButton(onClick = { menuCompromisoAbierto = true }, modifier = Modifier.fillMaxWidth()) {
                        Text(textoCompromiso)
                    }
                    DropdownMenu(expanded = menuCompromisoAbierto, onDismissRequest = { menuCompromisoAbierto = false }) {
                        DropdownMenuItem(
                            text = { Text("Ninguno en particular") },
                            onClick = { compromisoId = null; menuCompromisoAbierto = false }
                        )
                        compromisos.forEach { compromiso ->
                            DropdownMenuItem(
                                text = { Text("${compromiso.empresaNombre} · ${compromiso.concepto}") },
                                onClick = { compromisoId = compromiso.id; menuCompromisoAbierto = false }
                            )
                        }
                    }
                }
            }

            TarjetaSeccion(titulo = "Tipo de problema") {
                Row(
                    Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(Dimens.espacioChico)
                ) {
                    TIPOS_PROBLEMA.forEach { opcion ->
                        FilterChip(
                            selected = tipo == opcion,
                            onClick = { tipo = opcion },
                            label = { Text(opcion) }
                        )
                    }
                }
            }

            TarjetaSeccion(titulo = "Descripción") {
                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it.take(500) },
                    label = { Text("Describe el problema") },
                    minLines = 3,
                    supportingText = { Text("${descripcion.length}/500") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            TarjetaSeccion(titulo = "Foto (opcional)") {
                Text(
                    "Puedes adjuntar una foto de la boleta, del cobro o de la pantalla donde ves el error.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (tieneFoto) FotoDeArchivo(archivo = borrador, version = versionFoto)
                BotonSecundario(
                    texto = if (tieneFoto) "Tomar otra foto" else "Tomar foto",
                    onClick = selector.tomarFoto
                )
                BotonSecundario(texto = "Elegir de la galería", onClick = selector.elegirDeGaleria)
                if (tieneFoto) {
                    TextButton(onClick = { borrador.delete(); versionFoto++ }) { Text("Quitar foto") }
                }
            }

            mensaje?.let {
                Text(
                    it,
                    color = if (envioExitoso) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.SemiBold
                )
            }

            BotonPrincipal(
                texto = "Enviar reporte",
                onClick = {
                    val resultado = when {
                        tipo.isBlank() -> ResultadoOperacion(false, "Selecciona el tipo de problema.")
                        descripcion.trim().length < 10 -> ResultadoOperacion(false, "Describe el problema con al menos 10 caracteres.")
                        else -> onEnviar(compromisoId, tipo, descripcion.trim(), if (borrador.exists()) borrador else null)
                    }
                    mensaje = resultado.mensaje
                    envioExitoso = resultado.exitoso
                    if (resultado.exitoso) {
                        tipo = ""
                        descripcion = ""
                        compromisoId = null
                        versionFoto++
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(Dimens.espacioChico))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ReportarProblemaScreenPreview() {
    PagoFlexTheme { ReportarProblemaScreen(borrador = File("borrador.jpg")) }
}