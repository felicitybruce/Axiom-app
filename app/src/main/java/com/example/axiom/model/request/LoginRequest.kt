package com.example.axiom.model.request

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

data class LoginRequest (
    val email: String,
    val password: String
    )
