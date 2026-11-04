package com.finsync.mobile.data.remote

data class RegisterRequest(
    val email: String,
    val password: String,
    val displayName: String
)
