package com.example.arkanoid.retrofit

import retrofit2.http.GET

interface ApiService {

    @GET("pagesofwealth/status.json")
    suspend fun checkStatus(): TestDataDto
}