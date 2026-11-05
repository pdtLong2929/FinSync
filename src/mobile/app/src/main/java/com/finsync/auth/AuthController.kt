package com.finsync.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import com.finsync.BuildConfig

data class AuthUiState(
    val page: AuthPage = AuthPage.Welcome,
    val busy: Boolean = false,
    val errors: AuthFieldErrors = AuthFieldErrors(),
    val failure: AuthFailure? = null,
    val authenticated: Boolean = false,
    val registered: Boolean = false,
    val canReturnToWorkspace: Boolean = false,
    val user: AuthUser? = null,
    val prefillEmail: String = "",
)

/** Credentials exist only in a short-lived request; never in UiState/SavedState/logs.
 * Navigation cancels in-flight work and rejects late results from old requests. */
class AuthController(private val repository: AuthRepository, private val scope: CoroutineScope) {
    val accountRepository: AuthRepository get() = repository
    private val mutableState = MutableStateFlow(AuthUiState())
    val state = mutableState.asStateFlow()
    private var request: Job? = null
    private var generation = 0
    private var expiry: Job? = null
    private var authEpoch = 0
    private var acceptedSession: AuthSession? = null

    private fun watchExpiry(initial: AuthSession) {
        expiry?.cancel()
        val epoch = ++authEpoch
        acceptedSession = initial
        expiry = scope.launch {
            var session = initial
            while (epoch == authEpoch && mutableState.value.authenticated) {
                delay((session.expiresAtMillis - System.currentTimeMillis()).coerceAtLeast(1))
                if (!repository.supportsAccountApi) { expireSession(); return@launch }
                when (val result = repository.refreshSession()) {
                    is AuthResult.SignedIn -> { session = result.session; acceptedSession = session }
                    is AuthResult.Failed -> {
                        if (result.reason in listOf(AuthFailure.SessionExpired, AuthFailure.AccountLocked)) {
                            expireSession(); return@launch
                        }
                        // A temporary outage does not make a valid refresh credential disappear.
                        delay(30_000)
                    }
                    else -> { expireSession(); return@launch }
                }
            }
        }
    }
    fun updateIdentity(user: AuthUser) {
        if (mutableState.value.user?.id == user.id) mutableState.value = mutableState.value.copy(user = user)
    }
    fun signOut() {
        if (!repository.supportsAccountApi) { leaveAccount(); return }
        if (mutableState.value.busy) return
        expiry?.cancel(); authEpoch++
        val epoch = authEpoch
        mutableState.value = mutableState.value.copy(busy = true, failure = null)
        request = scope.launch {
            val failure = try { repository.signOut() }
                catch (cancelled: CancellationException) { throw cancelled }
                catch (_: Exception) { AuthFailure.Unavailable }
            if (epoch != authEpoch) return@launch
            if (failure == null) leaveAccount()
            else if (failure == AuthFailure.AccountLocked || failure == AuthFailure.SessionExpired) expireSession()
            else {
                mutableState.value = mutableState.value.copy(busy = false, failure = failure)
                acceptedSession?.let(::watchExpiry)
            }
        }
    }

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
        authEpoch++; acceptedSession = null
        expiry?.cancel()
        repository.clearSession()
        open(AuthPage.Expired)
        mutableState.value = mutableState.value.copy(authenticated = false, user = null, canReturnToWorkspace = false)
    }
    fun leaveAccount() {
        authEpoch++; acceptedSession = null
        expiry?.cancel()
        repository.clearSession()
        open(AuthPage.Welcome)
        mutableState.value = mutableState.value.copy(authenticated = false, registered = false, user = null, canReturnToWorkspace = false, prefillEmail = "")
    }
    fun submit(email: String, password: String = "", confirmation: String = "", displayName: String = "") {
        val current = mutableState.value
        if (current.busy || current.page !in listOf(AuthPage.Login, AuthPage.Register, AuthPage.ForgotPassword)) return
        val errors = validateAuth(current.page, email, password, confirmation,
            if (repository.requiresDisplayName) displayName else null)
        mutableState.value = current.copy(errors = errors, failure = null)
        if (errors.any) return
        mutableState.value = mutableState.value.copy(busy = true)
        val id = ++generation
        request = scope.launch {
            val result = try {
                when (current.page) {
                    AuthPage.Register -> repository.register(email.trim(), password, displayName)
                    AuthPage.ForgotPassword -> repository.requestPasswordReset(email.trim())
                    else -> repository.signIn(email.trim(), password)
                }
            } catch (cancelled: CancellationException) { throw cancelled }
              catch (_: Exception) { AuthResult.Failed(AuthFailure.Unavailable) }
            if (id != generation) return@launch
            mutableState.value = when (result) {
                is AuthResult.SignedIn -> {
                    if (result.session.isExpired()) {
                        repository.clearSession()
                        mutableState.value.copy(busy = false, failure = AuthFailure.Unavailable)
                    } else {
                        repository.acceptSession(result.session)
                        // Start the watcher after authenticated state is published below.
                        mutableState.value.copy(busy = false, page = AuthPage.Workspace, authenticated = true,
                            canReturnToWorkspace = true, registered = current.page == AuthPage.Register, user = result.session.user)
                    }
                }
                is AuthResult.Registered -> mutableState.value.copy(busy = false, page = AuthPage.RegistrationFeedback,
                    registered = true, authenticated = false, user = null, prefillEmail = result.user.email)
                AuthResult.Completed -> mutableState.value.copy(busy = false,
                    page = when (current.page) {
                        AuthPage.ForgotPassword -> AuthPage.ResetFeedback
                        AuthPage.Register -> AuthPage.RegistrationFeedback
                        else -> AuthPage.Account
                    },
                    authenticated = current.page == AuthPage.Login || current.authenticated,
                    registered = current.page == AuthPage.Register)
                is AuthResult.Failed -> mutableState.value.copy(busy = false, failure = result.reason,
                    errors = result.fields ?: AuthFieldErrors())
            }
            if (result is AuthResult.SignedIn && mutableState.value.authenticated) watchExpiry(result.session)
        }
    }
}

class AuthViewModel : ViewModel() {
    val repository: AuthRepository = if (BuildConfig.AUTH_BASE_URL.isNotBlank())
        HttpAuthRepository(BuildConfig.AUTH_BASE_URL, allowLocalHttp = BuildConfig.DEBUG) else UnavailableAuthRepository()
    val controller = AuthController(repository, viewModelScope)
    override fun onCleared() { repository.clearSession(); super.onCleared() }
}
