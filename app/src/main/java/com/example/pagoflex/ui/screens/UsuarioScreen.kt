package com.example.pagoflex.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.Image
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pagoflex.R
import com.example.pagoflex.ui.components.BotonPrincipal
import com.example.pagoflex.ui.components.BotonSecundario
import com.example.pagoflex.ui.components.CampoContrasena
import com.example.pagoflex.ui.components.TarjetaSeccion
import com.example.pagoflex.ui.theme.Dimens
import com.example.pagoflex.ui.theme.PagoFlexTheme
import com.example.pagoflex.viewmodel.UsuariosViewModel
import android.app.Application
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import com.example.pagoflex.ui.theme.PreferenciasTema

@Composable
fun UsuarioScreen(
    onLoginExitoso: (String) -> Unit = {},
    onIrARegistro: (String) -> Unit = {},
    usuariosViewModel: UsuariosViewModel = run {
        val context = LocalContext.current
        val app = (context.applicationContext as? Application) ?: Application()
        viewModel(
            factory = ViewModelProvider.AndroidViewModelFactory.getInstance(app)
        )
    }
) {
    val state by usuariosViewModel.loginUiState.collectAsStateWithLifecycle()

    LaunchedEffect(state.loginExitoso) {
        if (state.loginExitoso) onLoginExitoso(state.rolDestino)
    }

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(Dimens.espacioPantalla)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimens.espacioChico)
        ) {
            Spacer(Modifier.height(Dimens.espacioMedio))

            val logo = if (PreferenciasTema.modoOscuro) R.drawable.logo_inicio else R.drawable.logo_inicio
            Image(
                painter = painterResource(logo),
                contentDescription = "Logo de PagoFlex",
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(250.dp)
            )
            Text(
                text = "¿Cómo quieres ingresar hoy?",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            BotonSecundario(
                texto = "Soy Usuario Final",
                onClick = { usuariosViewModel.seleccionarRol("usuario") }
            )
            BotonSecundario(
                texto = "Soy Agente PagoFlex",
                onClick = { usuariosViewModel.seleccionarRol("agente") }
            )
            BotonSecundario(
                texto = "Soy Ejecutivo de Empresa",
                onClick = { usuariosViewModel.seleccionarRol("ejecutivo") }
            )

            state.rolSeleccionado?.let { rol ->
                Spacer(Modifier.height(Dimens.espacioChico))
                TarjetaSeccion(titulo = "Ingresar como ${rol.replaceFirstChar { it.uppercase() }}") {
                    OutlinedTextField(
                        value = state.cuenta,
                        onValueChange = usuariosViewModel::onLoginCuentaCambia,
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
                    CampoContrasena(
                        valor = state.contrasena,
                        onValorCambiado = usuariosViewModel::onLoginContrasenaCambia,
                        etiqueta = "Contraseña"
                    )
                    state.errorContrasena?.let {
                        Text(it, color = MaterialTheme.colorScheme.error)
                    }
                    state.errorGeneral?.let {
                        Text(it, color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.height(Dimens.espacioChico))
                    BotonPrincipal(
                        texto = "Iniciar sesión",
                        onClick = { usuariosViewModel.iniciarSesion() }
                    )
                    TextButton(onClick = { onIrARegistro(rol) }) {
                        Text("¿No tienes cuenta? Crear cuenta")
                    }
                }
            }
            Spacer(Modifier.height(Dimens.espacioGrande))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun UsuarioScreenPreview() {
    PagoFlexTheme { UsuarioScreen() }
}
