package com.finsync.mobile.ui.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.finsync.mobile.data.AuthRepository
import com.finsync.mobile.data.local.TokenStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val token: String) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

class AuthViewModel(
    application: Application,
    private val repository: AuthRepository = AuthRepository()
) : AndroidViewModel(application) {

    private val _state = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val state: StateFlow<AuthUiState> = _state

    fun login(email: String, password: String) {
        _state.value = AuthUiState.Loading
        viewModelScope.launch {
            try {
                val resp = repository.login(email, password)
                TokenStore.saveToken(getApplication(), resp.token)
                _state.value = AuthUiState.Success(resp.token)
            } catch (e: Exception) {
                _state.value = AuthUiState.Error(e.localizedMessage ?: "Login failed")
            }
        }
    }

    fun register(email: String, password: String, fullName: String) {
        _state.value = AuthUiState.Loading
        viewModelScope.launch {
            try {
                val resp = repository.register(email, password, fullName)
                TokenStore.saveToken(getApplication(), resp.token)
                _state.value = AuthUiState.Success(resp.token)
            } catch (e: Exception) {
                _state.value = AuthUiState.Error(e.localizedMessage ?: "Register failed")
            }
        }
    }
}
