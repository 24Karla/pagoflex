package com.example.pagoflex.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.pagoflex.ui.theme.PagoFlexTheme

@Composable
fun ControlesSeleccion(
    etiqueta: String = "",
    texto: String = etiqueta,
    seleccionado: Boolean = false,
    checked: Boolean = seleccionado,
    onSelecciónCambiada: (Boolean) -> Unit = {},
    onSeleccionCambiada: (Boolean) -> Unit = onSelecciónCambiada,
    onCheckedChange: ((Boolean) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val estadoActual = if (checked != seleccionado) checked else seleccionado
    val textoMostrar = if (etiqueta.isNotEmpty()) etiqueta else texto
    val callback = onCheckedChange ?: if (onSeleccionCambiada != onSelecciónCambiada) onSeleccionCambiada else onSelecciónCambiada

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = textoMostrar,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Switch(
            checked = estadoActual,
            onCheckedChange = callback
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ControlesSeleccionPreview() {
    PagoFlexTheme {
        ControlesSeleccion(
            etiqueta = "Notificaciones Push",
            seleccionado = true,
            onSelecciónCambiada = {}
        )
    }
}