package com.example.pagoflex.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.pagoflex.model.Compromiso
import com.example.pagoflex.model.Pago
import com.example.pagoflex.model.Usuario

class UsuarioDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "pagoflex.db"
        private const val DATABASE_VERSION = 2

        const val TABLE_COMUNAS = "comunas"
        const val TABLE_USUARIOS = "usuarios"
        const val TABLE_COMPROMISOS = "compromisos"
        const val TABLE_PAGOS = "pagos"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE $TABLE_COMUNAS (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT UNIQUE NOT NULL
            )
        """)

        db.execSQL("""
            CREATE TABLE $TABLE_USUARIOS (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                correo TEXT UNIQUE NOT NULL,
                rut TEXT NOT NULL,
                comuna TEXT NOT NULL,
                rol TEXT NOT NULL,
                contrasena TEXT NOT NULL
            )
        """)

        db.execSQL("""
            CREATE TABLE $TABLE_COMPROMISOS (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                usuarioId INTEGER NOT NULL,
                empresa TEXT NOT NULL,
                monto REAL NOT NULL,
                fechaVencimiento TEXT NOT NULL,
                estado TEXT NOT NULL,
                FOREIGN KEY(usuarioId) REFERENCES $TABLE_USUARIOS(id)
            )
        """)

        db.execSQL("""
            CREATE TABLE $TABLE_PAGOS (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                compromisoId INTEGER NOT NULL,
                usuarioId INTEGER NOT NULL,
                monto REAL NOT NULL,
                fecha TEXT NOT NULL,
                metodo TEXT NOT NULL,
                FOREIGN KEY(compromisoId) REFERENCES $TABLE_COMPROMISOS(id),
                FOREIGN KEY(usuarioId) REFERENCES $TABLE_USUARIOS(id)
            )
        """)

        // Insert initial comunas
        val comunas = listOf("Santiago", "Providencia", "Las Condes", "Ñuñoa", "Maipú", "Puente Alto", "La Florida", "Viña del Mar", "Valparaíso", "Concepción")
        for (c in comunas) {
            val values = ContentValues().apply { put("nombre", c) }
            db.insert(TABLE_COMUNAS, null, values)
        }

        // Insert default users (Usuario final, Agente, Ejecutivo)
        val defaultUsers = listOf(
            ContentValues().apply {
                put("nombre", "Juan Perez")
                put("correo", "usuario@pagoflex.cl")
                put("rut", "12345678-9")
                put("comuna", "Santiago")
                put("rol", "Usuario final")
                put("contrasena", "Password123*")
            },
            ContentValues().apply {
                put("nombre", "Ana Agente")
                put("correo", "agente@pagoflex.cl")
                put("rut", "98765432-1")
                put("comuna", "Providencia")
                put("rol", "Agente")
                put("contrasena", "Password123*")
            },
            ContentValues().apply {
                put("nombre", "Carlos Ejecutivo")
                put("correo", "ejecutivo@pagoflex.cl")
                put("rut", "11111111-1")
                put("comuna", "Las Condes")
                put("rol", "Ejecutivo")
                put("contrasena", "Password123*")
            }
        )
        for (u in defaultUsers) {
            val userId = db.insert(TABLE_USUARIOS, null, u)
            if (userId != -1L && u.getAsString("rol") == "Usuario final") {
                // Initial commitment for default user
                val comp = ContentValues().apply {
                    put("usuarioId", userId)
                    put("empresa", "Empresa Eléctrica S.A.")
                    put("monto", 45000.0)
                    put("fechaVencimiento", "2026-05-15")
                    put("estado", "Pendiente")
                }
                db.insert(TABLE_COMPROMISOS, null, comp)
            }
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PAGOS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_COMPROMISOS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USUARIOS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_COMUNAS")
        onCreate(db)
    }

    fun obtenerComunas(): List<String> {
        val lista = mutableListOf<String>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT nombre FROM $TABLE_COMUNAS ORDER BY nombre ASC", null)
        if (cursor.moveToFirst()) {
            do {
                lista.add(cursor.getString(0))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return lista
    }

    fun registrarUsuario(usuario: Usuario): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("nombre", usuario.nombre)
            put("correo", usuario.correo)
            put("rut", usuario.rut)
            put("comuna", usuario.comuna)
            put("rol", usuario.rol)
            put("contrasena", usuario.contrasena)
        }
        val id = db.insert(TABLE_USUARIOS, null, values)
        return id != -1L
    }

    fun validarCredenciales(correo: String, contrasena: String): Usuario? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT id, nombre, correo, rut, comuna, rol, contrasena FROM $TABLE_USUARIOS WHERE correo = ? AND contrasena = ?", arrayOf(correo, contrasena))
        var usuario: Usuario? = null
        if (cursor.moveToFirst()) {
            usuario = Usuario(
                id = cursor.getLong(0),
                nombre = cursor.getString(1),
                correo = cursor.getString(2),
                rut = cursor.getString(3),
                comuna = cursor.getString(4),
                rol = cursor.getString(5),
                contrasena = cursor.getString(6)
            )
        }
        cursor.close()
        return usuario
    }

    fun obtenerCompromisosPorUsuario(usuarioId: Long): List<Compromiso> {
        val lista = mutableListOf<Compromiso>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT id, usuarioId, empresa, monto, fechaVencimiento, estado FROM $TABLE_COMPROMISOS WHERE usuarioId = ?", arrayOf(usuarioId.toString()))
        if (cursor.moveToFirst()) {
            do {
                lista.add(
                    Compromiso(
                        id = cursor.getLong(0),
                        usuarioId = cursor.getLong(1),
                        empresa = cursor.getString(2),
                        monto = cursor.getDouble(3),
                        fechaVencimiento = cursor.getString(4),
                        estado = cursor.getString(5)
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return lista
    }

    fun registrarPago(pago: Pago): Boolean {
        val db = writableDatabase
        db.beginTransaction()
        try {
            val values = ContentValues().apply {
                put("compromisoId", pago.compromisoId)
                put("usuarioId", pago.usuarioId)
                put("monto", pago.monto)
                put("fecha", pago.fecha)
                put("metodo", pago.metodo)
            }
            val id = db.insert(TABLE_PAGOS, null, values)
            if (id != -1L) {
                val updateValues = ContentValues().apply { put("estado", "Pagado") }
                db.update(TABLE_COMPROMISOS, updateValues, "id = ?", arrayOf(pago.compromisoId.toString()))
                db.setTransactionSuccessful()
                return true
            }
        } finally {
            db.endTransaction()
        }
        return false
    }

    fun actualizarPerfil(usuario: Usuario): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("nombre", usuario.nombre)
            put("comuna", usuario.comuna)
            put("contrasena", usuario.contrasena)
        }
        val filas = db.update(TABLE_USUARIOS, values, "id = ?", arrayOf(usuario.id.toString()))
        return filas > 0
    }
}
