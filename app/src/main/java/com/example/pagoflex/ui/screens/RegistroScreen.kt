package com.example.pagoflex.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pagoflex.ui.components.BotonPrincipal
import com.example.pagoflex.ui.components.CampoContrasena
import com.example.pagoflex.ui.components.TarjetaSeccion
import com.example.pagoflex.ui.theme.Dimens
import com.example.pagoflex.ui.theme.PagoFlexTheme
import com.example.pagoflex.viewmodel.UsuariosViewModel
import android.app.Application
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroScreen(
    onVolver: () -> Unit = {},
    onRegistroExitoso: (String) -> Unit = {},
    usuariosViewModel: UsuariosViewModel = run {
        val context = LocalContext.current
        val app = (context.applicationContext as? Application) ?: Application()
        viewModel(
            factory = ViewModelProvider.AndroidViewModelFactory.getInstance(app)
        )
    }
) {
    val state by usuariosViewModel.registroUiState.collectAsStateWithLifecycle()

    LaunchedEffect(state.registroExitoso) {
        if (state.registroExitoso) onRegistroExitoso(state.rolDestino)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Crear cuenta", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    TextButton(onClick = onVolver) { Text("‹ Volver") }
                }
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
            Text(
                text = "Completa tus datos para comenzar.",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            TarjetaSeccion(titulo = "Datos personales") {
                OutlinedTextField(
                    value = state.nombre,
                    onValueChange = usuariosViewModel::onRegistroNombreCambia,
                    label = { Text("Nombre completo") },
                    singleLine = true,
                    isError = state.errorNombre != null,
                    supportingText = { state.errorNombre?.let { Text(it) } },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = state.rut,
                    onValueChange = usuariosViewModel::onRegistroRutCambia,
                    label = { Text("RUT") },
                    placeholder = { Text("12.345.678-5") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii),
                    isError = state.errorRut != null,
                    supportingText = { state.errorRut?.let { Text(it) } },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = state.cuenta,
                    onValueChange = usuariosViewModel::onRegistroCuentaCambia,
                    label = { Text("Correo electrónico") },
                    placeholder = { Text("nombre@correo.com") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    isError = state.errorCuenta != null,
                    supportingText = { state.errorCuenta?.let { Text(it) } },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = state.ciudad,
                    onValueChange = usuariosViewModel::onRegistroCiudadCambia,
                    label = { Text("Ciudad / Comuna") },
                    singleLine = true,
                    isError = state.errorCiudad != null,
                    supportingText = { state.errorCiudad?.let { Text(it) } },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            TarjetaSeccion(titulo = "Seguridad") {
                CampoContrasena(
                    valor = state.contrasena,
                    onValorCambiado = usuariosViewModel::onRegistroContrasenaCambia,
                    etiqueta = "Contraseña"
                )
                state.errorContrasena?.let {
                    Text(it, color = MaterialTheme.colorScheme.error)
                }
                CampoContrasena(
                    valor = state.confirmarContrasena,
                    onValorCambiado = usuariosViewModel::onRegistroConfirmarContrasenaCambia,
                    etiqueta = "Confirmar contraseña"
                )
                state.errorConfirmarContrasena?.let {
                    Text(it, color = MaterialTheme.colorScheme.error)
                }
            }

            state.errorGeneral?.let {
                Text(it, color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
            }

            BotonPrincipal(
                texto = "Crear cuenta",
                onClick = { usuariosViewModel.registrarCuenta() },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(Dimens.espacioChico))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RegistroScreenPreview() {
    PagoFlexTheme { RegistroScreen() }
}