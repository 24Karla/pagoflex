package com.example.pagoflex.data.remote

import retrofit2.http.GET
import retrofit2.http.Path

interface CompromisoApi {
    @GET("api/compromisos")
    suspend fun listar(): List<Map<String, Any>>

    @GET("api/compromisos/{id}")
    suspend fun obtener(@Path("id") id: Long): Map<String, Any>
}
