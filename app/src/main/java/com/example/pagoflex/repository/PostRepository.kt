package com.example.pagoflex.repository

import com.example.pagoflex.data.remote.RetrofitInstance
import com.example.pagoflex.model.Post

class PostRepository {
    suspend fun getPosts(): List<Post> {
        return RetrofitInstance.api.getPosts()
    }
}
