package com.example.pagoflex.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pagoflex.ui.theme.Dimens
import com.example.pagoflex.ui.theme.PagoFlexTheme

data class CompromisoEmpresa(
    val folio: String,
    val clienteRut: String,
    val concepto: String,
    val monto: String,
    val estado: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EjecutivoCarteraScreen(
    nombreEmpresa: String = "Colegio San Jorge",
    onNuevoCompromiso: () -> Unit = {},
    onAnularCompromiso: (String) -> Unit = {}
) {
    val cartera = listOf(
        CompromisoEmpresa("CP-101", "RUT 11.111.111-1", "Matrícula - Cuota 4/10", "$65.000", "PENDIENTE"),
        CompromisoEmpresa("CP-102", "RUT 22.222.222-2", "Matrícula - Cuota 4/10", "$65.000", "PAGADO")
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Cartera: $nombreEmpresa", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNuevoCompromiso,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Text("+ Nuevo", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = Dimens.espacioChico))
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(Dimens.espacioPantalla)
        ) {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(Dimens.espacioChico)) {
                items(cartera) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(Dimens.espacioPantalla)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = item.clienteRut, fontWeight = FontWeight.Bold)
                                Surface(
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    shape = RoundedCornerShape(Dimens.espacioMini)
                                ) {
                                    Text(
                                        text = item.estado,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(Dimens.espacioMini))
                            Text(text = item.concepto, style = MaterialTheme.typography.bodyMedium)
                            Text(text = "Folio: ${item.folio} - Monto: ${item.monto}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                            if (item.estado == "PENDIENTE") {
                                Spacer(modifier = Modifier.height(Dimens.espacioChico))
                                OutlinedButton(
                                    onClick = { onAnularCompromiso(item.folio) },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Anular Compromiso (RN-21)", color = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun EjecutivoCarteraScreenPreview() {
    PagoFlexTheme {
        EjecutivoCarteraScreen()
    }
}