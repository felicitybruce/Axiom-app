package com.example.axiom.service

import com.example.axiom.model.request.LoginRequest
import com.example.axiom.model.request.RegisterRequest
import com.example.axiom.model.response.LoginResponse
import com.example.axiom.model.response.RegisterResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("/authenticate")
    fun loginUser(@Body loginRequest: LoginRequest): Call<LoginResponse>

    @POST("/users")
    fun registerUser(@Body registerRequest: RegisterRequest): Call<RegisterResponse>

}