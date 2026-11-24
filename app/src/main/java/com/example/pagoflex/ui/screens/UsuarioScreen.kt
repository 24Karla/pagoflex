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
import androidx.compose.material3.AlertDialog
import androidx.compose.foundation.Image
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.example.pagoflex.BuildConfig

@Composable
fun UsuarioScreen(
    onLoginExitoso: (String) -> Unit = {},
    onIrARegistro: () -> Unit = {},
    usuariosViewModel: UsuariosViewModel = run {
        val context = LocalContext.current
        val app = (context.applicationContext as? Application) ?: Application()
        viewModel(
            factory = ViewModelProvider.AndroidViewModelFactory.getInstance(app)
        )
    }
) {
    val context = LocalContext.current
    val state by usuariosViewModel.loginUiState.collectAsStateWithLifecycle()
    val permisoRedLocal = "android.permission.ACCESS_LOCAL_NETWORK"
    val solicitarPermiso = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { concedido ->
        if (concedido) usuariosViewModel.iniciarSesion()
        else usuariosViewModel.informarPermisoLoginDenegado()
    }
    LaunchedEffect(state.requierePermisoRed) {
        if (state.requierePermisoRed) solicitarPermiso.launch(permisoRedLocal)
    }
    var recuperar by remember { mutableStateOf(false) }
    var correoRecuperacion by remember { mutableStateOf("") }
    var rutRecuperacion by remember { mutableStateOf("") }
    var claveNueva by remember { mutableStateOf("") }
    var mensajeRecuperacion by remember { mutableStateOf<String?>(null) }

    fun vibrarDispositivo() {
        try {
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(100)
            }
        } catch (e: Exception) {}
    }

    if (recuperar) AlertDialog(
        onDismissRequest = { recuperar = false },
        title = { Text("Recuperar contraseña") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(Dimens.espacioChico)) {
                OutlinedTextField(correoRecuperacion, { correoRecuperacion = it }, label = { Text("Correo") }, singleLine = true)
                OutlinedTextField(rutRecuperacion, { rutRecuperacion = it }, label = { Text("RUT registrado") }, singleLine = true)
                CampoContrasena(
                    valor = claveNueva,
                    onValorCambiado = { claveNueva = it },
                    etiqueta = "Nueva contraseña"
                )
                Text("Mínimo 8 caracteres, mayúscula, minúscula, número y carácter especial.")
                mensajeRecuperacion?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            }
        },
        confirmButton = { TextButton(onClick = {
            vibrarDispositivo()
            val resultado = usuariosViewModel.recuperarContrasena(correoRecuperacion, rutRecuperacion, claveNueva)
            mensajeRecuperacion = resultado.mensaje
            if (resultado.exitoso) recuperar = false
        }) { Text("Actualizar") } },
        dismissButton = { TextButton(onClick = { recuperar = false }) { Text("Cancelar") } }
    )

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

            val logo = if (PreferenciasTema.modoOscuro) R.drawable.logo_inicio_oscuro else R.drawable.logo_inicio
            Image(
                painter = painterResource(logo),
                contentDescription = "Logo de PagoFlex",
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(220.dp)
            )
            Text(
                text = "Iniciar Sesión",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(Modifier.height(Dimens.espacioChico))

            TarjetaSeccion(titulo = "Credenciales de Acceso") {
                OutlinedTextField(
                    value = state.cuenta,
                    enabled = !state.iniciandoSesion,
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
                    etiqueta = "Contraseña",
                    enabled = !state.iniciandoSesion
                )
                state.errorContrasena?.let {
                    Text(it, color = MaterialTheme.colorScheme.error)
                }
                state.errorGeneral?.let {
                    Text(it, color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(Dimens.espacioChico))
                BotonPrincipal(
                    texto = if (state.iniciandoSesion) "Iniciando sesión…" else "Iniciar sesión",
                    enabled = !state.iniciandoSesion,
                    onClick = {
                        vibrarDispositivo()
                        val requierePermiso = Build.VERSION.SDK_INT >= 37 &&
                            BuildConfig.GATEWAY_URL.startsWith("http://") &&
                            context.checkSelfPermission(permisoRedLocal) != PackageManager.PERMISSION_GRANTED
                        usuariosViewModel.iniciarSesion(puedeConsultarServidor = !requierePermiso)
                    }
                )
                TextButton(onClick = onIrARegistro, enabled = !state.iniciandoSesion) {
                    Text("¿No tienes cuenta? Crear cuenta")
                }
                TextButton(
                    onClick = { correoRecuperacion = state.cuenta; mensajeRecuperacion = null; recuperar = true },
                    enabled = !state.iniciandoSesion
                ) {
                    Text("¿Olvidaste tu contraseña?")
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
