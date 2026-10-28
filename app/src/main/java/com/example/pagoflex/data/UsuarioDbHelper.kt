package com.example.pagoflex.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Patterns
import com.example.pagoflex.model.Rol
import com.example.pagoflex.model.Usuario
import com.example.pagoflex.model.Empresa
import com.example.pagoflex.model.Compromiso
import com.example.pagoflex.model.ComprobantePago
import com.example.pagoflex.model.ReporteIncidencia
import com.example.pagoflex.model.RecomendacionOrganizacion
import com.example.pagoflex.model.EventoAuditoria
import com.example.pagoflex.model.ResultadoPago
import com.example.pagoflex.model.ResultadoOperacion
import com.example.pagoflex.model.PagoCartera
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec


data class PagoUsuario(
    val id: Long,
    val monto: String,
    val concepto: String,
    val fechaMillis: Long,
    val estado: String,
    val empresaNombre: String = "",
    val canal: String = ""
)
class UsuarioDbHelper(context: Context) :
    SQLiteOpenHelper(context, NOMBRE_BD, null, VERSION_BD) {
    companion object {
        private const val NOMBRE_BD = "pagoflex.db"
        private const val VERSION_BD = 10
        private const val TABLA_ROLES = "roles"
        private const val ROL_ID = "id_rol"
        private const val ROL_NOMBRE = "nombre_rol"
        private const val TABLA_USUARIOS = "usuarios"
        private const val USU_ID = "id_usuario"
        private const val USU_NOMBRE = "nombre"
        private const val USU_CIUDAD = "ciudad"
        private const val USU_RUT = "rut"
        private const val USU_CUENTA = "cuenta"
        private const val USU_CONTRASENA = "contrasena"
        private const val USU_SALT = "contrasena_salt"
        private const val USU_ROL = "id_rol"
        private const val TABLA_PAGOS = "pagos"
        private const val PAGO_ID = "id_pago"
        private const val PAGO_USUARIO_ID = "id_usuario"
        private const val PAGO_COMPROMISO_ID = "id_compromiso"
        private const val PAGO_MONTO = "monto"
        private const val PAGO_CONCEPTO = "concepto"
        private const val PAGO_FECHA = "fecha_millis"
        private const val PAGO_ESTADO = "estado"
        private const val TABLA_EMPRESAS = "empresas"
        private const val EMPRESA_ID = "id_empresa"
        private const val EMPRESA_NOMBRE = "nombre"
        private const val EMPRESA_RECARGO = "recargo_fijo_clp"
        private const val EMPRESA_EJECUTIVO_ID = "id_ejecutivo"
        private const val EMPRESA_CUENTA_DEMO_ID = "id_cuenta_demo"
        private const val EMPRESA_ES_DEMO = "es_demo"
        private const val EMPRESA_TELEFONO = "telefono_contacto"
        private const val EMPRESA_CORREO = "correo_contacto"

        private const val TABLA_COMPROMISOS = "compromisos"
        private const val COMPROMISO_ID = "id_compromiso"
        private const val COMPROMISO_USUARIO_ID = "id_usuario"
        private const val COMPROMISO_AGENTE_ID = "id_agente"
        private const val COMPROMISO_EMPRESA_ID = "id_empresa"
        private const val COMPROMISO_CLIENTE = "cliente_nombre"
        private const val COMPROMISO_CONCEPTO = "concepto"
        private const val COMPROMISO_CUOTA = "cuota_actual"
        private const val COMPROMISO_CUOTAS = "cuotas_totales"
        private const val COMPROMISO_MONTO = "monto_clp"
        private const val COMPROMISO_VENCIMIENTO = "vencimiento_millis"
        private const val COMPROMISO_ESTADO = "estado"
        private const val COMPROMISO_RECARGO = "recargo_clp"
        private const val COMPROMISO_CUENTA_DEMO_ID = "id_cuenta_demo"
        private const val COMPROMISO_ES_DEMO = "es_demo"
        private const val COMPROMISO_CREADO = "fecha_creacion_millis"

        private const val TABLA_COMPROBANTES = "comprobantes"
        private const val COMPROBANTE_ID = "id_comprobante"
        private const val COMPROBANTE_PAGO_ID = "id_pago"
        private const val COMPROBANTE_USUARIO_ID = "id_usuario"
        private const val COMPROBANTE_FOLIO = "folio"
        private const val COMPROBANTE_MONTO = "monto_clp"
        private const val COMPROBANTE_CONCEPTO = "concepto"
        private const val COMPROBANTE_CANAL = "canal"
        private const val COMPROBANTE_FECHA = "fecha_millis"
        private const val COMPROBANTE_ES_DEMO = "es_demo"

        private const val TABLA_REPORTES = "reportes"
        private const val REPORTE_ID = "id_reporte"
        private const val REPORTE_USUARIO_ID = "id_usuario"
        private const val REPORTE_COMPROMISO_ID = "id_compromiso"
        private const val REPORTE_EMPRESA_ID = "id_empresa"
        private const val REPORTE_AGENTE_ID = "id_agente_asignado"
        private const val REPORTE_TIPO = "tipo"
        private const val REPORTE_DESCRIPCION = "descripcion"
        private const val REPORTE_ESTADO = "estado"
        private const val REPORTE_RESPUESTA = "respuesta"
        private const val REPORTE_CREADO = "fecha_creacion_millis"
        private const val REPORTE_ACTUALIZADO = "fecha_actualizacion_millis"
        private const val REPORTE_CUENTA_DEMO_ID = "id_cuenta_demo"
        private const val REPORTE_ES_DEMO = "es_demo"

        private const val TABLA_RECOMENDACIONES = "recomendaciones"
        private const val RECOMENDACION_ID = "id_recomendacion"
        private const val RECOMENDACION_AGENTE_ID = "id_agente"
        private const val RECOMENDACION_EMPRESA = "organizacion"
        private const val RECOMENDACION_CONTACTO = "contacto"
        private const val RECOMENDACION_AUTORIZACION = "autorizacion_contacto"
        private const val RECOMENDACION_ESTADO = "estado"
        private const val RECOMENDACION_CREADA = "fecha_creacion_millis"
        private const val RECOMENDACION_ACTUALIZADA = "fecha_actualizacion_millis"
        private const val RECOMENDACION_CUENTA_DEMO_ID = "id_cuenta_demo"
        private const val RECOMENDACION_ES_DEMO = "es_demo"

        private const val TABLA_AUDITORIA = "auditoria"
        private const val AUDITORIA_ID = "id_evento"
        private const val AUDITORIA_ACTOR_ID = "actor_usuario_id"
        private const val AUDITORIA_ENTIDAD = "entidad"
        private const val AUDITORIA_ENTIDAD_ID = "entidad_id"
        private const val AUDITORIA_ACCION = "accion"
        private const val AUDITORIA_DETALLE = "detalle"
        private const val AUDITORIA_FECHA = "fecha_millis"
        private const val AUDITORIA_CUENTA_DEMO_ID = "id_cuenta_demo"
        private const val AUDITORIA_ES_DEMO = "es_demo"

        private const val TABLA_SEMILLAS_DEMO = "semillas_demo"
        private const val SEMILLA_USUARIO_ID = "id_usuario"
        private const val SEMILLA_VERSION = "version_semilla"
        private const val SEMILLA_FECHA = "fecha_carga_millis"
        private const val VERSION_SEMILLA_DEMO = 1

        private const val SQL_CREAR_ROLES =
            "CREATE TABLE $TABLA_ROLES ($ROL_ID INTEGER PRIMARY KEY AUTOINCREMENT, $ROL_NOMBRE TEXT NOT NULL UNIQUE)"
        private const val SQL_CREAR_USUARIOS =
            "CREATE TABLE $TABLA_USUARIOS (" +
                "$USU_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$USU_NOMBRE TEXT NOT NULL, " +
                "$USU_CUENTA TEXT NOT NULL COLLATE NOCASE UNIQUE, " +
                "$USU_CIUDAD TEXT NOT NULL, " +
                "$USU_RUT TEXT NOT NULL DEFAULT '', " +
                "$USU_CONTRASENA TEXT NOT NULL DEFAULT '', " +
                "$USU_SALT TEXT NOT NULL DEFAULT '', " +
                "$USU_ROL INTEGER NOT NULL REFERENCES $TABLA_ROLES($ROL_ID))"
        private const val SQL_PRECARGAR_ROLES =
            "INSERT INTO $TABLA_ROLES ($ROL_ID, $ROL_NOMBRE) VALUES " +
                "(1, 'Agente'), (2, 'Ejecutivo'), (3, 'Usuario Final')"
        private const val SQL_CREAR_PAGOS =
            "CREATE TABLE $TABLA_PAGOS ($PAGO_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$PAGO_USUARIO_ID INTEGER NOT NULL REFERENCES $TABLA_USUARIOS($USU_ID) ON DELETE CASCADE, " +
                "$PAGO_COMPROMISO_ID INTEGER REFERENCES $TABLA_COMPROMISOS($COMPROMISO_ID) ON DELETE SET NULL, " +
                "$PAGO_MONTO TEXT NOT NULL, $PAGO_CONCEPTO TEXT NOT NULL, " +
                "$PAGO_FECHA INTEGER NOT NULL, $PAGO_ESTADO TEXT NOT NULL)"
        private const val SQL_SELECT_USUARIOS =
            "SELECT u.*, r.$ROL_NOMBRE AS $ROL_NOMBRE FROM $TABLA_USUARIOS u " +
                "INNER JOIN $TABLA_ROLES r ON u.$USU_ROL = r.$ROL_ID"
    }

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREAR_ROLES)
        db.execSQL(SQL_CREAR_USUARIOS)
        db.execSQL(SQL_CREAR_PAGOS)
        db.execSQL("CREATE INDEX IF NOT EXISTS indice_pagos_usuario ON $TABLA_PAGOS($PAGO_USUARIO_ID)")
        db.execSQL(SQL_PRECARGAR_ROLES)
        crearTablasComplementarias(db)
        crearTriggerComprobantesInmutables(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE $TABLA_USUARIOS ADD COLUMN $USU_CONTRASENA TEXT NOT NULL DEFAULT ''")
        }
        if (oldVersion < 3) {
            db.execSQL("ALTER TABLE $TABLA_USUARIOS ADD COLUMN $USU_SALT TEXT NOT NULL DEFAULT ''")
            // Delete only the exact demo rows bundled by the previous version.
            db.delete(TABLA_USUARIOS,
                "($USU_CUENTA = ? AND $USU_NOMBRE = ? AND $USU_CONTRASENA = ?) OR " +
                    "($USU_CUENTA = ? AND $USU_NOMBRE = ? AND $USU_CONTRASENA = ?) OR " +
                    "($USU_CUENTA = ? AND $USU_NOMBRE = ? AND $USU_CONTRASENA = ?)",
                arrayOf(
                    "jesus@pago.cl", "Jesus Gonzales", "123456",
                    "agente@pago.cl", "Agente Demostración", "123456",
                    "ejecutivo@pago.cl", "Ejecutivo Demostración", "123456"
                )
            )
            val contrasenasAntiguas = mutableListOf<Pair<Long, String>>()
            db.rawQuery("SELECT $USU_ID, $USU_CONTRASENA FROM $TABLA_USUARIOS", null).use { cursor ->
                while (cursor.moveToNext()) {
                    contrasenasAntiguas.add(cursor.getLong(0) to cursor.getString(1).orEmpty())
                }
            }
            contrasenasAntiguas.forEach { (id, password) ->
                if (password.isNotEmpty()) {
                    val (hash, salt) = protegerContrasena(password)
                    db.update(TABLA_USUARIOS, ContentValues().apply {
                        put(USU_CONTRASENA, hash)
                        put(USU_SALT, salt)
                    }, "$USU_ID = ?", arrayOf(id.toString()))
                }
            }
            db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS indice_cuenta_unica ON $TABLA_USUARIOS($USU_CUENTA COLLATE NOCASE)")
        }
        if (oldVersion < 4) {
            db.execSQL(SQL_CREAR_PAGOS)
            db.execSQL("CREATE INDEX IF NOT EXISTS indice_pagos_usuario ON $TABLA_PAGOS($PAGO_USUARIO_ID)")
        }
        if (oldVersion < 6) {
            crearTablasComplementarias(db)
            sembrarDatosDemoPendientes(db)
        }
        if (oldVersion < 7) crearTriggerComprobantesInmutables(db)
        if (oldVersion < 8) migrarVinculoPagoCompromiso(db)
        if (oldVersion < 9) migrarContactosEmpresa(db)
        if (oldVersion < 10) migrarRutUsuario(db)
    }

    override fun onOpen(db: SQLiteDatabase) {
        super.onOpen(db)
        if (!db.isReadOnly) sembrarDatosDemoPendientes(db)
    }

    private fun sembrarDatosDemoPendientes(db: SQLiteDatabase) {
        val usuarios = mutableListOf<Triple<Long, Long, String>>()
        db.rawQuery("SELECT $USU_ID, $USU_ROL, $USU_NOMBRE FROM $TABLA_USUARIOS", null).use { cursor ->
            while (cursor.moveToNext()) usuarios += Triple(cursor.getLong(0), cursor.getLong(1), cursor.getString(2).orEmpty())
        }
        usuarios.forEach { (id, rol, nombre) -> sembrarDatosDemoUsuario(db, id, rol, nombre) }
    }

    private fun sembrarDatosDemoUsuario(db: SQLiteDatabase, usuarioId: Long, rolId: Long, nombre: String) {
        db.query(
            TABLA_SEMILLAS_DEMO, arrayOf(SEMILLA_USUARIO_ID),
            "$SEMILLA_USUARIO_ID = ?",
            arrayOf(usuarioId.toString()), null, null, null
        ).use { if (it.moveToFirst()) return }

        val propiaTransaccion = !db.inTransaction()
        if (propiaTransaccion) db.beginTransaction()
        try {
            val ahora = System.currentTimeMillis()
            val ajuste = (usuarioId % 9L) * 1_000L
            val dias = { cantidad: Int -> ahora + cantidad.toLong() * 86_400_000L }
            val organizaciones = listOf("Colegio Los Aromos", "Energía Valle Claro", "Gimnasio Horizonte")
            val empresas = organizaciones.map { organizacion ->
                val idExistente = db.query(
                    TABLA_EMPRESAS, arrayOf(EMPRESA_ID),
                    "$EMPRESA_CUENTA_DEMO_ID = ? AND $EMPRESA_NOMBRE = ?",
                    arrayOf(usuarioId.toString(), organizacion), null, null, null
                ).use { cursor -> if (cursor.moveToFirst()) cursor.getLong(0) else -1L }
                if (idExistente > 0L) idExistente else db.insertOrThrow(
                    TABLA_EMPRESAS,
                    null,
                    ContentValues().apply {
                        put(EMPRESA_NOMBRE, organizacion)
                        put(EMPRESA_RECARGO, 2_000L)
                        put(EMPRESA_TELEFONO, "+56 2 2000 0000")
                        put(EMPRESA_CORREO, "contacto@pagoflex.example")
                        if (rolId == Rol.ID_EJECUTIVO) put(EMPRESA_EJECUTIVO_ID, usuarioId)
                        put(EMPRESA_CUENTA_DEMO_ID, usuarioId)
                        put(EMPRESA_ES_DEMO, 1)
                    }
                )
            }
            val conceptos = listOf("Matrícula", "Cuenta mensual", "Mensualidad")
            val montos = listOf(104_000L + ajuste, 56_500L + ajuste, 44_990L + ajuste)
            val fechas = listOf(dias(7), dias(-5), dias(-18))
            val estados = if (rolId == Rol.ID_USUARIO) {
                listOf("Pendiente", "Vencido", "Pagado")
            } else {
                listOf("Pendiente", "Vencido", "Pendiente")
            }
            val idsCompromiso = montos.indices.map { indice ->
                db.insertOrThrow(TABLA_COMPROMISOS, null, ContentValues().apply {
                    if (rolId == Rol.ID_USUARIO) put(COMPROMISO_USUARIO_ID, usuarioId)
                    if (rolId == Rol.ID_AGENTE) put(COMPROMISO_AGENTE_ID, usuarioId)
                    put(COMPROMISO_EMPRESA_ID, empresas[indice])
                    put(COMPROMISO_CLIENTE, if (rolId == Rol.ID_USUARIO) nombre else "Cliente 00${indice + 1}")
                    put(COMPROMISO_CONCEPTO, conceptos[indice])
                    put(COMPROMISO_CUOTA, if (indice == 0) 4 else 1)
                    put(COMPROMISO_CUOTAS, if (indice == 0) 10 else 1)
                    put(COMPROMISO_MONTO, montos[indice])
                    put(COMPROMISO_VENCIMIENTO, fechas[indice])
                    put(COMPROMISO_ESTADO, estados[indice])
                    put(COMPROMISO_RECARGO, if (estados[indice] == "Vencido") 2_000L else 0L)
                    put(COMPROMISO_CREADO, ahora)
                    put(COMPROMISO_CUENTA_DEMO_ID, usuarioId)
                    put(COMPROMISO_ES_DEMO, 1)
                })
            }

            if (rolId == Rol.ID_USUARIO) {
                val tienePago = db.query(
                    TABLA_PAGOS, arrayOf(PAGO_ID), "$PAGO_USUARIO_ID = ?",
                    arrayOf(usuarioId.toString()), null, null, null, "1"
                ).use { it.moveToFirst() }
                if (!tienePago) {
                    val concepto = "Gimnasio Horizonte · Mensualidad"
                    val fechaPago = dias(-16)
                    val pagoId = db.insertOrThrow(TABLA_PAGOS, null, ContentValues().apply {
                        put(PAGO_USUARIO_ID, usuarioId)
                        put(PAGO_COMPROMISO_ID, idsCompromiso[2])
                        put(PAGO_MONTO, montos[2].toString())
                        put(PAGO_CONCEPTO, concepto)
                        put(PAGO_FECHA, fechaPago)
                        put(PAGO_ESTADO, "Pagado")
                    })
                    db.insertOrThrow(TABLA_COMPROBANTES, null, ContentValues().apply {
                        put(COMPROBANTE_PAGO_ID, pagoId)
                        put(COMPROBANTE_USUARIO_ID, usuarioId)
                        put(COMPROBANTE_FOLIO, "PF-DEMO-$usuarioId-001")
                        put(COMPROBANTE_MONTO, montos[2])
                        put(COMPROBANTE_CONCEPTO, concepto)
                        put(COMPROBANTE_CANAL, "App")
                        put(COMPROBANTE_FECHA, fechaPago)
                        put(COMPROBANTE_ES_DEMO, 1)
                    })
                }
                db.insertOrThrow(TABLA_REPORTES, null, ContentValues().apply {
                    put(REPORTE_USUARIO_ID, usuarioId)
                    put(REPORTE_COMPROMISO_ID, idsCompromiso[1])
                    put(REPORTE_EMPRESA_ID, empresas[1])
                    put(REPORTE_TIPO, "Monto incorrecto")
                    put(REPORTE_DESCRIPCION, "Solicito revisar el detalle de mi cuenta mensual.")
                    put(REPORTE_ESTADO, "Abierto")
                    put(REPORTE_CREADO, dias(-1))
                    put(REPORTE_ACTUALIZADO, dias(-1))
                    put(REPORTE_CUENTA_DEMO_ID, usuarioId)
                    put(REPORTE_ES_DEMO, 1)
                })
            }

            if (rolId == Rol.ID_AGENTE) {
                db.insertOrThrow(TABLA_REPORTES, null, ContentValues().apply {
                    put(REPORTE_COMPROMISO_ID, idsCompromiso[1])
                    put(REPORTE_EMPRESA_ID, empresas[1])
                    put(REPORTE_AGENTE_ID, usuarioId)
                    put(REPORTE_TIPO, "Ya pagué")
                    put(REPORTE_DESCRIPCION, "La persona informa que pagó por portal.")
                    put(REPORTE_ESTADO, "En gestión")
                    put(REPORTE_CREADO, dias(-3))
                    put(REPORTE_ACTUALIZADO, dias(-1))
                    put(REPORTE_CUENTA_DEMO_ID, usuarioId)
                    put(REPORTE_ES_DEMO, 1)
                })
                db.insertOrThrow(TABLA_RECOMENDACIONES, null, ContentValues().apply {
                    put(RECOMENDACION_AGENTE_ID, usuarioId)
                    put(RECOMENDACION_EMPRESA, "Mercado Raíz")
                    put(RECOMENDACION_CONTACTO, "Contacto autorizado")
                    put(RECOMENDACION_AUTORIZACION, 1)
                    put(RECOMENDACION_ESTADO, "Recibida")
                    put(RECOMENDACION_CREADA, dias(-2))
                    put(RECOMENDACION_ACTUALIZADA, dias(-2))
                    put(RECOMENDACION_CUENTA_DEMO_ID, usuarioId)
                    put(RECOMENDACION_ES_DEMO, 1)
                })
            }

            db.insertOrThrow(TABLA_AUDITORIA, null, ContentValues().apply {
                put(AUDITORIA_ACTOR_ID, usuarioId)
                put(AUDITORIA_ENTIDAD, "datos_demo")
                put(AUDITORIA_ENTIDAD_ID, usuarioId)
                put(AUDITORIA_ACCION, "CARGA_INICIAL")
                put(AUDITORIA_DETALLE, "Carga inicial de registros para esta cuenta")
                put(AUDITORIA_FECHA, ahora)
                put(AUDITORIA_CUENTA_DEMO_ID, usuarioId)
                put(AUDITORIA_ES_DEMO, 1)
            })
            db.insertOrThrow(TABLA_SEMILLAS_DEMO, null, ContentValues().apply {
                put(SEMILLA_USUARIO_ID, usuarioId)
                put(SEMILLA_VERSION, VERSION_SEMILLA_DEMO)
                put(SEMILLA_FECHA, ahora)
            })
            if (propiaTransaccion) db.setTransactionSuccessful()
        } finally {
            if (propiaTransaccion) db.endTransaction()
        }
    }
    private fun migrarVinculoPagoCompromiso(db: SQLiteDatabase) {
        val existe = db.rawQuery("PRAGMA table_info($TABLA_PAGOS)", null).use { cursor ->
            val nombre = cursor.getColumnIndexOrThrow("name")
            var encontrado = false
            while (cursor.moveToNext()) if (cursor.getString(nombre) == PAGO_COMPROMISO_ID) encontrado = true
            encontrado
        }
        if (!existe) {
            db.execSQL("ALTER TABLE $TABLA_PAGOS ADD COLUMN $PAGO_COMPROMISO_ID INTEGER REFERENCES $TABLA_COMPROMISOS($COMPROMISO_ID) ON DELETE SET NULL")
        }
        db.execSQL(
            "UPDATE $TABLA_PAGOS SET $PAGO_COMPROMISO_ID = (" +
                "SELECT c.$COMPROMISO_ID FROM $TABLA_COMPROMISOS c " +
                "INNER JOIN $TABLA_EMPRESAS e ON c.$COMPROMISO_EMPRESA_ID = e.$EMPRESA_ID " +
                "WHERE c.$COMPROMISO_USUARIO_ID = $TABLA_PAGOS.$PAGO_USUARIO_ID " +
                "AND $TABLA_PAGOS.$PAGO_CONCEPTO = e.$EMPRESA_NOMBRE || ' · ' || c.$COMPROMISO_CONCEPTO " +
                "AND c.$COMPROMISO_ESTADO = 'Pagado' LIMIT 1) " +
                "WHERE $PAGO_ESTADO = 'Pagado' AND $PAGO_COMPROMISO_ID IS NULL"
        )
    }
    private fun migrarRutUsuario(db: SQLiteDatabase) {
        val columnas = mutableSetOf<String>()
        db.rawQuery("PRAGMA table_info($TABLA_USUARIOS)", null).use { cursor ->
            val idx = cursor.getColumnIndexOrThrow("name")
            while (cursor.moveToNext()) columnas += cursor.getString(idx)
        }
        if (USU_RUT !in columnas) db.execSQL("ALTER TABLE $TABLA_USUARIOS ADD COLUMN $USU_RUT TEXT NOT NULL DEFAULT ''")
    }

    private fun migrarContactosEmpresa(db: SQLiteDatabase) {
        val columnas = mutableSetOf<String>()
        db.rawQuery("PRAGMA table_info($TABLA_EMPRESAS)", null).use { cursor ->
            val columnaNombre = cursor.getColumnIndexOrThrow("name")
            while (cursor.moveToNext()) columnas += cursor.getString(columnaNombre)
        }
        if (EMPRESA_TELEFONO !in columnas) db.execSQL("ALTER TABLE $TABLA_EMPRESAS ADD COLUMN $EMPRESA_TELEFONO TEXT NOT NULL DEFAULT ''")
        if (EMPRESA_CORREO !in columnas) db.execSQL("ALTER TABLE $TABLA_EMPRESAS ADD COLUMN $EMPRESA_CORREO TEXT NOT NULL DEFAULT ''")
        db.execSQL("UPDATE $TABLA_EMPRESAS SET $EMPRESA_TELEFONO = '+56 2 2000 0000', $EMPRESA_CORREO = 'contacto@pagoflex.example' WHERE $EMPRESA_ES_DEMO = 1 AND ($EMPRESA_TELEFONO = '' OR $EMPRESA_CORREO = '')")
    }
    private fun crearTriggerComprobantesInmutables(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TRIGGER IF NOT EXISTS comprobantes_inmutables_update " +
                "BEFORE UPDATE ON $TABLA_COMPROBANTES BEGIN " +
                "SELECT RAISE(ABORT, 'Los comprobantes de pago no se pueden modificar'); END"
        )
    }
    private fun crearTablasComplementarias(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS $TABLA_EMPRESAS (" +
                "$EMPRESA_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$EMPRESA_NOMBRE TEXT NOT NULL, " +
                "$EMPRESA_RECARGO INTEGER NOT NULL DEFAULT 0 CHECK($EMPRESA_RECARGO >= 0), " +
                "$EMPRESA_EJECUTIVO_ID INTEGER REFERENCES $TABLA_USUARIOS($USU_ID) ON DELETE SET NULL, " +
                "$EMPRESA_CUENTA_DEMO_ID INTEGER REFERENCES $TABLA_USUARIOS($USU_ID) ON DELETE CASCADE, " +
                "$EMPRESA_ES_DEMO INTEGER NOT NULL DEFAULT 0 CHECK($EMPRESA_ES_DEMO IN (0,1)), " +
                "$EMPRESA_TELEFONO TEXT NOT NULL DEFAULT '', " +
                "$EMPRESA_CORREO TEXT NOT NULL DEFAULT '', " +
                "UNIQUE($EMPRESA_CUENTA_DEMO_ID, $EMPRESA_NOMBRE))"
        )
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS $TABLA_COMPROMISOS (" +
                "$COMPROMISO_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COMPROMISO_USUARIO_ID INTEGER REFERENCES $TABLA_USUARIOS($USU_ID) ON DELETE CASCADE, " +
                "$COMPROMISO_AGENTE_ID INTEGER REFERENCES $TABLA_USUARIOS($USU_ID) ON DELETE SET NULL, " +
                "$COMPROMISO_EMPRESA_ID INTEGER NOT NULL REFERENCES $TABLA_EMPRESAS($EMPRESA_ID) ON DELETE RESTRICT, " +
                "$COMPROMISO_CLIENTE TEXT NOT NULL, $COMPROMISO_CONCEPTO TEXT NOT NULL, " +
                "$COMPROMISO_CUOTA INTEGER NOT NULL DEFAULT 1 CHECK($COMPROMISO_CUOTA > 0), " +
                "$COMPROMISO_CUOTAS INTEGER NOT NULL DEFAULT 1 CHECK($COMPROMISO_CUOTAS >= $COMPROMISO_CUOTA), " +
                "$COMPROMISO_MONTO INTEGER NOT NULL CHECK($COMPROMISO_MONTO >= 0), " +
                "$COMPROMISO_VENCIMIENTO INTEGER NOT NULL, " +
                "$COMPROMISO_ESTADO TEXT NOT NULL CHECK($COMPROMISO_ESTADO IN ('Pendiente','Vencido','Pagado','En revisión','Anulado')), " +
                "$COMPROMISO_RECARGO INTEGER NOT NULL DEFAULT 0 CHECK($COMPROMISO_RECARGO >= 0), " +
                "$COMPROMISO_CREADO INTEGER NOT NULL, " +
                "$COMPROMISO_CUENTA_DEMO_ID INTEGER REFERENCES $TABLA_USUARIOS($USU_ID) ON DELETE CASCADE, " +
                "$COMPROMISO_ES_DEMO INTEGER NOT NULL DEFAULT 0 CHECK($COMPROMISO_ES_DEMO IN (0,1)))"
        )
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS $TABLA_COMPROBANTES (" +
                "$COMPROBANTE_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COMPROBANTE_PAGO_ID INTEGER REFERENCES $TABLA_PAGOS($PAGO_ID) ON DELETE SET NULL, " +
                "$COMPROBANTE_USUARIO_ID INTEGER NOT NULL REFERENCES $TABLA_USUARIOS($USU_ID) ON DELETE CASCADE, " +
                "$COMPROBANTE_FOLIO TEXT NOT NULL UNIQUE, $COMPROBANTE_MONTO INTEGER NOT NULL CHECK($COMPROBANTE_MONTO >= 0), " +
                "$COMPROBANTE_CONCEPTO TEXT NOT NULL, $COMPROBANTE_CANAL TEXT NOT NULL, " +
                "$COMPROBANTE_FECHA INTEGER NOT NULL, $COMPROBANTE_ES_DEMO INTEGER NOT NULL DEFAULT 0 CHECK($COMPROBANTE_ES_DEMO IN (0,1)))"
        )
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS $TABLA_REPORTES (" +
                "$REPORTE_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$REPORTE_USUARIO_ID INTEGER REFERENCES $TABLA_USUARIOS($USU_ID) ON DELETE CASCADE, " +
                "$REPORTE_COMPROMISO_ID INTEGER REFERENCES $TABLA_COMPROMISOS($COMPROMISO_ID) ON DELETE SET NULL, " +
                "$REPORTE_EMPRESA_ID INTEGER REFERENCES $TABLA_EMPRESAS($EMPRESA_ID) ON DELETE SET NULL, " +
                "$REPORTE_AGENTE_ID INTEGER REFERENCES $TABLA_USUARIOS($USU_ID) ON DELETE SET NULL, " +
                "$REPORTE_TIPO TEXT NOT NULL, $REPORTE_DESCRIPCION TEXT NOT NULL, " +
                "$REPORTE_ESTADO TEXT NOT NULL DEFAULT 'Abierto' CHECK($REPORTE_ESTADO IN ('Abierto','En gestión','Resuelto')), " +
                "$REPORTE_RESPUESTA TEXT, $REPORTE_CREADO INTEGER NOT NULL, $REPORTE_ACTUALIZADO INTEGER NOT NULL, " +
                "$REPORTE_CUENTA_DEMO_ID INTEGER REFERENCES $TABLA_USUARIOS($USU_ID) ON DELETE CASCADE, " +
                "$REPORTE_ES_DEMO INTEGER NOT NULL DEFAULT 0 CHECK($REPORTE_ES_DEMO IN (0,1)))"
        )
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS $TABLA_RECOMENDACIONES (" +
                "$RECOMENDACION_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$RECOMENDACION_AGENTE_ID INTEGER NOT NULL REFERENCES $TABLA_USUARIOS($USU_ID) ON DELETE CASCADE, " +
                "$RECOMENDACION_EMPRESA TEXT NOT NULL, $RECOMENDACION_CONTACTO TEXT NOT NULL, " +
                "$RECOMENDACION_AUTORIZACION INTEGER NOT NULL CHECK($RECOMENDACION_AUTORIZACION IN (0,1)), " +
                "$RECOMENDACION_ESTADO TEXT NOT NULL DEFAULT 'Recibida' CHECK($RECOMENDACION_ESTADO IN ('Recibida','Contactada','En evaluación','Se unió','Descartada')), " +
                "$RECOMENDACION_CREADA INTEGER NOT NULL, $RECOMENDACION_ACTUALIZADA INTEGER NOT NULL, " +
                "$RECOMENDACION_CUENTA_DEMO_ID INTEGER REFERENCES $TABLA_USUARIOS($USU_ID) ON DELETE CASCADE, " +
                "$RECOMENDACION_ES_DEMO INTEGER NOT NULL DEFAULT 0 CHECK($RECOMENDACION_ES_DEMO IN (0,1)))"
        )
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS $TABLA_AUDITORIA (" +
                "$AUDITORIA_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$AUDITORIA_ACTOR_ID INTEGER REFERENCES $TABLA_USUARIOS($USU_ID) ON DELETE SET NULL, " +
                "$AUDITORIA_ENTIDAD TEXT NOT NULL, $AUDITORIA_ENTIDAD_ID INTEGER, " +
                "$AUDITORIA_ACCION TEXT NOT NULL, $AUDITORIA_DETALLE TEXT NOT NULL, $AUDITORIA_FECHA INTEGER NOT NULL, " +
                "$AUDITORIA_CUENTA_DEMO_ID INTEGER REFERENCES $TABLA_USUARIOS($USU_ID) ON DELETE CASCADE, " +
                "$AUDITORIA_ES_DEMO INTEGER NOT NULL DEFAULT 0 CHECK($AUDITORIA_ES_DEMO IN (0,1)))"
        )
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS $TABLA_SEMILLAS_DEMO (" +
                "$SEMILLA_USUARIO_ID INTEGER PRIMARY KEY REFERENCES $TABLA_USUARIOS($USU_ID) ON DELETE CASCADE, " +
                "$SEMILLA_VERSION INTEGER NOT NULL, $SEMILLA_FECHA INTEGER NOT NULL)"
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS indice_compromisos_usuario_estado ON $TABLA_COMPROMISOS($COMPROMISO_USUARIO_ID, $COMPROMISO_ESTADO)")
        db.execSQL("CREATE INDEX IF NOT EXISTS indice_compromisos_agente_estado ON $TABLA_COMPROMISOS($COMPROMISO_AGENTE_ID, $COMPROMISO_ESTADO)")
        db.execSQL("CREATE INDEX IF NOT EXISTS indice_compromisos_empresa_vencimiento ON $TABLA_COMPROMISOS($COMPROMISO_EMPRESA_ID, $COMPROMISO_VENCIMIENTO)")
        db.execSQL("CREATE INDEX IF NOT EXISTS indice_reportes_agente_estado_fecha ON $TABLA_REPORTES($REPORTE_AGENTE_ID, $REPORTE_ESTADO, $REPORTE_CREADO)")
        db.execSQL("CREATE INDEX IF NOT EXISTS indice_recomendaciones_agente_estado ON $TABLA_RECOMENDACIONES($RECOMENDACION_AGENTE_ID, $RECOMENDACION_ESTADO)")
        db.execSQL("CREATE INDEX IF NOT EXISTS indice_comprobantes_usuario_fecha ON $TABLA_COMPROBANTES($COMPROBANTE_USUARIO_ID, $COMPROBANTE_FECHA)")
        db.execSQL("CREATE INDEX IF NOT EXISTS indice_empresas_ejecutivo ON $TABLA_EMPRESAS($EMPRESA_EJECUTIVO_ID)")
    }
    fun listarEmpresasDeCuenta(usuarioId: Long): List<Empresa> {
        if (usuarioId <= 0L) return emptyList()
        val resultado = mutableListOf<Empresa>()
        readableDatabase.query(
            TABLA_EMPRESAS, null,
            "$EMPRESA_CUENTA_DEMO_ID = ? OR $EMPRESA_EJECUTIVO_ID = ?",
            arrayOf(usuarioId.toString(), usuarioId.toString()), null, null, EMPRESA_NOMBRE
        ).use { cursor -> while (cursor.moveToNext()) resultado += leerEmpresa(cursor) }
        return resultado
    }

    fun actualizarContactoEmpresa(ejecutivoId: Long, empresaId: Long, telefono: String, correo: String): ResultadoOperacion {
        val telefonoLimpio = telefono.trim()
        val correoLimpio = correo.trim().lowercase()
        if (ejecutivoId <= 0L || empresaId <= 0L) return ResultadoOperacion(false, "No se pudo identificar la organización.")
        if (buscarPorId(ejecutivoId)?.rolId != Rol.ID_EJECUTIVO) return ResultadoOperacion(false, "Solo un Ejecutivo puede editar estos datos.")
        if (telefonoLimpio.isBlank() && correoLimpio.isBlank()) return ResultadoOperacion(false, "Ingresa al menos un teléfono o correo de contacto.")
        val digitosTelefono = telefonoLimpio.count { it.isDigit() }
        if (telefonoLimpio.isNotBlank() && (digitosTelefono !in 8..15 || telefonoLimpio.any { !(it.isDigit() || it in setOf('+', ' ', '-', '(', ')')) })) {
            return ResultadoOperacion(false, "Revisa el teléfono de contacto.")
        }
        if (correoLimpio.isNotBlank() && !Patterns.EMAIL_ADDRESS.matcher(correoLimpio).matches()) {
            return ResultadoOperacion(false, "Ingresa un correo de contacto válido.")
        }
        val db = writableDatabase
        db.beginTransaction()
        try {
            val empresa = db.query(TABLA_EMPRESAS, arrayOf(EMPRESA_CUENTA_DEMO_ID, EMPRESA_ES_DEMO),
                "$EMPRESA_ID = ? AND $EMPRESA_EJECUTIVO_ID = ?", arrayOf(empresaId.toString(), ejecutivoId.toString()), null, null, null
            ).use { cursor ->
                if (!cursor.moveToFirst()) return ResultadoOperacion(false, "La organización no pertenece a esta cuenta ejecutiva.")
                (if (cursor.isNull(0)) null else cursor.getLong(0)) to (cursor.getInt(1) == 1)
            }
            val filas = db.update(TABLA_EMPRESAS, ContentValues().apply {
                put(EMPRESA_TELEFONO, telefonoLimpio)
                put(EMPRESA_CORREO, correoLimpio)
            }, "$EMPRESA_ID = ? AND $EMPRESA_EJECUTIVO_ID = ?", arrayOf(empresaId.toString(), ejecutivoId.toString()))
            if (filas != 1) return ResultadoOperacion(false, "No se pudieron guardar los datos de contacto.")
            val ahora = System.currentTimeMillis()
            db.insertOrThrow(TABLA_AUDITORIA, null, ContentValues().apply {
                put(AUDITORIA_ACTOR_ID, ejecutivoId); put(AUDITORIA_ENTIDAD, "empresa")
                put(AUDITORIA_ENTIDAD_ID, empresaId); put(AUDITORIA_ACCION, "CONTACTO_ACTUALIZADO")
                put(AUDITORIA_DETALLE, "Datos de contacto de la empresa actualizados")
                put(AUDITORIA_FECHA, ahora)
                empresa.first?.let { put(AUDITORIA_CUENTA_DEMO_ID, it) }
                put(AUDITORIA_ES_DEMO, if (empresa.second) 1 else 0)
            })
            db.setTransactionSuccessful()
            return ResultadoOperacion(true, "Contacto actualizado.")
        } catch (e: Exception) {
            return ResultadoOperacion(false, "No se pudieron guardar los datos de contacto.")
        } finally {
            db.endTransaction()
        }
    }
    fun listarCompromisosDeUsuario(usuarioId: Long): List<Compromiso> =
        listarCompromisos("c.$COMPROMISO_USUARIO_ID = ?", usuarioId)

    fun buscarCompromisoDeUsuario(usuarioId: Long, compromisoId: Long): Compromiso? {
        if (usuarioId <= 0L || compromisoId <= 0L) return null
        return buscarCompromisoEnBase(readableDatabase, usuarioId, compromisoId)
    }

    fun pagarCompromiso(usuarioId: Long, compromisoId: Long, canal: String = "App"): ResultadoPago {
        if (usuarioId <= 0L || compromisoId <= 0L) return ResultadoPago(false, "No se pudo identificar el compromiso.")
        if (canal !in setOf("App", "Portal", "Agente conversacional")) return ResultadoPago(false, "Selecciona un canal de pago válido.")
        val db = writableDatabase
        db.beginTransaction()
        try {
            val compromiso = buscarCompromisoEnBase(db, usuarioId, compromisoId)
                ?: return ResultadoPago(false, "Este compromiso no pertenece a tu cuenta.")
            if (compromiso.estado !in setOf("Pendiente", "Vencido")) {
                val motivo = when (compromiso.estado) {
                    "Pagado" -> "Este compromiso ya está pagado."
                    "En revisión" -> "Este compromiso está en revisión y no se puede pagar todavía."
                    "Anulado" -> "Este compromiso fue anulado."
                    else -> "Este compromiso no está disponible para pago."
                }
                return ResultadoPago(false, motivo)
            }
            val anteriores = db.rawQuery(
                "SELECT COUNT(*) FROM $TABLA_COMPROMISOS WHERE $COMPROMISO_USUARIO_ID = ? AND $COMPROMISO_EMPRESA_ID = ? " +
                    "AND ($COMPROMISO_VENCIMIENTO < ? OR ($COMPROMISO_VENCIMIENTO = ? AND $COMPROMISO_ID < ?)) " +
                    "AND $COMPROMISO_ESTADO NOT IN ('Pagado','Anulado')",
                arrayOf(usuarioId.toString(), compromiso.empresaId.toString(), compromiso.vencimientoMillis.toString(),
                    compromiso.vencimientoMillis.toString(), compromiso.id.toString())
            ).use { cursor -> cursor.moveToFirst() && cursor.getInt(0) > 0 }
            if (anteriores) return ResultadoPago(false, "Paga primero el compromiso anterior de esta organización.")

            val total = compromiso.montoClp + compromiso.recargoClp
            val ahora = System.currentTimeMillis()
            val concepto = "${compromiso.empresaNombre} · ${compromiso.concepto}"
            val pagoId = db.insertOrThrow(TABLA_PAGOS, null, ContentValues().apply {
                put(PAGO_USUARIO_ID, usuarioId); put(PAGO_COMPROMISO_ID, compromisoId); put(PAGO_MONTO, total.toString())
                put(PAGO_CONCEPTO, concepto); put(PAGO_FECHA, ahora); put(PAGO_ESTADO, "Pagado")
            })
            val filasActualizadas = db.update(TABLA_COMPROMISOS,
                ContentValues().apply { put(COMPROMISO_ESTADO, "Pagado") },
                "$COMPROMISO_ID = ? AND $COMPROMISO_USUARIO_ID = ? AND $COMPROMISO_ESTADO IN ('Pendiente','Vencido')",
                arrayOf(compromisoId.toString(), usuarioId.toString()))
            if (filasActualizadas != 1) throw IllegalStateException("El compromiso cambió durante el pago")
            val folio = "PF-${pagoId.toString().padStart(8, '0')}"
            db.insertOrThrow(TABLA_COMPROBANTES, null, ContentValues().apply {
                put(COMPROBANTE_PAGO_ID, pagoId); put(COMPROBANTE_USUARIO_ID, usuarioId)
                put(COMPROBANTE_FOLIO, folio); put(COMPROBANTE_MONTO, total)
                put(COMPROBANTE_CONCEPTO, concepto); put(COMPROBANTE_CANAL, canal)
                put(COMPROBANTE_FECHA, ahora); put(COMPROBANTE_ES_DEMO, if (compromiso.esDemo) 1 else 0)
            })
            db.insert(TABLA_AUDITORIA, null, ContentValues().apply {
                put(AUDITORIA_ACTOR_ID, usuarioId); put(AUDITORIA_ENTIDAD, "compromiso")
                put(AUDITORIA_ENTIDAD_ID, compromisoId); put(AUDITORIA_ACCION, "PAGO_SIMULADO")
                put(AUDITORIA_DETALLE, "Pago registrado por $total CLP; comprobante $folio")
                put(AUDITORIA_FECHA, ahora)
                compromiso.cuentaDemoId?.let { put(AUDITORIA_CUENTA_DEMO_ID, it) }
                put(AUDITORIA_ES_DEMO, if (compromiso.esDemo) 1 else 0)
            })
            db.setTransactionSuccessful()
            return ResultadoPago(true, "Pago registrado correctamente.", folio)
        } catch (e: Exception) {
            return ResultadoPago(false, "No se pudo registrar el pago. Intenta nuevamente.")
        } finally {
            db.endTransaction()
        }
    }

    private fun buscarCompromisoEnBase(db: SQLiteDatabase, usuarioId: Long, compromisoId: Long): Compromiso? {
        val sql = "SELECT c.*, e.$EMPRESA_NOMBRE AS nombre_empresa, e.$EMPRESA_RECARGO AS recargo_empresa, " +
            "e.$EMPRESA_TELEFONO AS telefono_empresa, e.$EMPRESA_CORREO AS correo_empresa FROM $TABLA_COMPROMISOS c " +
            "INNER JOIN $TABLA_EMPRESAS e ON c.$COMPROMISO_EMPRESA_ID = e.$EMPRESA_ID " +
            "WHERE c.$COMPROMISO_USUARIO_ID = ? AND c.$COMPROMISO_ID = ? LIMIT 1"
        return db.rawQuery(sql, arrayOf(usuarioId.toString(), compromisoId.toString())).use { cursor ->
            if (cursor.moveToFirst()) leerCompromiso(cursor) else null
        }
    }
    fun listarCompromisosDeAgente(agenteId: Long): List<Compromiso> =
        listarCompromisos("c.$COMPROMISO_AGENTE_ID = ?", agenteId)

    fun crearCompromisoDeEjecutivo(
        ejecutivoId: Long,
        empresaId: Long,
        cuentaCliente: String,
        concepto: String,
        cuotaActual: Int,
        cuotasTotales: Int,
        montoClp: Long,
        vencimientoMillis: Long
    ): ResultadoOperacion {
        if (ejecutivoId <= 0L || empresaId <= 0L) return ResultadoOperacion(false, "No se pudo identificar la empresa.")
        if (buscarPorId(ejecutivoId)?.rolId != Rol.ID_EJECUTIVO) return ResultadoOperacion(false, "Solo un Ejecutivo puede registrar compromisos.")
        if (concepto.isBlank() || montoClp <= 0L || cuotaActual <= 0 || cuotasTotales <= 0 || cuotaActual > cuotasTotales || vencimientoMillis <= 0L) {
            return ResultadoOperacion(false, "Revisa concepto, monto, cuotas y fecha de vencimiento.")
        }
        val db = writableDatabase
        db.beginTransaction()
        try {
            val cliente = db.rawQuery(
                "SELECT $USU_ID, $USU_NOMBRE FROM $TABLA_USUARIOS WHERE $USU_CUENTA = ? COLLATE NOCASE AND $USU_ROL = ? LIMIT 1",
                arrayOf(cuentaCliente.trim(), Rol.ID_USUARIO.toString())
            ).use { cursor ->
                if (!cursor.moveToFirst()) return ResultadoOperacion(false, "No existe una cuenta de Usuario Final con ese correo.")
                cursor.getLong(0) to cursor.getString(1)
            }
            val empresa = db.rawQuery(
                "SELECT $EMPRESA_EJECUTIVO_ID, $EMPRESA_RECARGO, $EMPRESA_CUENTA_DEMO_ID, $EMPRESA_ES_DEMO " +
                    "FROM $TABLA_EMPRESAS WHERE $EMPRESA_ID = ? LIMIT 1",
                arrayOf(empresaId.toString())
            ).use { cursor ->
                if (!cursor.moveToFirst() || cursor.isNull(0) || cursor.getLong(0) != ejecutivoId) {
                    return ResultadoOperacion(false, "La organización no pertenece a esta cuenta ejecutiva.")
                }
                Triple(cursor.getLong(1), if (cursor.isNull(2)) null else cursor.getLong(2), cursor.getInt(3) == 1)
            }
            val id = db.insertOrThrow(TABLA_COMPROMISOS, null, ContentValues().apply {
                put(COMPROMISO_USUARIO_ID, cliente.first)
                put(COMPROMISO_EMPRESA_ID, empresaId)
                put(COMPROMISO_CLIENTE, cliente.second)
                put(COMPROMISO_CONCEPTO, concepto.trim())
                put(COMPROMISO_CUOTA, cuotaActual)
                put(COMPROMISO_CUOTAS, cuotasTotales)
                put(COMPROMISO_MONTO, montoClp)
                put(COMPROMISO_VENCIMIENTO, vencimientoMillis)
                put(COMPROMISO_ESTADO, "Pendiente")
                put(COMPROMISO_RECARGO, 0L)
                put(COMPROMISO_CREADO, System.currentTimeMillis())
                empresa.second?.let { put(COMPROMISO_CUENTA_DEMO_ID, it) }
                put(COMPROMISO_ES_DEMO, if (empresa.third) 1 else 0)
            })
            db.insertOrThrow(TABLA_AUDITORIA, null, ContentValues().apply {
                put(AUDITORIA_ACTOR_ID, ejecutivoId)
                put(AUDITORIA_ENTIDAD, "compromiso")
                put(AUDITORIA_ENTIDAD_ID, id)
                put(AUDITORIA_ACCION, "CREACION_EJECUTIVA")
                put(AUDITORIA_DETALLE, "Compromiso '${concepto.trim()}' creado para ${cliente.second}; empresa $empresaId; monto $montoClp CLP")
                put(AUDITORIA_FECHA, System.currentTimeMillis())
                empresa.second?.let { put(AUDITORIA_CUENTA_DEMO_ID, it) }
                put(AUDITORIA_ES_DEMO, if (empresa.third) 1 else 0)
            })
            db.setTransactionSuccessful()
            return ResultadoOperacion(true, "Compromiso registrado para ${cliente.second}.")
        } catch (e: Exception) {
            return ResultadoOperacion(false, "No se pudo registrar el compromiso. Intenta nuevamente.")
        } finally {
            db.endTransaction()
        }
    }

    fun anularCompromisoDeEjecutivo(ejecutivoId: Long, compromisoId: Long, motivo: String): ResultadoOperacion {
        val motivoLimpio = motivo.trim()
        val motivosPermitidos = setOf("Repactación", "Error", "Pagado directo", "Otro")
        if (ejecutivoId <= 0L || compromisoId <= 0L) return ResultadoOperacion(false, "No se pudo identificar el compromiso.")
        if (buscarPorId(ejecutivoId)?.rolId != Rol.ID_EJECUTIVO) return ResultadoOperacion(false, "Solo un Ejecutivo puede anular compromisos.")
        if (motivoLimpio !in motivosPermitidos && !motivoLimpio.startsWith("Otro: ")) {
            return ResultadoOperacion(false, "Selecciona un motivo válido para anular.")
        }
        if (motivoLimpio == "Otro" || motivoLimpio == "Otro: ") return ResultadoOperacion(false, "Escribe el motivo de la anulación.")
        val db = writableDatabase
        db.beginTransaction()
        try {
            val compromiso = db.rawQuery(
                "SELECT c.$COMPROMISO_ESTADO, c.$COMPROMISO_CUENTA_DEMO_ID, c.$COMPROMISO_ES_DEMO " +
                    "FROM $TABLA_COMPROMISOS c INNER JOIN $TABLA_EMPRESAS e ON c.$COMPROMISO_EMPRESA_ID = e.$EMPRESA_ID " +
                    "WHERE c.$COMPROMISO_ID = ? AND e.$EMPRESA_EJECUTIVO_ID = ? LIMIT 1",
                arrayOf(compromisoId.toString(), ejecutivoId.toString())
            ).use { cursor ->
                if (!cursor.moveToFirst()) return ResultadoOperacion(false, "El compromiso no pertenece a tu cartera.")
                Triple(cursor.getString(0), if (cursor.isNull(1)) null else cursor.getLong(1), cursor.getInt(2) == 1)
            }
            if (compromiso.first == "Pagado") return ResultadoOperacion(false, "No se puede anular un compromiso que ya fue pagado.")
            if (compromiso.first == "Anulado") return ResultadoOperacion(false, "Este compromiso ya está anulado.")
            val ahora = System.currentTimeMillis()
            val filas = db.update(
                TABLA_COMPROMISOS,
                ContentValues().apply { put(COMPROMISO_ESTADO, "Anulado") },
                "$COMPROMISO_ID = ? AND $COMPROMISO_ESTADO NOT IN ('Pagado','Anulado')",
                arrayOf(compromisoId.toString())
            )
            if (filas != 1) return ResultadoOperacion(false, "El compromiso cambió y no se pudo anular.")
            db.insertOrThrow(TABLA_AUDITORIA, null, ContentValues().apply {
                put(AUDITORIA_ACTOR_ID, ejecutivoId)
                put(AUDITORIA_ENTIDAD, "compromiso")
                put(AUDITORIA_ENTIDAD_ID, compromisoId)
                put(AUDITORIA_ACCION, "ANULACION_EJECUTIVA")
                put(AUDITORIA_DETALLE, "Compromiso anulado. Motivo: $motivoLimpio")
                put(AUDITORIA_FECHA, ahora)
                compromiso.second?.let { put(AUDITORIA_CUENTA_DEMO_ID, it) }
                put(AUDITORIA_ES_DEMO, if (compromiso.third) 1 else 0)
            })
            db.setTransactionSuccessful()
            return ResultadoOperacion(true, "Compromiso anulado. Motivo: $motivoLimpio")
        } catch (e: Exception) {
            return ResultadoOperacion(false, "No se pudo anular el compromiso. Intenta nuevamente.")
        } finally {
            db.endTransaction()
        }
    }
    fun listarCompromisosDeEjecutivo(ejecutivoId: Long): List<Compromiso> =
        listarCompromisos("e.$EMPRESA_EJECUTIVO_ID = ?", ejecutivoId)

    fun listarPagosDeCartera(ejecutivoId: Long, inicioMillis: Long, finMillis: Long): List<PagoCartera> {
        if (ejecutivoId <= 0L || finMillis <= inicioMillis) return emptyList()
        val sql = "SELECT p.$PAGO_ID, c.$COMPROMISO_EMPRESA_ID, p.$PAGO_MONTO, p.$PAGO_FECHA, e.$EMPRESA_NOMBRE, " +
            "CASE WHEN p.$PAGO_FECHA <= c.$COMPROMISO_VENCIMIENTO THEN 1 ELSE 0 END " +
            "FROM $TABLA_PAGOS p " +
            "INNER JOIN $TABLA_COMPROMISOS c ON p.$PAGO_COMPROMISO_ID = c.$COMPROMISO_ID " +
            "INNER JOIN $TABLA_EMPRESAS e ON c.$COMPROMISO_EMPRESA_ID = e.$EMPRESA_ID " +
            "WHERE e.$EMPRESA_EJECUTIVO_ID = ? AND p.$PAGO_ESTADO = 'Pagado' " +
            "AND p.$PAGO_FECHA >= ? AND p.$PAGO_FECHA < ? ORDER BY p.$PAGO_FECHA DESC"
        return readableDatabase.rawQuery(sql, arrayOf(ejecutivoId.toString(), inicioMillis.toString(), finMillis.toString())).use { cursor ->
            buildList {
                while (cursor.moveToNext()) add(
                    PagoCartera(
                        id = cursor.getLong(0),
                        empresaId = cursor.getLong(1),
                        montoClp = cursor.getString(2)?.toLongOrNull() ?: 0L,
                        fechaMillis = cursor.getLong(3),
                        empresaNombre = cursor.getString(4),
                        aTiempo = cursor.getInt(5) == 1
                    )
                )
            }
        }
    }
    private fun listarCompromisos(filtro: String, usuarioId: Long): List<Compromiso> {
        if (usuarioId <= 0L) return emptyList()
        val sql = "SELECT c.*, e.$EMPRESA_NOMBRE AS nombre_empresa, e.$EMPRESA_RECARGO AS recargo_empresa, " +
            "e.$EMPRESA_TELEFONO AS telefono_empresa, e.$EMPRESA_CORREO AS correo_empresa FROM $TABLA_COMPROMISOS c " +
            "INNER JOIN $TABLA_EMPRESAS e ON c.$COMPROMISO_EMPRESA_ID = e.$EMPRESA_ID " +
            "WHERE $filtro ORDER BY c.$COMPROMISO_VENCIMIENTO ASC"
        return consultar(sql, arrayOf(usuarioId.toString()), ::leerCompromiso)
    }

    fun obtenerResumenCumplimiento(usuarioId: Long): com.example.pagoflex.model.ResumenCumplimiento {
        if (usuarioId <= 0L) return com.example.pagoflex.model.ResumenCumplimiento(0, 0, 0.0, "Historial insuficiente")
        val ahora = System.currentTimeMillis()
        val inicio = java.util.Calendar.getInstance().apply { timeInMillis = ahora; add(java.util.Calendar.MONTH, -12) }.timeInMillis
        val conteos = readableDatabase.rawQuery(
            "SELECT COUNT(DISTINCT c.$COMPROMISO_ID), " +
                "COUNT(DISTINCT CASE WHEN p.$PAGO_ID IS NOT NULL AND p.$PAGO_FECHA <= c.$COMPROMISO_VENCIMIENTO THEN c.$COMPROMISO_ID END) " +
                "FROM $TABLA_COMPROMISOS c LEFT JOIN $TABLA_PAGOS p ON p.$PAGO_COMPROMISO_ID = c.$COMPROMISO_ID AND p.$PAGO_ESTADO = 'Pagado' " +
                "WHERE c.$COMPROMISO_USUARIO_ID = ? AND c.$COMPROMISO_VENCIMIENTO >= ? AND c.$COMPROMISO_VENCIMIENTO < ? " +
                "AND c.$COMPROMISO_ESTADO NOT IN ('Anulado','En revisión')",
            arrayOf(usuarioId.toString(), inicio.toString(), ahora.toString())
        ).use { cursor -> if (cursor.moveToFirst()) cursor.getInt(0) to cursor.getInt(1) else 0 to 0 }
        val porcentaje = if (conteos.first == 0) 0.0 else conteos.second * 100.0 / conteos.first
        val categoria = when {
            conteos.first < 3 -> "Historial insuficiente"
            porcentaje >= 95.0 -> "Ejemplar"
            porcentaje >= 80.0 -> "Al día"
            else -> "En camino"
        }
        return com.example.pagoflex.model.ResumenCumplimiento(conteos.first, conteos.second, porcentaje, categoria)
    }
    fun listarComprobantesDeUsuario(usuarioId: Long): List<ComprobantePago> =
        consultarTabla(
            TABLA_COMPROBANTES,
            "$COMPROBANTE_USUARIO_ID = ?",
            arrayOf(usuarioId.toString()),
            "$COMPROBANTE_FECHA DESC",
            ::leerComprobante
        )

    fun listarReportesDeUsuario(usuarioId: Long): List<ReporteIncidencia> =
        consultarTabla(TABLA_REPORTES, "$REPORTE_USUARIO_ID = ?", arrayOf(usuarioId.toString()), "$REPORTE_CREADO DESC", ::leerReporte)

    fun listarReportesDeAgente(agenteId: Long): List<ReporteIncidencia> {
        if (buscarPorId(agenteId)?.rolId != Rol.ID_AGENTE) return emptyList()
        val sql = "SELECT r.*, e.$EMPRESA_NOMBRE AS nombre_empresa FROM $TABLA_REPORTES r " +
            "LEFT JOIN $TABLA_EMPRESAS e ON r.$REPORTE_EMPRESA_ID = e.$EMPRESA_ID " +
            "ORDER BY CASE r.$REPORTE_ESTADO WHEN 'Abierto' THEN 0 WHEN 'En gestión' THEN 1 ELSE 2 END, r.$REPORTE_CREADO ASC"
        val reportes = mutableListOf<ReporteIncidencia>()
        readableDatabase.rawQuery(sql, null).use { cursor ->
            while (cursor.moveToNext()) reportes += leerReporte(cursor)
        }
        return reportes
    }

    fun actualizarReporteDeAgente(agenteId: Long, reporteId: Long, nuevoEstado: String, respuesta: String? = null): Boolean {
        if (buscarPorId(agenteId)?.rolId != Rol.ID_AGENTE || reporteId <= 0L || nuevoEstado !in setOf("En gestión", "Resuelto")) return false
        val textoRespuesta = respuesta?.trim().orEmpty()
        val db = writableDatabase
        db.beginTransaction()
        try {
            val actual = db.rawQuery(
                "SELECT r.$REPORTE_ESTADO, r.$REPORTE_CUENTA_DEMO_ID, r.$REPORTE_ES_DEMO, r.$REPORTE_AGENTE_ID " +
                    "FROM $TABLA_REPORTES r WHERE r.$REPORTE_ID = ? AND (r.$REPORTE_AGENTE_ID IS NULL OR r.$REPORTE_AGENTE_ID = ?) LIMIT 1",
                arrayOf(reporteId.toString(), agenteId.toString())
            ).use { cursor ->
                if (!cursor.moveToFirst()) return false
                Triple(cursor.getString(0), if (cursor.isNull(1)) null else cursor.getLong(1), cursor.getInt(2) == 1) to
                    if (cursor.isNull(3)) null else cursor.getLong(3)
            }
            val estado = actual.first.first
            val permitido = when {
                estado == "Abierto" && nuevoEstado == "En gestión" -> true
                estado == "En gestión" && nuevoEstado == "En gestión" && textoRespuesta.isNotBlank() -> true
                estado == "En gestión" && nuevoEstado == "Resuelto" && textoRespuesta.isNotBlank() -> true
                else -> false
            }
            if (!permitido) return false
            val ahora = System.currentTimeMillis()
            val cambios = ContentValues().apply {
                put(REPORTE_ESTADO, nuevoEstado)
                put(REPORTE_ACTUALIZADO, ahora)
                put(REPORTE_AGENTE_ID, agenteId)
                if (textoRespuesta.isNotBlank()) put(REPORTE_RESPUESTA, textoRespuesta)
            }
            val filas = db.update(TABLA_REPORTES, cambios,
                "$REPORTE_ID = ? AND ($REPORTE_AGENTE_ID IS NULL OR $REPORTE_AGENTE_ID = ?) AND $REPORTE_ESTADO = ?",
                arrayOf(reporteId.toString(), agenteId.toString(), estado))
            if (filas != 1) return false
            db.insertOrThrow(TABLA_AUDITORIA, null, ContentValues().apply {
                put(AUDITORIA_ACTOR_ID, agenteId); put(AUDITORIA_ENTIDAD, "reporte")
                put(AUDITORIA_ENTIDAD_ID, reporteId); put(AUDITORIA_ACCION, nuevoEstado.uppercase())
                put(AUDITORIA_DETALLE, if (textoRespuesta.isBlank()) "Agente tomó el caso" else "Agente actualizó el reporte: $textoRespuesta")
                put(AUDITORIA_FECHA, ahora)
                actual.first.second?.let { put(AUDITORIA_CUENTA_DEMO_ID, it) }
                put(AUDITORIA_ES_DEMO, if (actual.first.third) 1 else 0)
            })
            db.setTransactionSuccessful()
            return true
        } catch (e: Exception) {
            return false
        } finally {
            db.endTransaction()
        }
    }
    fun actualizarRecomendacionDeAgente(agenteId: Long, recomendacionId: Long, nuevoEstado: String): Boolean {
        if (agenteId <= 0L || recomendacionId <= 0L) return false
        val db = writableDatabase
        db.beginTransaction()
        try {
            val actual = db.query(TABLA_RECOMENDACIONES,
                arrayOf(RECOMENDACION_ESTADO, RECOMENDACION_CUENTA_DEMO_ID, RECOMENDACION_ES_DEMO),
                "$RECOMENDACION_ID = ? AND $RECOMENDACION_AGENTE_ID = ?",
                arrayOf(recomendacionId.toString(), agenteId.toString()), null, null, null
            ).use { cursor ->
                if (!cursor.moveToFirst()) return false
                Triple(cursor.getString(0), if (cursor.isNull(1)) null else cursor.getLong(1), cursor.getInt(2) == 1)
            }
            val siguientes = when (actual.first) {
                "Recibida" -> setOf("Contactada")
                "Contactada" -> setOf("En evaluación")
                "En evaluación" -> setOf("Se unió", "Descartada")
                else -> emptySet()
            }
            if (nuevoEstado !in siguientes) return false
            val ahora = System.currentTimeMillis()
            val filas = db.update(TABLA_RECOMENDACIONES,
                ContentValues().apply { put(RECOMENDACION_ESTADO, nuevoEstado); put(RECOMENDACION_ACTUALIZADA, ahora) },
                "$RECOMENDACION_ID = ? AND $RECOMENDACION_AGENTE_ID = ? AND $RECOMENDACION_ESTADO = ?",
                arrayOf(recomendacionId.toString(), agenteId.toString(), actual.first))
            if (filas != 1) return false
            db.insertOrThrow(TABLA_AUDITORIA, null, ContentValues().apply {
                put(AUDITORIA_ACTOR_ID, agenteId); put(AUDITORIA_ENTIDAD, "recomendacion")
                put(AUDITORIA_ENTIDAD_ID, recomendacionId); put(AUDITORIA_ACCION, nuevoEstado.uppercase())
                put(AUDITORIA_DETALLE, "Estado actualizado de ${actual.first} a $nuevoEstado")
                put(AUDITORIA_FECHA, ahora)
                actual.second?.let { put(AUDITORIA_CUENTA_DEMO_ID, it) }
                put(AUDITORIA_ES_DEMO, if (actual.third) 1 else 0)
            })
            db.setTransactionSuccessful()
            return true
        } catch (e: Exception) {
            return false
        } finally {
            db.endTransaction()
        }
    }
    fun listarRecomendacionesDeAgente(agenteId: Long): List<RecomendacionOrganizacion> =
        consultarTabla(TABLA_RECOMENDACIONES, "$RECOMENDACION_AGENTE_ID = ?", arrayOf(agenteId.toString()), "$RECOMENDACION_CREADA DESC", ::leerRecomendacion)

    fun listarAuditoriaDeCuenta(usuarioId: Long): List<EventoAuditoria> =
        consultarTabla(TABLA_AUDITORIA, "$AUDITORIA_CUENTA_DEMO_ID = ?", arrayOf(usuarioId.toString()), "$AUDITORIA_FECHA DESC", ::leerEventoAuditoria)

    private fun <T> consultarTabla(
        tabla: String,
        seleccion: String,
        argumentos: Array<String>,
        orden: String,
        convertir: (Cursor) -> T
    ): List<T> {
        val elementos = mutableListOf<T>()
        readableDatabase.query(tabla, null, seleccion, argumentos, null, null, orden).use { cursor ->
            while (cursor.moveToNext()) elementos += convertir(cursor)
        }
        return elementos
    }

    private fun consultar(sql: String, argumentos: Array<String>, convertir: (Cursor) -> Compromiso): List<Compromiso> {
        val elementos = mutableListOf<Compromiso>()
        readableDatabase.rawQuery(sql, argumentos).use { cursor ->
            while (cursor.moveToNext()) elementos += convertir(cursor)
        }
        return elementos
    }

    private fun leerEmpresa(cursor: Cursor) = Empresa(
        id = cursor.getLong(cursor.getColumnIndexOrThrow(EMPRESA_ID)),
        nombre = cursor.getString(cursor.getColumnIndexOrThrow(EMPRESA_NOMBRE)),
        recargoFijoClp = cursor.getLong(cursor.getColumnIndexOrThrow(EMPRESA_RECARGO)),
        ejecutivoId = cursor.longNullable(EMPRESA_EJECUTIVO_ID),
        cuentaDemoId = cursor.longNullable(EMPRESA_CUENTA_DEMO_ID),
        esDemo = cursor.getInt(cursor.getColumnIndexOrThrow(EMPRESA_ES_DEMO)) == 1,
        telefonoContacto = cursor.getString(cursor.getColumnIndexOrThrow(EMPRESA_TELEFONO)),
        correoContacto = cursor.getString(cursor.getColumnIndexOrThrow(EMPRESA_CORREO))
    )

    private fun leerCompromiso(cursor: Cursor): Compromiso {
        val vencimiento = cursor.getLong(cursor.getColumnIndexOrThrow(COMPROMISO_VENCIMIENTO))
        val estadoGuardado = cursor.getString(cursor.getColumnIndexOrThrow(COMPROMISO_ESTADO))
        val vencidoPorFecha = estadoGuardado == "Pendiente" && vencimiento < System.currentTimeMillis()
        val vencido = estadoGuardado == "Vencido" || vencidoPorFecha
        val recargoGuardado = cursor.getLong(cursor.getColumnIndexOrThrow(COMPROMISO_RECARGO))
        val recargoEmpresa = cursor.getLong(cursor.getColumnIndexOrThrow("recargo_empresa"))
        return Compromiso(
            id = cursor.getLong(cursor.getColumnIndexOrThrow(COMPROMISO_ID)),
            empresaId = cursor.getLong(cursor.getColumnIndexOrThrow(COMPROMISO_EMPRESA_ID)),
            empresaNombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre_empresa")),
            usuarioId = cursor.longNullable(COMPROMISO_USUARIO_ID),
            agenteId = cursor.longNullable(COMPROMISO_AGENTE_ID),
            clienteNombre = cursor.getString(cursor.getColumnIndexOrThrow(COMPROMISO_CLIENTE)),
            concepto = cursor.getString(cursor.getColumnIndexOrThrow(COMPROMISO_CONCEPTO)),
            cuotaActual = cursor.getInt(cursor.getColumnIndexOrThrow(COMPROMISO_CUOTA)),
            cuotasTotales = cursor.getInt(cursor.getColumnIndexOrThrow(COMPROMISO_CUOTAS)),
            montoClp = cursor.getLong(cursor.getColumnIndexOrThrow(COMPROMISO_MONTO)),
            vencimientoMillis = vencimiento,
            estado = if (vencido) "Vencido" else estadoGuardado,
            recargoClp = if (vencido) maxOf(recargoGuardado, recargoEmpresa) else recargoGuardado,
            cuentaDemoId = cursor.longNullable(COMPROMISO_CUENTA_DEMO_ID),
            esDemo = cursor.getInt(cursor.getColumnIndexOrThrow(COMPROMISO_ES_DEMO)) == 1,
            telefonoEmpresa = cursor.getString(cursor.getColumnIndexOrThrow("telefono_empresa")),
            correoEmpresa = cursor.getString(cursor.getColumnIndexOrThrow("correo_empresa"))
        )
    }
    private fun leerComprobante(cursor: Cursor) = ComprobantePago(
        id = cursor.getLong(cursor.getColumnIndexOrThrow(COMPROBANTE_ID)),
        pagoId = cursor.longNullable(COMPROBANTE_PAGO_ID),
        usuarioId = cursor.getLong(cursor.getColumnIndexOrThrow(COMPROBANTE_USUARIO_ID)),
        folio = cursor.getString(cursor.getColumnIndexOrThrow(COMPROBANTE_FOLIO)),
        montoClp = cursor.getLong(cursor.getColumnIndexOrThrow(COMPROBANTE_MONTO)),
        concepto = cursor.getString(cursor.getColumnIndexOrThrow(COMPROBANTE_CONCEPTO)),
        canal = cursor.getString(cursor.getColumnIndexOrThrow(COMPROBANTE_CANAL)),
        fechaMillis = cursor.getLong(cursor.getColumnIndexOrThrow(COMPROBANTE_FECHA)),
        esDemo = cursor.getInt(cursor.getColumnIndexOrThrow(COMPROBANTE_ES_DEMO)) == 1
    )

    private fun leerReporte(cursor: Cursor) = ReporteIncidencia(
        id = cursor.getLong(cursor.getColumnIndexOrThrow(REPORTE_ID)),
        usuarioId = cursor.longNullable(REPORTE_USUARIO_ID),
        compromisoId = cursor.longNullable(REPORTE_COMPROMISO_ID),
        empresaId = cursor.longNullable(REPORTE_EMPRESA_ID),
        agenteAsignadoId = cursor.longNullable(REPORTE_AGENTE_ID),
        tipo = cursor.getString(cursor.getColumnIndexOrThrow(REPORTE_TIPO)),
        descripcion = cursor.getString(cursor.getColumnIndexOrThrow(REPORTE_DESCRIPCION)),
        estado = cursor.getString(cursor.getColumnIndexOrThrow(REPORTE_ESTADO)),
        respuesta = cursor.getString(cursor.getColumnIndexOrThrow(REPORTE_RESPUESTA)),
        fechaCreacionMillis = cursor.getLong(cursor.getColumnIndexOrThrow(REPORTE_CREADO)),
        fechaActualizacionMillis = cursor.getLong(cursor.getColumnIndexOrThrow(REPORTE_ACTUALIZADO)),
        cuentaDemoId = cursor.longNullable(REPORTE_CUENTA_DEMO_ID),
        esDemo = cursor.getInt(cursor.getColumnIndexOrThrow(REPORTE_ES_DEMO)) == 1,
        empresaNombre = cursor.getColumnIndex("nombre_empresa").takeIf { it >= 0 }?.let { cursor.getString(it).orEmpty() }.orEmpty()
    )

    private fun leerRecomendacion(cursor: Cursor) = RecomendacionOrganizacion(
        id = cursor.getLong(cursor.getColumnIndexOrThrow(RECOMENDACION_ID)),
        agenteId = cursor.getLong(cursor.getColumnIndexOrThrow(RECOMENDACION_AGENTE_ID)),
        organizacion = cursor.getString(cursor.getColumnIndexOrThrow(RECOMENDACION_EMPRESA)),
        contacto = cursor.getString(cursor.getColumnIndexOrThrow(RECOMENDACION_CONTACTO)),
        autorizacionContacto = cursor.getInt(cursor.getColumnIndexOrThrow(RECOMENDACION_AUTORIZACION)) == 1,
        estado = cursor.getString(cursor.getColumnIndexOrThrow(RECOMENDACION_ESTADO)),
        fechaCreacionMillis = cursor.getLong(cursor.getColumnIndexOrThrow(RECOMENDACION_CREADA)),
        fechaActualizacionMillis = cursor.getLong(cursor.getColumnIndexOrThrow(RECOMENDACION_ACTUALIZADA)),
        cuentaDemoId = cursor.longNullable(RECOMENDACION_CUENTA_DEMO_ID),
        esDemo = cursor.getInt(cursor.getColumnIndexOrThrow(RECOMENDACION_ES_DEMO)) == 1
    )

    private fun leerEventoAuditoria(cursor: Cursor) = EventoAuditoria(
        id = cursor.getLong(cursor.getColumnIndexOrThrow(AUDITORIA_ID)),
        actorUsuarioId = cursor.longNullable(AUDITORIA_ACTOR_ID),
        entidad = cursor.getString(cursor.getColumnIndexOrThrow(AUDITORIA_ENTIDAD)),
        entidadId = cursor.longNullable(AUDITORIA_ENTIDAD_ID),
        accion = cursor.getString(cursor.getColumnIndexOrThrow(AUDITORIA_ACCION)),
        detalle = cursor.getString(cursor.getColumnIndexOrThrow(AUDITORIA_DETALLE)),
        fechaMillis = cursor.getLong(cursor.getColumnIndexOrThrow(AUDITORIA_FECHA)),
        cuentaDemoId = cursor.longNullable(AUDITORIA_CUENTA_DEMO_ID),
        esDemo = cursor.getInt(cursor.getColumnIndexOrThrow(AUDITORIA_ES_DEMO)) == 1
    )

    private fun Cursor.longNullable(nombreColumna: String): Long? {
        val indice = getColumnIndexOrThrow(nombreColumna)
        return if (isNull(indice)) null else getLong(indice)
    }
    fun guardarCompromiso(compromiso: Compromiso): Long {
        if (compromiso.montoClp < 0L || compromiso.recargoClp < 0L || compromiso.cuotaActual <= 0 || compromiso.cuotasTotales < compromiso.cuotaActual) return -1L
        if (compromiso.estado !in setOf("Pendiente", "Vencido", "Pagado", "En revisión", "Anulado")) return -1L
        val values = ContentValues().apply {
            compromiso.usuarioId?.let { put(COMPROMISO_USUARIO_ID, it) }
            compromiso.agenteId?.let { put(COMPROMISO_AGENTE_ID, it) }
            put(COMPROMISO_EMPRESA_ID, compromiso.empresaId)
            put(COMPROMISO_CLIENTE, compromiso.clienteNombre.trim())
            put(COMPROMISO_CONCEPTO, compromiso.concepto.trim())
            put(COMPROMISO_CUOTA, compromiso.cuotaActual)
            put(COMPROMISO_CUOTAS, compromiso.cuotasTotales)
            put(COMPROMISO_MONTO, compromiso.montoClp)
            put(COMPROMISO_VENCIMIENTO, compromiso.vencimientoMillis)
            put(COMPROMISO_ESTADO, compromiso.estado)
            put(COMPROMISO_RECARGO, compromiso.recargoClp)
            put(COMPROMISO_CREADO, System.currentTimeMillis())
            compromiso.cuentaDemoId?.let { put(COMPROMISO_CUENTA_DEMO_ID, it) }
            put(COMPROMISO_ES_DEMO, if (compromiso.esDemo) 1 else 0)
        }
        return writableDatabase.insert(TABLA_COMPROMISOS, null, values)
    }

    fun guardarReporte(reporte: ReporteIncidencia): Long {
        if (reporte.tipo.isBlank() || reporte.descripcion.isBlank()) return -1L
        if (reporte.estado !in setOf("Abierto", "En gestión", "Resuelto")) return -1L
        val now = System.currentTimeMillis()
        return writableDatabase.insert(TABLA_REPORTES, null, ContentValues().apply {
            reporte.usuarioId?.let { put(REPORTE_USUARIO_ID, it) }
            reporte.compromisoId?.let { put(REPORTE_COMPROMISO_ID, it) }
            reporte.empresaId?.let { put(REPORTE_EMPRESA_ID, it) }
            reporte.agenteAsignadoId?.let { put(REPORTE_AGENTE_ID, it) }
            put(REPORTE_TIPO, reporte.tipo.trim())
            put(REPORTE_DESCRIPCION, reporte.descripcion.trim())
            put(REPORTE_ESTADO, reporte.estado)
            reporte.respuesta?.let { put(REPORTE_RESPUESTA, it) }
            put(REPORTE_CREADO, if (reporte.fechaCreacionMillis > 0L) reporte.fechaCreacionMillis else now)
            put(REPORTE_ACTUALIZADO, now)
            reporte.cuentaDemoId?.let { put(REPORTE_CUENTA_DEMO_ID, it) }
            put(REPORTE_ES_DEMO, if (reporte.esDemo) 1 else 0)
        })
    }

    fun guardarRecomendacion(recomendacion: RecomendacionOrganizacion): Long {
        if (recomendacion.organizacion.isBlank() || recomendacion.contacto.isBlank() || !recomendacion.autorizacionContacto) return -1L
        if (recomendacion.estado !in setOf("Recibida", "Contactada", "En evaluación", "Se unió", "Descartada")) return -1L
        val now = System.currentTimeMillis()
        return writableDatabase.insert(TABLA_RECOMENDACIONES, null, ContentValues().apply {
            put(RECOMENDACION_AGENTE_ID, recomendacion.agenteId)
            put(RECOMENDACION_EMPRESA, recomendacion.organizacion.trim())
            put(RECOMENDACION_CONTACTO, recomendacion.contacto.trim())
            put(RECOMENDACION_AUTORIZACION, 1)
            put(RECOMENDACION_ESTADO, recomendacion.estado)
            put(RECOMENDACION_CREADA, if (recomendacion.fechaCreacionMillis > 0L) recomendacion.fechaCreacionMillis else now)
            put(RECOMENDACION_ACTUALIZADA, now)
            recomendacion.cuentaDemoId?.let { put(RECOMENDACION_CUENTA_DEMO_ID, it) }
            put(RECOMENDACION_ES_DEMO, if (recomendacion.esDemo) 1 else 0)
        })
    }

    fun guardarComprobante(comprobante: ComprobantePago): Long {
        if (comprobante.usuarioId <= 0L || comprobante.folio.isBlank() || comprobante.montoClp < 0L) return -1L
        return writableDatabase.insert(TABLA_COMPROBANTES, null, ContentValues().apply {
            comprobante.pagoId?.let { put(COMPROBANTE_PAGO_ID, it) }
            put(COMPROBANTE_USUARIO_ID, comprobante.usuarioId)
            put(COMPROBANTE_FOLIO, comprobante.folio.trim())
            put(COMPROBANTE_MONTO, comprobante.montoClp)
            put(COMPROBANTE_CONCEPTO, comprobante.concepto.trim())
            put(COMPROBANTE_CANAL, comprobante.canal.trim())
            put(COMPROBANTE_FECHA, if (comprobante.fechaMillis > 0L) comprobante.fechaMillis else System.currentTimeMillis())
            put(COMPROBANTE_ES_DEMO, if (comprobante.esDemo) 1 else 0)
        })
    }

    fun registrarAuditoria(
        actorUsuarioId: Long?,
        entidad: String,
        entidadId: Long?,
        accion: String,
        detalle: String,
        cuentaDemoId: Long? = actorUsuarioId,
        esDemo: Boolean = false
    ): Long {
        if (entidad.isBlank() || accion.isBlank()) return -1L
        return writableDatabase.insert(TABLA_AUDITORIA, null, ContentValues().apply {
            actorUsuarioId?.let { put(AUDITORIA_ACTOR_ID, it) }
            put(AUDITORIA_ENTIDAD, entidad.trim())
            entidadId?.let { put(AUDITORIA_ENTIDAD_ID, it) }
            put(AUDITORIA_ACCION, accion.trim())
            put(AUDITORIA_DETALLE, detalle.trim())
            put(AUDITORIA_FECHA, System.currentTimeMillis())
            cuentaDemoId?.let { put(AUDITORIA_CUENTA_DEMO_ID, it) }
            put(AUDITORIA_ES_DEMO, if (esDemo) 1 else 0)
        })
    }
    fun guardarSolicitudPago(usuarioId: Long, monto: String, concepto: String): Long {
        if (buscarPorId(usuarioId) == null) return -1L
        val values = ContentValues().apply {
            put(PAGO_USUARIO_ID, usuarioId)
            put(PAGO_MONTO, monto.trim())
            put(PAGO_CONCEPTO, concepto.trim())
            put(PAGO_FECHA, System.currentTimeMillis())
            put(PAGO_ESTADO, "En revisión")
        }
        return writableDatabase.insert(TABLA_PAGOS, null, values)
    }

    fun listarSolicitudesDeUsuario(usuarioId: Long): List<PagoUsuario> {
        if (usuarioId <= 0L) return emptyList()
        val sql = "SELECT p.$PAGO_ID, p.$PAGO_MONTO, p.$PAGO_CONCEPTO, p.$PAGO_FECHA, p.$PAGO_ESTADO, " +
            "COALESCE(e.$EMPRESA_NOMBRE, ''), COALESCE(r.$COMPROBANTE_CANAL, '') " +
            "FROM $TABLA_PAGOS p " +
            "LEFT JOIN $TABLA_COMPROMISOS c ON p.$PAGO_COMPROMISO_ID = c.$COMPROMISO_ID " +
            "LEFT JOIN $TABLA_EMPRESAS e ON c.$COMPROMISO_EMPRESA_ID = e.$EMPRESA_ID " +
            "LEFT JOIN $TABLA_COMPROBANTES r ON r.$COMPROBANTE_PAGO_ID = p.$PAGO_ID " +
            "WHERE p.$PAGO_USUARIO_ID = ? ORDER BY p.$PAGO_FECHA DESC"
        return readableDatabase.rawQuery(sql, arrayOf(usuarioId.toString())).use { cursor ->
            buildList {
                while (cursor.moveToNext()) add(
                    PagoUsuario(
                        id = cursor.getLong(0), monto = cursor.getString(1), concepto = cursor.getString(2),
                        fechaMillis = cursor.getLong(3), estado = cursor.getString(4),
                        empresaNombre = cursor.getString(5).orEmpty(), canal = cursor.getString(6).orEmpty()
                    )
                )
            }
        }
    }

    fun crearReporteDeUsuario(usuarioId: Long, compromisoId: Long?, tipo: String, descripcion: String): Pair<Long, String> {
        val tiposPermitidos = setOf("Monto incorrecto", "Ya pagué", "No reconozco este cobro", "Otro")
        val tipoLimpio = tipo.trim()
        val texto = descripcion.trim()
        if (usuarioId <= 0L || buscarPorId(usuarioId)?.rolId != Rol.ID_USUARIO) return -1L to "Solo un Usuario Final puede enviar reportes."
        if (tipoLimpio !in tiposPermitidos) return -1L to "Selecciona el tipo de problema."
        if (texto.length < 10) return -1L to "Describe el problema con al menos 10 caracteres."
        if (texto.length > 500) return -1L to "La descripción no puede superar los 500 caracteres."

        val db = writableDatabase
        db.beginTransaction()
        try {
            var empresaId: Long? = null
            var cuentaDemoId: Long? = null
            var esDemo = false
            if (compromisoId != null) {
                val compromiso = buscarCompromisoEnBase(db, usuarioId, compromisoId)
                    ?: return -1L to "El compromiso seleccionado no pertenece a tu cuenta."
                empresaId = compromiso.empresaId
                cuentaDemoId = compromiso.cuentaDemoId
                esDemo = compromiso.esDemo
            }
            val ahora = System.currentTimeMillis()
            val id = db.insertOrThrow(TABLA_REPORTES, null, ContentValues().apply {
                put(REPORTE_USUARIO_ID, usuarioId)
                compromisoId?.let { put(REPORTE_COMPROMISO_ID, it) }
                empresaId?.let { put(REPORTE_EMPRESA_ID, it) }
                put(REPORTE_TIPO, tipoLimpio)
                put(REPORTE_DESCRIPCION, texto)
                put(REPORTE_ESTADO, "Abierto")
                put(REPORTE_CREADO, ahora)
                put(REPORTE_ACTUALIZADO, ahora)
                cuentaDemoId?.let { put(REPORTE_CUENTA_DEMO_ID, it) }
                put(REPORTE_ES_DEMO, if (esDemo) 1 else 0)
            })
            db.insertOrThrow(TABLA_AUDITORIA, null, ContentValues().apply {
                put(AUDITORIA_ACTOR_ID, usuarioId)
                put(AUDITORIA_ENTIDAD, "reporte")
                put(AUDITORIA_ENTIDAD_ID, id)
                put(AUDITORIA_ACCION, "REPORTE_CREADO")
                put(AUDITORIA_DETALLE, "Reporte '$tipoLimpio' enviado por el usuario")
                put(AUDITORIA_FECHA, ahora)
                cuentaDemoId?.let { put(AUDITORIA_CUENTA_DEMO_ID, it) }
                put(AUDITORIA_ES_DEMO, if (esDemo) 1 else 0)
            })
            db.setTransactionSuccessful()
            return id to "Reporte enviado."
        } catch (e: Exception) {
            return -1L to "No se pudo enviar el reporte. Intenta nuevamente."
        } finally {
            db.endTransaction()
        }
    }

    fun listarRoles(): List<Rol> {
        val roles = mutableListOf<Rol>()
        readableDatabase.rawQuery(
            "SELECT $ROL_ID, $ROL_NOMBRE FROM $TABLA_ROLES ORDER BY $ROL_ID", null
        ).use { cursor ->
            while (cursor.moveToNext()) {
                roles.add(Rol(
                    id = cursor.getLong(cursor.getColumnIndexOrThrow(ROL_ID)),
                    nombre = cursor.getString(cursor.getColumnIndexOrThrow(ROL_NOMBRE))
                ))
            }
        }
        return roles
    }

    fun insertarUsuario(usuario: Usuario): Long {
        val usuarioProtegido = if (usuario.contrasenaSalt.isBlank() && usuario.contrasena.isNotBlank()) {
            val (hash, salt) = protegerContrasena(usuario.contrasena)
            usuario.copy(contrasena = hash, contrasenaSalt = salt)
        } else usuario
        val db = writableDatabase
        val id = db.insert(TABLA_USUARIOS, null, valoresDe(usuarioProtegido))
        if (id > 0L) sembrarDatosDemoUsuario(db, id, usuarioProtegido.rolId, usuarioProtegido.nombre)
        return id
    }

    fun crearCuenta(nombre: String, cuenta: String, ciudad: String, rut: String, contrasena: String, rolId: Long): Long =
        insertarUsuario(Usuario(
            nombre = nombre.trim(),
            cuenta = cuenta.trim().lowercase(),
            ciudad = ciudad.trim(),
            rut = rut,
            contrasena = contrasena,
            rolId = rolId
        ))

    fun listarUsuarios(): List<Usuario> =
        consultarUsuarios("$SQL_SELECT_USUARIOS ORDER BY u.$USU_NOMBRE", null)

    fun buscarUsuarios(texto: String): List<Usuario> {
        val patron = "%$texto%"
        return consultarUsuarios(
            "$SQL_SELECT_USUARIOS WHERE u.$USU_NOMBRE LIKE ? OR u.$USU_CUENTA LIKE ? ORDER BY u.$USU_NOMBRE",
            arrayOf(patron, patron)
        )
    }

    fun buscarPorId(id: Long): Usuario? =
        consultarUsuarios("$SQL_SELECT_USUARIOS WHERE u.$USU_ID = ?", arrayOf(id.toString())).firstOrNull()

    fun buscarPorRut(rut: String): Usuario? =
        if (rut.isBlank()) null else consultarUsuarios("$SQL_SELECT_USUARIOS WHERE $USU_RUT = ? COLLATE NOCASE", arrayOf(rut.trim())).firstOrNull()

    fun actualizarRutUsuario(usuarioId: Long, rut: String): ResultadoOperacion {
        if (usuarioId <= 0L || rut.isBlank()) return ResultadoOperacion(false, "Ingresa un RUT válido.")
        val existente = buscarPorRut(rut)
        if (existente != null && existente.id != usuarioId) return ResultadoOperacion(false, "Ese RUT ya está asociado a otra cuenta.")
        val db = writableDatabase
        db.beginTransaction()
        try {
            val existeUsuario = db.query(TABLA_USUARIOS, arrayOf(USU_ID), "$USU_ID = ?", arrayOf(usuarioId.toString()), null, null, null).use { it.moveToFirst() }
            if (!existeUsuario) return ResultadoOperacion(false, "No se encontró la cuenta actual.")
            val filas = db.update(TABLA_USUARIOS, ContentValues().apply { put(USU_RUT, rut) }, "$USU_ID = ?", arrayOf(usuarioId.toString()))
            if (filas != 1) return ResultadoOperacion(false, "No se pudo guardar el RUT.")
            val ahora = System.currentTimeMillis()
            db.insertOrThrow(TABLA_AUDITORIA, null, ContentValues().apply {
                put(AUDITORIA_ACTOR_ID, usuarioId)
                put(AUDITORIA_ENTIDAD, "usuario")
                put(AUDITORIA_ENTIDAD_ID, usuarioId)
                put(AUDITORIA_ACCION, "RUT_ACTUALIZADO")
                put(AUDITORIA_DETALLE, "RUT de la cuenta actualizado")
                put(AUDITORIA_FECHA, ahora)
                put(AUDITORIA_ES_DEMO, 0)
            })
            db.setTransactionSuccessful()
            return ResultadoOperacion(true, "RUT guardado correctamente.")
        } catch (e: Exception) {
            return ResultadoOperacion(false, "No se pudo guardar el RUT. Intenta nuevamente.")
        } finally {
            db.endTransaction()
        }
    }
    fun buscarPorCuenta(cuenta: String): Usuario? {
        val correo = cuenta.trim().lowercase()
        if (correo.isBlank()) return null
        return consultarUsuarios(
            "$SQL_SELECT_USUARIOS WHERE LOWER(u.$USU_CUENTA) = ?", arrayOf(correo)
        ).firstOrNull()
    }

    fun validarLogin(cuenta: String, contrasena: String, rolId: Long): Usuario? {
        val usuario = buscarPorCuenta(cuenta) ?: return null
        if (usuario.rolId != rolId) return null

        // Migra al formato protegido cuentas antiguas que aún no tenían salt.
        if (usuario.contrasenaSalt.isBlank()) {
            if (usuario.contrasena != contrasena) return null
            val (hash, salt) = protegerContrasena(contrasena)
            val actualizado = ContentValues().apply {
                put(USU_CONTRASENA, hash)
                put(USU_SALT, salt)
            }
            val filas = writableDatabase.update(
                TABLA_USUARIOS,
                actualizado,
                "$USU_ID = ? AND $USU_SALT = ''",
                arrayOf(usuario.id.toString())
            )
            return if (filas == 1) buscarPorId(usuario.id) else null
        }

        val salt = usuario.contrasenaSalt.hexToBytes()
        val (hash, _) = protegerContrasena(contrasena, salt)
        return usuario.takeIf {
            MessageDigest.isEqual(it.contrasena.toByteArray(), hash.toByteArray())
        }
    }

    fun actualizarUsuario(usuario: Usuario): Int =
        writableDatabase.update(TABLA_USUARIOS, valoresDe(usuario), "$USU_ID = ?", arrayOf(usuario.id.toString()))

    fun eliminarUsuario(id: Long): Int =
        writableDatabase.delete(TABLA_USUARIOS, "$USU_ID = ?", arrayOf(id.toString()))

    private fun valoresDe(usuario: Usuario) = ContentValues().apply {
        put(USU_NOMBRE, usuario.nombre)
        put(USU_CUENTA, usuario.cuenta.trim().lowercase())
        put(USU_CIUDAD, usuario.ciudad)
        put(USU_RUT, usuario.rut)
        put(USU_CONTRASENA, usuario.contrasena)
        put(USU_SALT, usuario.contrasenaSalt)
        put(USU_ROL, usuario.rolId)
    }

    private fun consultarUsuarios(sql: String, args: Array<String>?): List<Usuario> {
        val lista = mutableListOf<Usuario>()
        readableDatabase.rawQuery(sql, args).use { cursor ->
            while (cursor.moveToNext()) lista.add(leerUsuario(cursor))
        }
        return lista
    }

    private fun leerUsuario(cursor: Cursor): Usuario = Usuario(
        nombre = cursor.getString(cursor.getColumnIndexOrThrow(USU_NOMBRE)),
        cuenta = cursor.getString(cursor.getColumnIndexOrThrow(USU_CUENTA)),
        ciudad = cursor.getString(cursor.getColumnIndexOrThrow(USU_CIUDAD)),
        rut = cursor.getString(cursor.getColumnIndexOrThrow(USU_RUT)).orEmpty(),
        contrasena = cursor.getString(cursor.getColumnIndexOrThrow(USU_CONTRASENA)).orEmpty(),
        contrasenaSalt = cursor.getString(cursor.getColumnIndexOrThrow(USU_SALT)).orEmpty(),
        id = cursor.getLong(cursor.getColumnIndexOrThrow(USU_ID)),
        rolId = cursor.getLong(cursor.getColumnIndexOrThrow(USU_ROL)),
        rolNombre = cursor.getString(cursor.getColumnIndexOrThrow(ROL_NOMBRE))
    )

    private fun protegerContrasena(password: String, saltExistente: ByteArray? = null): Pair<String, String> {
        val salt = saltExistente ?: ByteArray(16).also(SecureRandom()::nextBytes)
        val spec = PBEKeySpec(password.toCharArray(), salt, 120_000, 256)
        val hash = try {
            SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
                .generateSecret(spec).encoded.toHex()
        } finally {
            spec.clearPassword()
        }
        return hash to salt.toHex()
    }

    private fun ByteArray.toHex(): String = joinToString("") { "%02x".format(it) }
    private fun String.hexToBytes(): ByteArray = chunked(2).map { it.toInt(16).toByte() }.toByteArray()
}