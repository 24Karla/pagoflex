package com.example.pagoflex.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import com.example.pagoflex.ui.theme.Dimens
import com.example.pagoflex.ui.theme.PagoFlexTheme

@Composable
fun CampoContrasena(
    valor: String = "",
    value: String = valor,
    onValorCambiado: (String) -> Unit = {},
    onValueChange: (String) -> Unit = onValorCambiado,
    modifier: Modifier = Modifier,
    etiqueta: String = "Contraseña",
    label: String = etiqueta,
    esVisibleInicial: Boolean = false
) {
    var contrasenaVisible by remember { mutableStateOf(esVisibleInicial) }
    val textoActual = if (value != "") value else valor
    val callback = if (onValueChange != onValorCambiado) onValueChange else onValorCambiado
    val textoEtiqueta = if (label != "Contraseña") label else etiqueta

    OutlinedTextField(
        value = textoActual,
        onValueChange = callback,
        label = { Text(textoEtiqueta) },
        singleLine = true,
        visualTransformation = if (contrasenaVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailingIcon = {
            TextButton(onClick = { contrasenaVisible = !contrasenaVisible }) {
                Text(
                    text = if (contrasenaVisible) "Ocultar" else "Ver",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        shape = RoundedCornerShape(Dimens.radioBoton),
        modifier = modifier.fillMaxWidth()
    )
}

@Preview(showBackground = true)
@Composable
private fun CampoContrasenaPreview() {
    PagoFlexTheme { CampoContrasena() }
}