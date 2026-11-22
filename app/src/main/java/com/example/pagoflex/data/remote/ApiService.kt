package com.example.pagoflex.data.remote

import com.example.pagoflex.model.Post
import retrofit2.http.GET

interface ApiService {
    @GET("posts")
    suspend fun getPosts(): List<Post>
}
