package com.example.pagoflex.navigation

sealed class Ruta(val ruta: String) {

    data object Login : Ruta("login")
    data object Inicio : Ruta("inicio")
    data object Perfil : Ruta("perfil")
    data object Configuraciones : Ruta("configuraciones")

    data object Compromisos : Ruta("compromisos")
    data object HistorialPagos : Ruta("historial_pagos")
    data object Formulario : Ruta("formulario")

    data object DetalleCompromiso : Ruta("detalle_compromiso/{id}") {
        fun crear(id: Long) = "detalle_compromiso/$id"
    }

    data object AgenteReportes : Ruta("agente_reportes")
    data object AgenteRecomendaciones : Ruta("agente_recomendaciones")

    data object EjecutivoResumen : Ruta("ejecutivo_resumen")
    data object EjecutivoCartera : Ruta("ejecutivo_cartera")
}