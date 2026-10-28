package com.example.pagoflex.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import kotlin.math.max

object GestorFotos {
    private const val CARPETA_PERFIL = "fotos_perfil"
    private const val CARPETA_REPORTES = "fotos_reportes"
    private const val CARPETA_TEMPORAL = "fotos_temp"
    private const val LADO_MAXIMO_PX = 1280
    private const val CALIDAD_JPEG = 85

    private fun carpeta(base: File, nombre: String): File = File(base, nombre).also { it.mkdirs() }

    fun archivoPerfil(context: Context, usuarioId: Long): File =
        File(carpeta(context.filesDir, CARPETA_PERFIL), "perfil_$usuarioId.jpg")

    fun archivoReporte(context: Context, reporteId: Long): File =
        File(carpeta(context.filesDir, CARPETA_REPORTES), "reporte_$reporteId.jpg")

    fun archivoBorradorReporte(context: Context, usuarioId: Long): File =
        File(carpeta(context.cacheDir, CARPETA_TEMPORAL), "borrador_reporte_$usuarioId.jpg")

    fun archivoCaptura(context: Context): File =
        File(carpeta(context.cacheDir, CARPETA_TEMPORAL), "captura_camara.jpg")

    fun uriCaptura(context: Context): Uri {
        val archivo = archivoCaptura(context)
        if (!archivo.exists()) archivo.createNewFile()
        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", archivo)
    }

    /** Lee la imagen de [origen], la reduce, corrige la rotación y la guarda como JPG en [destino]. */
    fun guardarReducida(context: Context, origen: Uri, destino: File): Boolean {
        val resolver = context.contentResolver
        return try {
            val limites = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            resolver.openInputStream(origen)?.use { BitmapFactory.decodeStream(it, null, limites) }
            if (limites.outWidth <= 0 || limites.outHeight <= 0) return false

            val opciones = BitmapFactory.Options().apply {
                inSampleSize = calcularMuestreo(limites.outWidth, limites.outHeight)
            }
            val original = resolver.openInputStream(origen)?.use { BitmapFactory.decodeStream(it, null, opciones) }
                ?: return false
            val grados = resolver.openInputStream(origen)?.use { leerGradosRotacion(it) } ?: 0

            val ajustado = ajustar(original, grados)
            val exito = escribirJpeg(ajustado, destino)
            ajustado.recycle()
            exito
        } catch (e: Exception) {
            false
        } catch (e: OutOfMemoryError) {
            false
        }
    }

    private fun calcularMuestreo(ancho: Int, alto: Int): Int {
        var muestreo = 1
        while (ancho / (muestreo * 2) >= LADO_MAXIMO_PX && alto / (muestreo * 2) >= LADO_MAXIMO_PX) muestreo *= 2
        return muestreo
    }

    private fun leerGradosRotacion(entrada: InputStream): Int = try {
        when (ExifInterface(entrada).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90
            ExifInterface.ORIENTATION_ROTATE_180 -> 180
            ExifInterface.ORIENTATION_ROTATE_270 -> 270
            else -> 0
        }
    } catch (e: Exception) {
        0
    }

    private fun ajustar(original: Bitmap, grados: Int): Bitmap {
        val escala = LADO_MAXIMO_PX.toFloat() / max(original.width, original.height)
        val matriz = Matrix()
        if (escala < 1f) matriz.postScale(escala, escala)
        if (grados != 0) matriz.postRotate(grados.toFloat())
        if (matriz.isIdentity) return original
        val resultado = Bitmap.createBitmap(original, 0, 0, original.width, original.height, matriz, true)
        if (resultado !== original) original.recycle()
        return resultado
    }

    private fun escribirJpeg(bitmap: Bitmap, destino: File): Boolean {
        destino.parentFile?.mkdirs()
        val temporal = File(destino.parentFile, destino.name + ".tmp")
        val ok = FileOutputStream(temporal).use { bitmap.compress(Bitmap.CompressFormat.JPEG, CALIDAD_JPEG, it) }
        if (!ok) {
            temporal.delete()
            return false
        }
        destino.delete()
        return temporal.renameTo(destino)
    }
}