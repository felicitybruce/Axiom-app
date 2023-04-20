package com.example.axiom.client

import com.example.axiom.service.ApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    private fun getRetrofit(): Retrofit {

        // Log the data
        val logger = HttpLoggingInterceptor();
        // Logging whole body
        logger.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder()
            .addInterceptor(logger)
            .build()

        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("http://192.168.0.50:8080")
            .client(client)
            .build()
    }

    fun getApiService(): ApiService{
        return getRetrofit().create(ApiService::class.java)
    }
}