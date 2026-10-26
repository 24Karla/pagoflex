package com.example.pagoflex.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.unit.sp
import com.example.pagoflex.model.RecomendacionOrganizacion
import com.example.pagoflex.ui.components.BotonPrincipal
import com.example.pagoflex.ui.components.BotonSecundario
import com.example.pagoflex.ui.components.TarjetaSeccion
import com.example.pagoflex.ui.theme.Dimens
import com.example.pagoflex.ui.theme.PagoFlexTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgenteRecomendacionesScreen(
    userId: Long = 1L,
    recomendaciones: List<RecomendacionOrganizacion> = emptyList(),
    onCargarRecomendaciones: () -> List<RecomendacionOrganizacion> = { recomendaciones },
    onActualizarEstado: (Long, String) -> Boolean = { _, _ -> false },
    onVolver: () -> Unit = {},
    onVerPerfil: () -> Unit = {}
) {
    var lista by remember(userId) { mutableStateOf(recomendaciones) }
    var mensaje by remember { mutableStateOf<String?>(null) }
    val fecha = remember { SimpleDateFormat("d MMM yyyy", Locale("es", "CL")) }
    val estados = listOf("Recibida", "Contactada", "En evaluación", "Se unió / Descartada")

    fun avanzar(recomendacion: RecomendacionOrganizacion, estado: String) {
        if (onActualizarEstado(recomendacion.id, estado)) {
            lista = onCargarRecomendaciones()
            mensaje = null
        } else {
            mensaje = "No se pudo actualizar. Revisa el estado actual de la recomendación."
        }
    }

    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("PagoFlex", fontWeight = FontWeight.Bold) }, actions = { IconButton(onClick = onVerPerfil) { Text("👤", fontSize = 20.sp, color = MaterialTheme.colorScheme.primary) } }) },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(Dimens.espacioPantalla).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(Dimens.espacioMedio)
        ) {
            Text("Recomendaciones", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            TarjetaSeccion(titulo = "Seguimiento") {
                Text("Avanza cada organización según el contacto realizado.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(estados.joinToString("  →  "), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
            }
            mensaje?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            if (lista.isEmpty()) {
                TarjetaSeccion(titulo = "Sin recomendaciones") {
                    Text("Las organizaciones referidas aparecerán aquí.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                lista.forEach { recomendacion ->
                    TarjetaSeccion(titulo = recomendacion.organizacion) {
                        Text("Estado: ${recomendacion.estado}", fontWeight = FontWeight.SemiBold)
                        Text("Contacto: ${recomendacion.contacto}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Autorización de contacto: ${if (recomendacion.autorizacionContacto) "Confirmada" else "No confirmada"}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Actualizada: ${fecha.format(Date(recomendacion.fechaActualizacionMillis))}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        when (recomendacion.estado) {
                            "Recibida" -> BotonPrincipal(texto = "Marcar como contactada", onClick = { avanzar(recomendacion, "Contactada") })
                            "Contactada" -> BotonPrincipal(texto = "Pasar a evaluación", onClick = { avanzar(recomendacion, "En evaluación") })
                            "En evaluación" -> Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Dimens.espacioChico)) {
                                BotonPrincipal(texto = "Se unió", modifier = Modifier.weight(1f), onClick = { avanzar(recomendacion, "Se unió") })
                                OutlinedButton(onClick = { avanzar(recomendacion, "Descartada") }, modifier = Modifier.weight(1f)) { Text("Descartar") }
                            }
                            else -> Text("Seguimiento finalizado", color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
            BotonSecundario(texto = "Volver", onClick = onVolver)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AgenteRecomendacionesScreenPreview() { PagoFlexTheme { AgenteRecomendacionesScreen() } }
