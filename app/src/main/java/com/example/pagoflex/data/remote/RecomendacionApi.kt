package com.example.pagoflex.data.remote

import retrofit2.http.GET

interface RecomendacionApi {
    @GET("api/recomendaciones")
    suspend fun listar(): List<Map<String, Any>>
}
