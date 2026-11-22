package com.example.pagoflex.data.remote

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface PagoApi {
    @GET("api/pagos")
    suspend fun listar(): List<Map<String, Any>>

    @POST("api/pagos")
    suspend fun registrar(@Body datos: Map<String, Any>): Map<String, Any>
}
