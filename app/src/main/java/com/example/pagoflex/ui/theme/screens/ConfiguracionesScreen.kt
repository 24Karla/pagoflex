package com.pagoflex.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfiguracionesScreen(
    onVolver: () -> Unit
) {
    var permitirNotificaciones by remember { mutableStateOf(true) }
    var modoSilenciosoNocturno by remember { mutableStateOf(true) } // RN-15: Nunca notificar entre 22:00 y 08:00

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configuración", fontWeight = FontWeight.Bold) },
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
                text = "Preferencias de la cuenta",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = PagoFlexGreen
            )

            Spacer(modifier = Modifier.height(16.dp))

            //switch activar o desactivar avisos
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Avisos de vencimiento", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            Text("Recibir recordatorios antes del vencimiento", fontSize = 12.sp, color = Color.Gray)
                        }
                        Switch(
                            checked = permitirNotificaciones,
                            onCheckedChange = { permitirNotificaciones = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = PagoFlexGreen
                            )
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFE0E0E0))

                    //switch horario nocturno
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Respetar descanso nocturno", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            Text("Sin avisos entre las 22:00 y las 08:00 hrs", fontSize = 12.sp, color = Color.Gray)
                        }
                        Switch(
                            checked = modoSilenciosoNocturno,
                            onCheckedChange = { modoSilenciosoNocturno = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = PagoFlexGreen
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Privacidad y Seguridad",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = PagoFlexGreen
            )

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Protección de datos (Ley 19.628 / Ley 21.719)",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "PagoFlex no almacena información de tus tarjetas ni cuentas bancarias. Los pagos son simulados para tu seguridad.",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onVolver,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PagoFlexGreen)
            ) {
                Text("Guardar y Volver", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}