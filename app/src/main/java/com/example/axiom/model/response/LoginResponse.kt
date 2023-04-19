package com.example.axiom.model.response

// What to expect once user is logged in

data class LoginResponse(
    val id: Int,
    val username: String,
    val email: String
)