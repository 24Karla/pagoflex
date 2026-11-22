package com.example.pagoflex.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val URL_USUARIOS = "http://10.0.2.2:8082/"
    private const val URL_ROLES = "http://10.0.2.2:8081/"

    private const val URL_AUDITORIA = "http://10.0.2.2:8083/"

    private const val URL_COMPROMISOS = "http://10.0.2.2:8084/"

    private const val URL_EMPRESAS = "http://10.0.2.2:8085/"

    private const val URL_PAGOS = "http://10.0.2.2:8086/"

    private const val URL_RECOMENDACIONES = "http://10.0.2.2:8087/"

    private const val URL_REPORTES = "http://10.0.2.2:8088/"


    private fun crear(urlBase: String): Retrofit =
        Retrofit.Builder()
            .baseUrl(urlBase)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    val usuarioApi: UsuarioApi by lazy { crear(URL_USUARIOS).create(UsuarioApi::class.java) }

    val rolApi: RolApi by lazy { crear(URL_ROLES).create(RolApi::class.java) }

    val avatarApi: AvatarApi by lazy { crear(URL_USUARIOS).create(AvatarApi::class.java) }
}
