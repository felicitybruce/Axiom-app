package com.example.axiom.model.request

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

data class RegisterRequest(
    val id: Int? = 0,
    val firstName: String,
    val lastName: String,
    val email: String,
    val username: String,
    val password: String,
    val cnfPassword: String,
)