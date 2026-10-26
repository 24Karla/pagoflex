package com.example.pagoflex.notificaciones

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver as AndroidBroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.pagoflex.MainActivity
import com.example.pagoflex.R
import com.example.pagoflex.data.SesionPreferencias
import com.example.pagoflex.data.UsuarioDbHelper
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object PreferenciasAvisos {
    private const val ARCHIVO = "preferencias_avisos"
    private const val CLAVE_ACTIVAS = "activas"
    private const val CLAVE_DIAS = "dias_antes"

    private fun prefs(context: Context) = context.getSharedPreferences(ARCHIVO, Context.MODE_PRIVATE)

    fun activas(context: Context): Boolean = prefs(context).getBoolean(CLAVE_ACTIVAS, false)

    fun guardarActivas(context: Context, activas: Boolean) {
        prefs(context).edit().putBoolean(CLAVE_ACTIVAS, activas).apply()
    }

    fun diasAntes(context: Context): Set<Int> =
        prefs(context).getStringSet(CLAVE_DIAS, setOf("3"))
            .orEmpty()
            .mapNotNull { it.toIntOrNull() }
            .filter { it in setOf(0, 1, 3, 7) }
            .toSet()

    fun guardarDiasAntes(context: Context, dias: Set<Int>) {
        prefs(context).edit()
            .putStringSet(CLAVE_DIAS, dias.filter { it in setOf(0, 1, 3, 7) }.map(Int::toString).toSet())
            .apply()
    }

    fun yaAvisoHoy(context: Context, usuarioId: Long, compromisoId: Long, fecha: String): Boolean =
        prefs(context).getString("ultimo_${usuarioId}_${compromisoId}", null) == fecha

    fun registrarAviso(context: Context, usuarioId: Long, compromisoId: Long, fecha: String) {
        prefs(context).edit().putString("ultimo_${usuarioId}_${compromisoId}", fecha).apply()
    }
}

object AvisosVencimiento {
    private const val CANAL_ID = "vencimientos_pagoflex"
    private const val ACCION = "com.example.pagoflex.ACCION_AVISO_VENCIMIENTO"
    private const val REQUEST_CODE = 4018

    fun crearCanal(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(
                NotificationChannel(CANAL_ID, "Avisos de vencimiento", NotificationManager.IMPORTANCE_DEFAULT).apply {
                    description = "Recordatorios locales de compromisos próximos a vencer."
                }
            )
        }
    }

    fun programar(context: Context) {
        val app = context.applicationContext
        if (!PreferenciasAvisos.activas(app)) {
            cancelar(app)
            return
        }
        crearCanal(app)
        val alarm = app.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendiente = pendingIntent(app)
        val proxima = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 9)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (timeInMillis <= System.currentTimeMillis()) add(Calendar.DAY_OF_YEAR, 1)
        }.timeInMillis
        alarm.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, proxima, pendiente)
    }

    fun cancelar(context: Context) {
        val app = context.applicationContext
        val alarm = app.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarm.cancel(pendingIntent(app))
    }

    private fun pendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, ReceptorAvisosVencimiento::class.java).setAction(ACCION)
        return PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    fun revisarYNotificar(context: Context) {
        val app = context.applicationContext
        if (!PreferenciasAvisos.activas(app)) return
        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(app, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) return
        if (!NotificationManagerCompat.from(app).areNotificationsEnabled()) return

        val hora = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        if (hora < 8 || hora >= 22) return

        val usuarioId = SesionPreferencias(app).leer()
        if (usuarioId <= 0L) return
        val diasConfigurados = PreferenciasAvisos.diasAntes(app)
        if (diasConfigurados.isEmpty()) return

        val helper = UsuarioDbHelper(app)
        val compromisos = try {
            helper.listarCompromisosDeUsuario(usuarioId)
        } finally {
            helper.close()
        }
        val hoy = Calendar.getInstance()
        val claveHoy = SimpleDateFormat("yyyyMMdd", Locale.US).format(Date())
        val formatoFecha = SimpleDateFormat("d 'de' MMMM", Locale("es", "CL"))
        val formatoMonto = NumberFormat.getIntegerInstance(Locale("es", "CL"))
        compromisos
            .filter { it.estado == "Pendiente" || it.estado == "Vencido" }
            .filter { compromiso ->
                diasConfigurados.any { dias ->
                    val recordatorio = Calendar.getInstance().apply {
                        timeInMillis = compromiso.vencimientoMillis
                        add(Calendar.DAY_OF_YEAR, -dias)
                    }
                    mismoDia(recordatorio, hoy)
                }
            }
            .forEach { compromiso ->
                if (PreferenciasAvisos.yaAvisoHoy(app, usuarioId, compromiso.id, claveHoy)) return@forEach
                val fecha = formatoFecha.format(Date(compromiso.vencimientoMillis))
                val titulo = "Próximo vencimiento"
                val texto = "Tienes un compromiso próximo a vencer. Abre PagoFlex para revisar los detalles."
                val abrirApp = Intent(app, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                }
                val abrirPendiente = PendingIntent.getActivity(
                    app, compromiso.id.toInt(), abrirApp,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                val aviso = NotificationCompat.Builder(app, CANAL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle(titulo)
                    .setContentText(texto)
                    .setStyle(NotificationCompat.BigTextStyle().bigText(texto))
                    .setContentIntent(abrirPendiente)
                    .setAutoCancel(true)
                    .build()
                NotificationManagerCompat.from(app).notify(compromiso.id.toInt(), aviso)
                PreferenciasAvisos.registrarAviso(app, usuarioId, compromiso.id, claveHoy)
            }
    }

    private fun mismoDia(a: Calendar, b: Calendar): Boolean =
        a.get(Calendar.YEAR) == b.get(Calendar.YEAR) &&
            a.get(Calendar.DAY_OF_YEAR) == b.get(Calendar.DAY_OF_YEAR)
}

class ReceptorAvisosVencimiento : AndroidBroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val resultado = goAsync()
        Thread {
            try {
                AvisosVencimiento.programar(context)
                AvisosVencimiento.revisarYNotificar(context)
            } finally {
                resultado.finish()
            }
        }.start()
    }
}

class ReceptorReinicio : AndroidBroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (PreferenciasAvisos.activas(context)) AvisosVencimiento.programar(context)
    }
}

