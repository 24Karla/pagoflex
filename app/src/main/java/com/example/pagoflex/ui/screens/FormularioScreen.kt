package com.example.pagoflex.ui.screens

import android.Manifest
import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pagoflex.ui.components.BotonPrincipal
import com.example.pagoflex.ui.components.BotonSecundario
import com.example.pagoflex.ui.components.TarjetaSeccion
import com.example.pagoflex.ui.theme.Dimens
import com.example.pagoflex.ui.theme.PagoFlexTheme
import com.example.pagoflex.viewmodel.FormularioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioScreen(
    onVolver: () -> Unit = {},
    onSolicitudRegistrada: (String, String) -> Unit = { _, _ -> },
    formularioViewModel: FormularioViewModel = viewModel()
) {
    val uiState by formularioViewModel.uiState.collectAsStateWithLifecycle()
    var fotoBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var permisoCamaraDenegado by remember { mutableStateOf(false) }

    val camaraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        fotoBitmap = bitmap
        formularioViewModel.onComprobanteAdjuntado(bitmap != null)
    }
    val permisoCamaraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { concedido ->
        permisoCamaraDenegado = !concedido
        if (concedido) camaraLauncher.launch()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Registrar Pago", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
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
            TarjetaSeccion(titulo = "Detalle del pago") {
                OutlinedTextField(
                    value = uiState.monto,
                    onValueChange = formularioViewModel::onMontoCambia,
                    label = { Text("Monto a pagar (\$)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    isError = uiState.errorMonto != null,
                    supportingText = { uiState.errorMonto?.let { Text(it) } },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = uiState.concepto,
                    onValueChange = formularioViewModel::onConceptoCambia,
                    label = { Text("Concepto / detalle") },
                    singleLine = true,
                    isError = uiState.errorConcepto != null,
                    supportingText = { uiState.errorConcepto?.let { Text(it) } },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            TarjetaSeccion(titulo = "Comprobante de pago") {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(Dimens.espacioChico)
                ) {
                    if (fotoBitmap != null) {
                        Image(
                            bitmap = fotoBitmap!!.asImageBitmap(),
                            contentDescription = "Foto del comprobante",
                            modifier = Modifier
                                .size(180.dp)
                                .clip(RoundedCornerShape(Dimens.radioTarjeta)),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Text(
                            text = "Adjunta una foto si ya realizaste el pago.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    BotonSecundario(
                        texto = if (fotoBitmap == null) "Tomar foto del comprobante" else "Cambiar foto",
                        onClick = { permisoCamaraLauncher.launch(Manifest.permission.CAMERA) }
                    )
                    if (permisoCamaraDenegado) {
                        Text(
                            text = "Sin permiso de cámara. Puedes continuar sin adjuntar una foto.",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            Text(
                text = "La app valida una solicitud de demostración; todavía no procesa cobros reales.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (uiState.solicitudRegistrada) {
                TarjetaSeccion(titulo = "Solicitud lista") {
                    Text("Monto: \$${uiState.monto}")
                    Text("Concepto: ${uiState.concepto}")
                    Text("Estado: ${uiState.estado.estado}")
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Dimens.espacioChico)
            ) {
                BotonSecundario(
                    texto = "Cancelar",
                    onClick = {
                        formularioViewModel.limpiar()
                        fotoBitmap = null
                        onVolver()
                    },
                    modifier = Modifier.weight(1f)
                )
                BotonPrincipal(
                    texto = if (uiState.solicitudRegistrada) "Ver historial" else "Confirmar solicitud",
                    onClick = {
                        if (uiState.solicitudRegistrada) {
                            onSolicitudRegistrada(uiState.monto, uiState.concepto)
                        } else {
                            formularioViewModel.registrarPago()
                        }
                    },
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(Dimens.espacioChico))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun FormularioScreenPreview() {
    PagoFlexTheme { FormularioScreen() }
}
