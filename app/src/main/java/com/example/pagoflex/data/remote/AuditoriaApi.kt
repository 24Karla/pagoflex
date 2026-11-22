package com.example.pagoflex.data.remote

import retrofit2.http.GET

interface AuditoriaApi {
    @GET("api/auditoria")
    suspend fun listar(): List<Map<String, Any>>
}
