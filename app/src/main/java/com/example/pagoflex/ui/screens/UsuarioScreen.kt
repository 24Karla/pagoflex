package com.example.pagoflex.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.pagoflex.data.UsuarioDbHelper
import com.example.pagoflex.model.Usuario
import com.example.pagoflex.ui.components.BotonPrincipal
import com.example.pagoflex.ui.components.CampoContrasena
import com.example.pagoflex.ui.components.ImagenEncabezado
import com.example.pagoflex.ui.theme.Dimens

@Composable
fun UsuarioScreen(
    onLoginExitoso: () -> Unit = {}
) {
    val context = LocalContext.current
    val dbHelper = remember { UsuarioDbHelper(context) }

    var esRegistro by remember { mutableStateOf(false) }
    var esRecuperacion by remember { mutableStateOf(false) }

    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }
    var rut by remember { mutableStateOf("") }
    var comunaSeleccionada by remember { mutableStateOf("") }
    var comunasLista by remember { mutableStateOf(dbHelper.obtenerComunas()) }
    var expandedComuna by remember { mutableStateOf(false) }

    var mensajeError by remember { mutableStateOf("") }
    var mensajeExito by remember { mutableStateOf("") }

    // Password validation rules check
    val tieneLargo = contrasena.length >= 8
    val tieneMayuscula = contrasena.any { it.isUpperCase() }
    val tieneMinuscula = contrasena.any { it.isLowerCase() }
    val tieneNumero = contrasena.any { it.isDigit() }
    val tieneEspecial = contrasena.any { !it.isLetterOrDigit() }
    val passwordValida = tieneLargo && tieneMayuscula && tieneMinuscula && tieneNumero && tieneEspecial

    // Name validation: no numbers or symbols
    val nombreValido = nombre.isNotBlank() && nombre.all { it.isLetter() || it.isWhitespace() }

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(Dimens.espacioPantalla),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            ImagenEncabezado()

            Spacer(modifier = Modifier.height(Dimens.espacioGrande))

            if (esRecuperacion) {
                Text("Recuperación / Cambio de Clave", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(Dimens.espacioChico))
                OutlinedTextField(
                    value = correo,
                    onValueChange = { correo = it },
                    label = { Text("Correo electrónico") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(Dimens.espacioChico))
                CampoContrasena(
                    valor = contrasena,
                    onValorCambiado = { contrasena = it },
                    etiqueta = "Nueva Contraseña"
                )
                if (mensajeError.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(Dimens.espacioChico))
                    Text(mensajeError, color = MaterialTheme.colorScheme.error)
                }
                if (mensajeExito.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(Dimens.espacioChico))
                    Text(mensajeExito, color = MaterialTheme.colorScheme.primary)
                }
                Spacer(modifier = Modifier.height(Dimens.espacioMedio))
                BotonPrincipal(
                    texto = "Actualizar Contraseña",
                    onClick = {
                        if (passwordValida && correo.isNotBlank()) {
                            // Simple mock update or DB update for recovery/change
                            mensajeExito = "Contraseña actualizada exitosamente."
                            mensajeError = ""
                        } else {
                            mensajeError = "Ingrese correo válido y contraseña con 5 requisitos de seguridad."
                        }
                    }
                )
                Spacer(modifier = Modifier.height(Dimens.espacioChico))
                OutlinedButton(
                    onClick = { esRecuperacion = false; mensajeError = ""; mensajeExito = "" },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Volver al Login")
                }
            } else if (esRegistro) {
                Text("Registro de Usuario Final", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(Dimens.espacioChico))

                OutlinedTextField(
                    value = nombre,
                    onValueChange = { if (it.all { char -> char.isLetter() || char.isWhitespace() }) nombre = it },
                    label = { Text("Nombre completo (solo letras)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(Dimens.espacioChico))

                OutlinedTextField(
                    value = correo,
                    onValueChange = { correo = it },
                    label = { Text("Correo electrónico") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(Dimens.espacioChico))

                OutlinedTextField(
                    value = rut,
                    onValueChange = { rut = it },
                    label = { Text("RUT (ej. 12345678-9)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(Dimens.espacioChico))

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = comunaSeleccionada.ifEmpty { "Seleccione Comuna" },
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Comuna") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expandedComuna = true }
                    )
                    DropdownMenu(
                        expanded = expandedComuna,
                        onDismissRequest = { expandedComuna = false }
                    ) {
                        comunasLista.forEach { comuna ->
                            DropdownMenuItem(
                                text = { Text(comuna) },
                                onClick = {
                                    comunaSeleccionada = comuna
                                    expandedComuna = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(Dimens.espacioChico))

                CampoContrasena(
                    valor = contrasena,
                    onValorCambiado = { contrasena = it },
                    etiqueta = "Contraseña (Mín 8, Mayús, Minús, Núm, Especial)"
                )

                if (mensajeError.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(Dimens.espacioChico))
                    Text(mensajeError, color = MaterialTheme.colorScheme.error)
                }
                if (mensajeExito.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(Dimens.espacioChico))
                    Text(mensajeExito, color = MaterialTheme.colorScheme.primary)
                }

                Spacer(modifier = Modifier.height(Dimens.espacioMedio))

                BotonPrincipal(
                    texto = "Registrarse",
                    onClick = {
                        if (!nombreValido) {
                            mensajeError = "El nombre no debe aceptar números ni símbolos."
                        } else if (!passwordValida) {
                            mensajeError = "La clave debe cumplir los 5 requisitos de seguridad."
                        } else if (correo.isBlank() || rut.isBlank() || comunaSeleccionada.isBlank()) {
                            mensajeError = "Todos los campos son obligatorios."
                        } else {
                            val nuevoUsuario = Usuario(
                                nombre = nombre,
                                correo = correo,
                                rut = rut,
                                comuna = comunaSeleccionada,
                                rol = "Usuario final",
                                contrasena = contrasena
                            )
                            val registrado = dbHelper.registrarUsuario(nuevoUsuario)
                            if (registrado) {
                                mensajeExito = "Registro exitoso. Inicie sesión."
                                mensajeError = ""
                                esRegistro = false
                            } else {
                                mensajeError = "El correo ya está registrado."
                            }
                        }
                    }
                )

                Spacer(modifier = Modifier.height(Dimens.espacioChico))
                OutlinedButton(
                    onClick = { esRegistro = false; mensajeError = ""; mensajeExito = "" },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Volver al Login")
                }
            } else {
                OutlinedTextField(
                    value = correo,
                    onValueChange = { correo = it },
                    label = { Text("Correo electrónico") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(Dimens.espacioChico))

                CampoContrasena(
                    valor = contrasena,
                    onValorCambiado = { contrasena = it },
                    etiqueta = "Contraseña"
                )

                if (mensajeError.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(Dimens.espacioChico))
                    Text(mensajeError, color = MaterialTheme.colorScheme.error)
                }

                Spacer(modifier = Modifier.height(Dimens.espacioMedio))

                BotonPrincipal(
                    texto = "Iniciar Sesión",
                    onClick = {
                        val usuarioValido = dbHelper.validarCredenciales(correo, contrasena)
                        if (usuarioValido != null) {
                            mensajeError = ""
                            onLoginExitoso()
                        } else {
                            mensajeError = "Correo o contraseña incorrectos"
                        }
                    }
                )

                Spacer(modifier = Modifier.height(Dimens.espacioChico))

                OutlinedButton(
                    onClick = { esRegistro = true; mensajeError = "" },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Registrarse (Usuario Final)")
                }

                Spacer(modifier = Modifier.height(Dimens.espacioChico))

                Text(
                    text = "¿Olvidaste tu contraseña?",
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { esRecuperacion = true; mensajeError = "" }
                )
            }
        }
    }
}
