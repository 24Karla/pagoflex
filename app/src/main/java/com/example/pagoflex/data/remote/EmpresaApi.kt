package com.example.pagoflex.data.remote

import retrofit2.http.GET

interface EmpresaApi {
    @GET("api/empresas")
    suspend fun listar(): List<Map<String, Any>>
}
