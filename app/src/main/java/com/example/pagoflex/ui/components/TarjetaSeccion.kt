package com.example.pagoflex.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.pagoflex.ui.theme.Dimens
import com.example.pagoflex.ui.theme.PagoFlexTheme

@Composable
fun TarjetaSeccion(
    modifier: Modifier = Modifier,
    titulo: String = "",
    contenido: @Composable ColumnScope.() -> Unit = {}
) {
    Card(
        shape = RoundedCornerShape(Dimens.radioTarjeta),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.espacioTarjeta)
        ) {
            if (titulo.isNotEmpty()) {
                Text(
                    text = titulo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(Dimens.espacioChico))
            }
            contenido()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TarjetaSeccionPreview() {
    PagoFlexTheme {
        TarjetaSeccion(titulo = "Información") {
            Text("Contenido de prueba")
        }
    }
}