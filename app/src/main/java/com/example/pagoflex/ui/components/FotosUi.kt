package com.example.pagoflex.ui.components

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.pagoflex.data.GestorFotos
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

/** Acciones para elegir una foto: cámara o galería. */
class SelectorFoto internal constructor(
    val tomarFoto: () -> Unit,
    val elegirDeGaleria: () -> Unit
)

/**
 * Prepara la cámara y la galería. La foto elegida se reduce y se guarda en [destino].
 * [onResultado] se llama con exito=true si se guardó, o con un mensaje de error si falló.
 */
@Composable
fun rememberSelectorFoto(
    destino: File,
    onResultado: (exito: Boolean, mensaje: String?) -> Unit
): SelectorFoto {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val destinoActual by rememberUpdatedState(destino)
    val resultadoActual by rememberUpdatedState(onResultado)

    fun procesar(origen: Uri, borrarCaptura: Boolean) {
        scope.launch {
            val exito = withContext(Dispatchers.IO) {
                val ok = GestorFotos.guardarReducida(context, origen, destinoActual)
                if (borrarCaptura) GestorFotos.archivoCaptura(context).delete()
                ok
            }
            resultadoActual(exito, if (exito) null else "No se pudo guardar la foto. Prueba con otra.")
        }
    }

    val camara = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { exito ->
        if (exito) procesar(GestorFotos.uriCaptura(context), true)
        else GestorFotos.archivoCaptura(context).delete()
    }
    val galeria = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) procesar(uri, false)
    }

    return remember {
        SelectorFoto(
            tomarFoto = {
                try {
                    camara.launch(GestorFotos.uriCaptura(context))
                } catch (e: Exception) {
                    resultadoActual(false, "No se pudo abrir la cámara en este dispositivo.")
                }
            },
            elegirDeGaleria = {
                try {
                    galeria.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                } catch (e: Exception) {
                    resultadoActual(false, "No se pudo abrir la galería.")
                }
            }
        )
    }
}

@Composable
private fun rememberFotoBitmap(archivo: File, version: Int): ImageBitmap? {
    var bitmap by remember(archivo.path) { mutableStateOf<ImageBitmap?>(null) }
    LaunchedEffect(archivo.path, version) {
        bitmap = withContext(Dispatchers.IO) {
            if (archivo.exists()) BitmapFactory.decodeFile(archivo.absolutePath)?.asImageBitmap() else null
        }
    }
    return bitmap
}

/** Muestra la foto de perfil si existe; si no, el avatar con iniciales de siempre. */
@Composable
fun AvatarConFoto(
    iniciales: String,
    archivo: File,
    version: Int,
    modifier: Modifier = Modifier,
    tamano: Dp = 96.dp,
    onClick: (() -> Unit)? = null
) {
    val foto = rememberFotoBitmap(archivo, version)
    val clic = if (onClick != null) {
        Modifier.clickable(onClickLabel = "Cambiar foto de perfil", onClick = onClick)
    } else Modifier

    if (foto != null) {
        Image(
            bitmap = foto,
            contentDescription = "Foto de perfil",
            contentScale = ContentScale.Crop,
            modifier = modifier.size(tamano).clip(CircleShape).then(clic)
        )
    } else {
        Box(
            modifier = modifier.clip(CircleShape).then(clic),
            contentAlignment = Alignment.Center
        ) { AvatarCircular(iniciales = iniciales) }
    }
}

/** Miniatura de una foto guardada; al tocarla se amplía. No muestra nada si no hay foto. */
@Composable
fun FotoDeArchivo(
    archivo: File,
    modifier: Modifier = Modifier,
    version: Int = 0,
    descripcion: String = "Foto adjunta"
) {
    val foto = rememberFotoBitmap(archivo, version)
    var ampliada by remember { mutableStateOf(false) }

    if (foto != null) {
        Image(
            bitmap = foto,
            contentDescription = descripcion,
            contentScale = ContentScale.Crop,
            modifier = modifier
                .fillMaxWidth()
                .height(160.dp)
                .clip(RoundedCornerShape(12.dp))
                .clickable(onClickLabel = "Ampliar foto") { ampliada = true }
        )
        if (ampliada) {
            Dialog(
                onDismissRequest = { ampliada = false },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                Image(
                    bitmap = foto,
                    contentDescription = descripcion,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clickable { ampliada = false }
                )
            }
        }
    }
}

/** Foto adjunta a un reporte (la ve el agente). No muestra nada si el reporte no tiene foto. */
@Composable
fun FotoReporte(reporteId: Long, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val archivo = remember(reporteId) { GestorFotos.archivoReporte(context, reporteId) }
    FotoDeArchivo(archivo = archivo, modifier = modifier, descripcion = "Foto adjunta al reporte")
}