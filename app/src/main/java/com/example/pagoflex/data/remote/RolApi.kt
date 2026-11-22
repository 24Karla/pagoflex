package com.example.pagoflex.data.remote

import retrofit2.http.GET

interface RolApi {
    @GET("api/roles")
    suspend fun listar(): List<RolResponse>
}
