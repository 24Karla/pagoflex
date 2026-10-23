package com.example.pagoflex.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.pagoflex.ui.theme.Dimens
import com.example.pagoflex.ui.theme.PagoFlexTheme

@Composable
fun BotonPrincipal(
    texto: String = "Aceptar",
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    habilitado: Boolean = enabled
) {
    Button(
        onClick = onClick,
        enabled = habilitado,
        shape = RoundedCornerShape(Dimens.radioBoton),
        modifier = modifier.fillMaxWidth()
    ) {
        Text(text = texto, fontWeight = FontWeight.Bold)
    }
}

@Preview(showBackground = true)
@Composable
private fun BotonPrincipalPreview() {
    PagoFlexTheme { BotonPrincipal(texto = "Guardar") }
}