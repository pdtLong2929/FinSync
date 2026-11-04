package com.finsync.mobile.data.remote

import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("/api/v1/auth/login")
    suspend fun login(@Body req: LoginRequest): AuthResponse

    @POST("/api/v1/auth/register")
    suspend fun register(@Body req: RegisterRequest): AuthResponse
}
