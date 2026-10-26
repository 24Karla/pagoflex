package com.example.pagoflex.model

data class Empresa(
    val id: Long,
    val nombre: String,
    val recargoFijoClp: Long,
    val ejecutivoId: Long? = null,
    val cuentaDemoId: Long? = null,
    val esDemo: Boolean = false,
    val telefonoContacto: String = "",
    val correoContacto: String = ""
)

data class Compromiso(
    val id: Long,
    val empresaId: Long,
    val empresaNombre: String,
    val usuarioId: Long? = null,
    val agenteId: Long? = null,
    val clienteNombre: String,
    val concepto: String,
    val cuotaActual: Int,
    val cuotasTotales: Int,
    val montoClp: Long,
    val vencimientoMillis: Long,
    val estado: String,
    val recargoClp: Long,
    val cuentaDemoId: Long? = null,
    val esDemo: Boolean = false,
    val telefonoEmpresa: String = "",
    val correoEmpresa: String = ""
)

data class ComprobantePago(
    val id: Long,
    val pagoId: Long?,
    val usuarioId: Long,
    val folio: String,
    val montoClp: Long,
    val concepto: String,
    val canal: String,
    val fechaMillis: Long,
    val esDemo: Boolean = false
)


data class PagoCartera(
    val id: Long,
    val empresaId: Long,
    val montoClp: Long,
    val fechaMillis: Long,
    val empresaNombre: String = "",
    val aTiempo: Boolean = false
)
data class ReporteIncidencia(
    val id: Long,
    val usuarioId: Long?,
    val compromisoId: Long?,
    val empresaId: Long?,
    val agenteAsignadoId: Long?,
    val tipo: String,
    val descripcion: String,
    val estado: String,
    val respuesta: String?,
    val fechaCreacionMillis: Long,
    val fechaActualizacionMillis: Long,
    val cuentaDemoId: Long? = null,
    val esDemo: Boolean = false,
    val empresaNombre: String = ""
)

data class RecomendacionOrganizacion(
    val id: Long,
    val agenteId: Long,
    val organizacion: String,
    val contacto: String,
    val autorizacionContacto: Boolean,
    val estado: String,
    val fechaCreacionMillis: Long,
    val fechaActualizacionMillis: Long,
    val cuentaDemoId: Long? = null,
    val esDemo: Boolean = false
)

data class EventoAuditoria(
    val id: Long,
    val actorUsuarioId: Long?,
    val entidad: String,
    val entidadId: Long?,
    val accion: String,
    val detalle: String,
    val fechaMillis: Long,
    val cuentaDemoId: Long? = null,
    val esDemo: Boolean = false
)

data class ResultadoPago(
    val exitoso: Boolean,
    val mensaje: String,
    val folio: String? = null
)


data class ResultadoOperacion(val exitoso: Boolean, val mensaje: String)

data class ResumenCumplimiento(val compromisosEvaluados: Int, val pagosATiempo: Int, val porcentaje: Double, val categoria: String)
