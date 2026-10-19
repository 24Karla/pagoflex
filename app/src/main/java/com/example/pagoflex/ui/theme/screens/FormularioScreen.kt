package com.pagoflex.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioScreen(
    folioCompromiso: String = "CP-2026-0101",
    onEnviadoExitoso: () -> Unit,
    onVolver: () -> Unit
) {
    var motivoSeleccionado by remember { mutableStateOf("Ya pagué por otro medio") }
    var comentario by remember { mutableStateOf("") }
    var expandedMenu by remember { mutableStateOf(false) }

    val opcionesMotivos = listOf(
        "Ya pagué por otro medio",
        "No reconozco este cobro",
        "El monto no corresponde",
        "Otro"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reportar Problema", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF5F5F5)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp)
        ) {
            Text(
                text = "¿Algo no cuadra con tus pagos?",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Text(
                text = "Indica el motivo de la incidencia para la cuota $folioCompromiso.",
                fontSize = 13.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
            )

            //seleccion de motivo
            ExposedDropdownMenuBox(
                expanded = expandedMenu,
                onExpandedChange = { expandedMenu = !expandedMenu }
            ) {
                OutlinedTextField(
                    value = motivoSeleccionado,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Motivo de la incidencia") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedMenu) },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = expandedMenu,
                    onDismissRequest = { expandedMenu = false }
                ) {
                    opcionesMotivos.forEach { opcion ->
                        DropdownMenuItem(
                            text = { Text(opcion) },
                            onClick = {
                                motivoSeleccionado = opcion
                                expandedMenu = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            //campo comentario
            OutlinedTextField(
                value = comentario,
                onValueChange = { comentario = it },
                label = { Text("Detalle o comentario adicional") },
                placeholder = { Text("Escribe aquí tu explicación...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 4
            )

            Spacer(modifier = Modifier.weight(1f))

            //boton enviar reporte
            Button(
                onClick = onEnviadoExitoso,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PagoFlexGreen),
                enabled = motivoSeleccionado != "Otro" || comentario.isNotBlank()
            ) {
                Text("Enviar Reporte", color = Color.White, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onVolver,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Cancelar", color = Color.Black)
            }
        }
    }
}