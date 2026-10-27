package com.example.pagoflex.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pagoflex.ui.components.AvatarCircular
import com.example.pagoflex.ui.components.BotonPrincipal
import com.example.pagoflex.ui.components.BotonSecundario
import com.example.pagoflex.ui.components.TarjetaSeccion
import com.example.pagoflex.ui.theme.Dimens
import com.example.pagoflex.ui.theme.PagoFlexTheme
import com.example.pagoflex.viewmodel.UsuariosViewModel
import android.app.Application
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    onVolver: () -> Unit = {},
    onIrConfiguraciones: () -> Unit = {},
    onCerrarSesion: () -> Unit = {},
    usuariosViewModel: UsuariosViewModel = run {
        val context = LocalContext.current
        val app = (context.applicationContext as? Application) ?: Application()
        viewModel(
            factory = ViewModelProvider.AndroidViewModelFactory.getInstance(app)
        )
    }
) {
    val usuario = remember { usuariosViewModel.obtenerUsuarioSesion() }
    val nombre = usuario?.nombre ?: "Cuenta no disponible"
    val correo = usuario?.cuenta ?: "—"
    val ciudad = usuario?.ciudad?.ifBlank { "—" } ?: "—"
    var rutActual by remember(usuario?.id) { mutableStateOf(usuario?.rut.orEmpty()) }
    var editarRut by remember { mutableStateOf(false) }
    var rutIngresado by remember { mutableStateOf("") }
    var errorRut by remember { mutableStateOf<String?>(null) }
    val rutEnmascarado = rutActual.takeIf { it.length >= 4 }?.let { "${it.take(2)}.*.*-${it.last()}" } ?: "No registrado"
    val iniciales = nombre.split(" ")
        .mapNotNull { it.firstOrNull()?.toString() }
        .take(2)
        .joinToString("")
        .ifEmpty { "PF" }

    if (editarRut) {
        AlertDialog(
            onDismissRequest = { editarRut = false; errorRut = null },
            title = { Text("Añadir o actualizar RUT") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(Dimens.espacioChico)) {
                    OutlinedTextField(
                        value = rutIngresado,
                        onValueChange = { rutIngresado = it.uppercase().filter { caracter -> caracter.isDigit() || caracter == 'K' || caracter == '.' || caracter == '-' }; errorRut = null },
                        label = { Text("RUT") },
                        placeholder = { Text("12.345.678-5") },
                        singleLine = true,
                        isError = errorRut != null
                    )
                    errorRut?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                }
            },
            confirmButton = {
                Button(onClick = {
                    val resultado = usuariosViewModel.actualizarRutCuenta(rutIngresado)
                    if (resultado.exitoso) {
                        rutActual = rutIngresado.uppercase().filter { it.isDigit() || it == 'K' }.let { limpio -> "${limpio.dropLast(1)}-${limpio.last()}" }
                        editarRut = false
                        errorRut = null
                    } else errorRut = resultado.mensaje
                }) { Text("Guardar") }
            },
            dismissButton = { TextButton(onClick = { editarRut = false; errorRut = null }) { Text("Cancelar") } }
        )
    }
    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("Mi Perfil", fontWeight = FontWeight.Bold) }) },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(Dimens.espacioPantalla).verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimens.espacioMedio)
        ) {
            AvatarCircular(iniciales = iniciales)
            Text(text = nombre, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(text = correo, style = MaterialTheme.typography.bodyMedium)
            TarjetaSeccion(titulo = "Información Personal") {
                Text("Ciudad: $ciudad")
                Text("RUT: $rutEnmascarado")
                TextButton(onClick = { rutIngresado = rutActual; errorRut = null; editarRut = true }) {
                    Text(if (rutActual.isBlank()) "Añadir RUT" else "Actualizar RUT")
                }
                Text("Rol: ${usuario?.rolNombre ?: "—"}")
                Text("Cuenta ID: ${usuario?.id ?: "—"}")
            }
            Spacer(modifier = Modifier.height(Dimens.espacioChico))
            BotonPrincipal(texto = "Configuraciones", onClick = onIrConfiguraciones)
            BotonSecundario(texto = "Cerrar sesión", onClick = onCerrarSesion)
            BotonSecundario(texto = "Volver", onClick = onVolver)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PerfilScreenPreview() { PagoFlexTheme { PerfilScreen() } }
