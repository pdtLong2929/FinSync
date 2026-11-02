package com.finsync.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val page: AuthPage = AuthPage.Welcome,
    val busy: Boolean = false,
    val errors: AuthFieldErrors = AuthFieldErrors(),
    val failure: AuthFailure? = null,
    val authenticated: Boolean = false,
    val registered: Boolean = false,
    val canReturnToWorkspace: Boolean = false,
)

/** Credentials exist only in a short-lived request; never in UiState/SavedState/logs.
 * Navigation cancels in-flight work and rejects late results from old requests. */
class AuthController(private val repository: AuthRepository, private val scope: CoroutineScope) {
    private val mutableState = MutableStateFlow(AuthUiState())
    val state = mutableState.asStateFlow()
    private var request: Job? = null
    private var generation = 0

    fun open(page: AuthPage) {
        generation++
        request?.cancel()
        mutableState.value = mutableState.value.copy(page = page, busy = false, errors = AuthFieldErrors(), failure = null,
            canReturnToWorkspace = mutableState.value.canReturnToWorkspace || page == AuthPage.Workspace)
    }
    fun cancelSubmission() {
        generation++
        request?.cancel()
        mutableState.value = mutableState.value.copy(busy = false)
    }
    fun clearErrors() { mutableState.value = mutableState.value.copy(errors = AuthFieldErrors(), failure = null) }
    fun expireSession() {
        open(AuthPage.Expired)
        mutableState.value = mutableState.value.copy(authenticated = false)
    }
    fun leaveAccount() {
        open(AuthPage.Welcome)
        mutableState.value = mutableState.value.copy(authenticated = false, registered = false)
    }
    fun submit(email: String, password: String = "", confirmation: String = "") {
        val current = mutableState.value
        if (current.busy || current.page !in listOf(AuthPage.Login, AuthPage.Register, AuthPage.ForgotPassword)) return
        val errors = validateAuth(current.page, email, password, confirmation)
        mutableState.value = current.copy(errors = errors, failure = null)
        if (errors.any) return
        mutableState.value = mutableState.value.copy(busy = true)
        val id = ++generation
        request = scope.launch {
            val result = try {
                when (current.page) {
                    AuthPage.Register -> repository.register(email.trim(), password)
                    AuthPage.ForgotPassword -> repository.requestPasswordReset(email.trim())
                    else -> repository.signIn(email.trim(), password)
                }
            } catch (cancelled: CancellationException) { throw cancelled }
              catch (_: Exception) { AuthResult.Failed(AuthFailure.Unavailable) }
            if (id != generation) return@launch
            mutableState.value = when (result) {
                AuthResult.Completed -> mutableState.value.copy(busy = false,
                    page = when (current.page) {
                        AuthPage.ForgotPassword -> AuthPage.ResetFeedback
                        AuthPage.Register -> AuthPage.RegistrationFeedback
                        else -> AuthPage.Account
                    },
                    authenticated = current.page == AuthPage.Login || current.authenticated,
                    registered = current.page == AuthPage.Register)
                is AuthResult.Failed -> mutableState.value.copy(busy = false, failure = result.reason)
            }
        }
    }
}

class AuthViewModel : ViewModel() {
    val repository: AuthRepository = UnavailableAuthRepository()
    val controller = AuthController(repository, viewModelScope)
}
