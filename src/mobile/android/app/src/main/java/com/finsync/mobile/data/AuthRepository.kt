package com.finsync.mobile.data

import com.finsync.mobile.data.remote.AuthApi
import com.finsync.mobile.data.remote.LoginRequest
import com.finsync.mobile.data.remote.RegisterRequest
import com.finsync.mobile.data.remote.AuthResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository(private val api: AuthApi = com.finsync.mobile.data.remote.NetworkModule.authApi) {
    suspend fun login(email: String, password: String): AuthResponse =
        withContext(Dispatchers.IO) { api.login(LoginRequest(email, password)) }

    suspend fun register(email: String, password: String, fullName: String): AuthResponse =
    withContext(Dispatchers.IO) { api.register(RegisterRequest(email, password, fullName)) }
}
