package com.example.pagoflex.data.remote

import com.example.pagoflex.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.GATEWAY_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val usuarioApi: UsuarioApi by lazy { retrofit.create(UsuarioApi::class.java) }
    val rolApi: RolApi by lazy { retrofit.create(RolApi::class.java) }
    val avatarApi: AvatarApi by lazy { retrofit.create(AvatarApi::class.java) }
    val compromisoApi: CompromisoApi by lazy { retrofit.create(CompromisoApi::class.java) }
    val empresaApi: EmpresaApi by lazy { retrofit.create(EmpresaApi::class.java) }
    val pagoApi: PagoApi by lazy { retrofit.create(PagoApi::class.java) }
    val reporteApi: ReporteApi by lazy { retrofit.create(ReporteApi::class.java) }
    val recomendacionApi: RecomendacionApi by lazy { retrofit.create(RecomendacionApi::class.java) }
    val auditoriaApi: AuditoriaApi by lazy { retrofit.create(AuditoriaApi::class.java) }
}
