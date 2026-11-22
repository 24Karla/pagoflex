package com.example.pagoflex.data.remote

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ReporteApi {
    @GET("api/reportes")
    suspend fun listar(): List<Map<String, Any>>

    @POST("api/reportes")
    suspend fun registrar(@Body datos: Map<String, Any>): Map<String, Any>
}
