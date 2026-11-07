package com.finsync.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalAutofillManager
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.finsync.R
import com.finsync.ui.components.*
import com.finsync.ui.theme.LiquidTokens
import com.finsync.ui.theme.Space

/** Local exploration is separate from authentication. Only a real repository may succeed. */
@Composable
fun AuthHost(controller: AuthController, appContent: @Composable (() -> Unit) -> Unit) {
    val view = androidx.compose.ui.platform.LocalView.current
    DisposableEffect(view) {
        val bars = (view.context as? android.app.Activity)?.window?.let { androidx.core.view.WindowCompat.getInsetsController(it, view) }
        val status = bars?.isAppearanceLightStatusBars
        val navigation = bars?.isAppearanceLightNavigationBars
        bars?.isAppearanceLightStatusBars = true
        bars?.isAppearanceLightNavigationBars = true
        onDispose {
            status?.let { bars?.isAppearanceLightStatusBars = it }
            navigation?.let { bars?.isAppearanceLightNavigationBars = it }
        }
    }
    val state by controller.state.collectAsStateWithLifecycle()
    val screens = rememberSaveableStateHolder()
    val context = LocalContext.current
    val profileStore = remember(context.applicationContext) { LocalProfileStore(context.applicationContext) }
    var profile by remember(state.user?.id) { mutableStateOf<LocalProfile?>(null) }
    var profileLoading by remember(state.user?.id) { mutableStateOf(false) }
    var profileError by remember(state.user?.id) { mutableStateOf(false) }
    var reload by remember { mutableIntStateOf(0) }
    LaunchedEffect(state.user?.id, reload) {
        val user = state.user ?: return@LaunchedEffect
        profileLoading = true; profileError = false
        try {
            val local = profileStore.load(user)
            if (controller.accountRepository.supportsAccountApi) {
                val server = controller.accountRepository.loadProfile()
                profile = local.copy(displayName = server.displayName, phone = server.phoneNumber.orEmpty(), serverVersion = server.version)
                controller.updateIdentity(server)
            } else profile = local
        }
        catch (cancelled: kotlinx.coroutines.CancellationException) { throw cancelled }
        catch (failed: AccountApiException) {
            if (failed.reason in listOf(AuthFailure.SessionExpired, AuthFailure.AccountLocked)) controller.expireSession()
            else profileError = true
        }
        catch (_: Exception) { profileError = true }
        finally { profileLoading = false }
    }
    CompositionLocalProvider(LocalAccountProfile provides profile) {
    if (state.page == AuthPage.Workspace) {
        screens.SaveableStateProvider("finance") {
            appContent { controller.open(if (state.authenticated) AuthPage.Account else AuthPage.Welcome) }
        }
    } else Box(Modifier.fillMaxSize().safeDrawingPadding()) {
        key(state.page) {
            when (state.page) {
                AuthPage.Account -> if (state.user != null && state.authenticated) ProfileScreen(
                    state.user!!, profile, profileLoading, profileError, profileStore,
                    onRetry = { reload++ }, onSaved = { profile = it },
                    onBack = { if (!state.busy) controller.open(AuthPage.Workspace) },
                    onWorkspace = { controller.open(AuthPage.Workspace) }, onSignOut = controller::signOut,
                    accountApi = controller.accountRepository.takeIf { it.supportsAccountApi },
                    remoteBusy = state.busy, remoteFailure = state.failure,
                    onIdentity = controller::updateIdentity,
                    onExpired = controller::expireSession)
                    else AuthLanding(state, controller)
                AuthPage.Login, AuthPage.Register, AuthPage.ForgotPassword -> AuthForm(state, controller)
                else -> AuthLanding(state, controller)
            }
        }
    }
    }
}

@Composable
private fun AuthHeading(title: String, subtitle: String) {
    Column(verticalArrangement = Arrangement.spacedBy(Space.small)) {
        Box(Modifier.size(LiquidTokens.compactChromeSize).liquidLens(),contentAlignment=Alignment.Center) {
            Icon(painterResource(R.drawable.ic_brand_monogram),null,Modifier.size(com.finsync.ui.theme.DockTokens.medallion),tint=LiquidTokens.ink)
        }
        Text(title, style = MaterialTheme.typography.headlineMedium)
        Text(subtitle, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun AuthLanding(state: AuthUiState, controller: AuthController) {
    val page = state.page
    val back: (() -> Unit)? = when {
        page == AuthPage.ResetFeedback || page == AuthPage.RegistrationFeedback -> ({ controller.open(AuthPage.Login) })
        state.canReturnToWorkspace -> ({ controller.open(AuthPage.Workspace) })
        page == AuthPage.Account -> ({ controller.leaveAccount() })
        page != AuthPage.Welcome -> ({ controller.open(AuthPage.Welcome) })
        else -> null
    }
    FeaturePage("FinSync", back) {
        item { AuthHeading(when (page) {
            AuthPage.Explore -> "Make yourself at home"
            AuthPage.Expired -> "Welcome back"
            AuthPage.ResetFeedback -> "Request received"
            AuthPage.RegistrationFeedback -> "Account created"
            AuthPage.Account -> "Your account"
            else -> "Your money. Your rhythm."
        }, when (page) {
            AuthPage.Explore -> "Explore FinSync with a sample workspace."
            AuthPage.Expired -> "Your session has expired. Sign in to continue."
            AuthPage.ResetFeedback -> "If an account matches your email, follow the recovery instructions in your inbox."
            AuthPage.RegistrationFeedback -> "Your account is ready. Sign in with your email and password."
            AuthPage.Account -> "You're signed in."
            else -> "Everyday spending, shared plans and a little room for the future."
        }) }
        if (page == AuthPage.Explore) item { FinanceCard {
            Text("Sample workspace", style = MaterialTheme.typography.titleMedium)
            Text("Balances and people are examples. Changes stay in this session and reset when the app closes. No bank account is connected and no money is moved.")
        } }
        if (page == AuthPage.Account) {
            state.user?.let { user -> item { FinanceCard {
                Text(user.displayName, style = MaterialTheme.typography.titleMedium)
                Text(user.email, style = MaterialTheme.typography.bodyMedium)
                Text("You're signed in. You'll need to sign in again when the app restarts.")
            } } }
            item { Text("Financial screens still use sample data. No bank account is connected and no money is moved.",
                style = MaterialTheme.typography.bodySmall) }
        }
        item { Column(verticalArrangement = Arrangement.spacedBy(Space.small)) {
            MetalButton(onClick = { controller.open(when (page) {
                AuthPage.Explore, AuthPage.Account -> AuthPage.Workspace
                else -> AuthPage.Login
            }) }, modifier = Modifier.fillMaxWidth()) {
                Text(when (page) {
                    AuthPage.Explore -> "Open sample workspace"
                    AuthPage.Account -> "Open sample workspace"
                    AuthPage.Expired -> "Sign in again"
                    AuthPage.ResetFeedback, AuthPage.RegistrationFeedback -> "Back to sign in"
                    else -> "Get started"
                })
            }
            if (page == AuthPage.Welcome) {
                MetalOutlineButton(onClick = { controller.open(AuthPage.Register) }, modifier = Modifier.fillMaxWidth()) { Text("Sign up") }
                TextButton(onClick = { controller.open(AuthPage.Explore) }, modifier = Modifier.fillMaxWidth()) { Text("Explore FinSync") }
            }
            if (page == AuthPage.Expired) TextButton(onClick = { controller.open(AuthPage.Explore) }) { Text("Explore FinSync") }
            if (page == AuthPage.Account) {
                TextButton(onClick = controller::leaveAccount) { Text("Sign out on this device") }
                Text("This signs you out on this device only.", style = MaterialTheme.typography.bodySmall)
            }
        } }
    }
}

@Composable
private fun AuthForm(state: AuthUiState, controller: AuthController) {
    val registration = state.page == AuthPage.Register
    val recovery = state.page == AuthPage.ForgotPassword
    var email by rememberSaveable { mutableStateOf(state.prefillEmail) }
    var displayName by rememberSaveable { mutableStateOf("") }
    // Passwords never enter saved bundles or persistent storage.
    var password by remember { mutableStateOf("") }
    var confirmation by remember { mutableStateOf("") }
    var visible by remember { mutableStateOf(false) }
    var confirmVisible by remember { mutableStateOf(false) }
    var pending by remember { mutableStateOf<AuthPage?>(null) }
    val passwordFocus = remember { FocusRequester() }
    val confirmFocus = remember { FocusRequester() }
    val autofill = LocalAutofillManager.current
    DisposableEffect(autofill) {
        onDispose { if (!controller.state.value.authenticated) autofill?.cancel() }
    }
    val focus = LocalFocusManager.current
    val keyboardOpen = WindowInsets.ime.getBottom(LocalDensity.current) > 0
    val dirty = email.isNotBlank() || displayName.isNotBlank() || password.isNotEmpty() || confirmation.isNotEmpty()
    val navigate: (AuthPage) -> Unit = { target ->
        if (state.busy) controller.cancelSubmission()
        if (dirty || state.busy) pending = target else { autofill?.cancel(); controller.open(target) }
    }
    LaunchedEffect(state.failure) { if (state.failure != null) autofill?.cancel() }
    val backendAccess = rememberLocalBackendAccess {
        if (!state.busy) { email = email.trim(); controller.submit(email, password, confirmation, displayName) }
    }
    val submit = {
        if (recovery) controller.submit(email.trim(), password, confirmation, displayName)
        else backendAccess.submit()
    }
    val action: @Composable () -> Unit = {
        Column(verticalArrangement = Arrangement.spacedBy(Space.small)) {
            if (backendAccess.denied && !recovery) {
                ErrorText("Allow Nearby devices / local network access to connect to the demo backend on your computer. Then try again.")
                TextButton(onClick = backendAccess.openSettings) { Text("Open app permissions") }
            }
            if (state.errors.any) ErrorText("Check the highlighted fields.")
            state.failure?.let { ErrorText(when (it) {
                AuthFailure.NotConnected -> "Account access isn't available yet. You can explore FinSync without an account."
                AuthFailure.Offline -> "You're offline. Check your connection and try again."
                AuthFailure.Unavailable -> "We couldn't complete your request. Please try again."
                AuthFailure.InvalidCredentials -> "Email or password is incorrect."
                AuthFailure.AccountLocked -> "This account is locked. Contact your administrator."
                AuthFailure.EmailExists -> "This email is already registered. Sign in instead."
                AuthFailure.InvalidInput -> "Check your account details and try again."
                AuthFailure.Unsupported -> "Password recovery isn't available yet. No email has been sent."
                AuthFailure.RateLimited -> "Too many attempts. Wait a moment and try again."
                AuthFailure.SessionExpired -> "Your session has expired. Sign in again."
                AuthFailure.ProfileConflict -> "Your profile changed elsewhere. Reload it before saving."
            }) }
            if (state.busy) LinearProgressIndicator(Modifier.fillMaxWidth())
            MetalButton(onClick = submit, enabled = !state.busy, modifier = Modifier.fillMaxWidth()) {
                Text(if (state.busy) "Please wait..." else if (registration) "Create account" else if (recovery) "Continue" else "Sign in")
            }
            if (state.failure == AuthFailure.NotConnected) TextButton(onClick = { navigate(AuthPage.Explore) }, modifier = Modifier.fillMaxWidth()) { Text("Explore FinSync") }
        }
    }
    FeaturePage("FinSync", { navigate(if (recovery) AuthPage.Login else AuthPage.Welcome) },
        primaryAction = if (keyboardOpen) action else null) {
        item { AuthHeading(if (registration) "A fresh start" else if (recovery) "Forgot password?" else "Welcome back",
            if (registration) "Make room for what matters." else if (recovery) "Let's help you get back to your account." else "Your next move starts here.") }
        item { Column(verticalArrangement = Arrangement.spacedBy(Space.small)) {
            if (registration) ChromeTextField(displayName, { displayName = it; controller.clearErrors() }, Modifier.fillMaxWidth(),
                label = { Text("Display name") }, singleLine = true, enabled = !state.busy,
                isError = state.errors.displayName != null, supportingText = { ErrorText(state.errors.displayName) },
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, imeAction = ImeAction.Next))
            ChromeTextField(email, { email = it; controller.clearErrors() }, Modifier.fillMaxWidth().semantics { contentType = ContentType.EmailAddress + ContentType.Username },
                label = { Text("Email address") }, singleLine = true, enabled = !state.busy,
                isError = state.errors.email != null, supportingText = { ErrorText(state.errors.email); if (state.errors.email == null) Text(" ") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, autoCorrectEnabled = false, imeAction = if (recovery) ImeAction.Done else ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { passwordFocus.requestFocus() }, onDone = { submit() }))
            if (!recovery) ChromeTextField(password, { password = it; controller.clearErrors() },
                Modifier.fillMaxWidth().focusRequester(passwordFocus).semantics { contentType = if (registration) ContentType.NewPassword else ContentType.Password },
                label = { Text("Password") }, singleLine = true, enabled = !state.busy,
                isError = state.errors.password != null,
                supportingText = { if (state.errors.password != null) ErrorText(state.errors.password) else Text(if (registration) "8+ characters · Up to 72 UTF-8 bytes" else " ") },
                visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = { IconButton(onClick = { visible = !visible }, enabled = !state.busy) {
                    Icon(painterResource(R.drawable.ic_visibility), contentDescription = if (visible) "Hide password" else "Show password")
                } }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, autoCorrectEnabled = false, imeAction = if (registration) ImeAction.Next else ImeAction.Done),
                keyboardActions = KeyboardActions(onNext = { confirmFocus.requestFocus() }, onDone = { submit() }))
            if (registration) ChromeTextField(confirmation, { confirmation = it; controller.clearErrors() },
                Modifier.fillMaxWidth().focusRequester(confirmFocus).semantics { contentType = ContentType.NewPassword },
                label = { Text("Confirm password") }, singleLine = true, enabled = !state.busy,
                isError = state.errors.confirmation != null, supportingText = { ErrorText(state.errors.confirmation); if (state.errors.confirmation == null) Text(" ") },
                visualTransformation = if (confirmVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = { IconButton(onClick = { confirmVisible = !confirmVisible }, enabled = !state.busy) {
                    Icon(painterResource(R.drawable.ic_visibility), contentDescription = if (confirmVisible) "Hide confirmation password" else "Show confirmation password")
                } }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, autoCorrectEnabled = false, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { submit() }))
            if (!registration && !recovery) TextButton(onClick = { navigate(AuthPage.ForgotPassword) }, enabled = !state.busy,
                modifier = Modifier.align(Alignment.End)) { Text("Forgot password?") }
        } }
        if (recovery) item { FinanceCard {
            Text("Email recovery is coming", style = MaterialTheme.typography.titleMedium)
            Text("You can enter your email here, but recovery emails are not sent yet.",
                style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        } }
        if (!keyboardOpen) item { action() }
        item { TextButton(onClick = { navigate(if (registration || recovery) AuthPage.Login else AuthPage.Register) }, enabled = !state.busy,
            modifier = Modifier.fillMaxWidth()) { Text(if (registration || recovery) "Back to sign in" else "New here? Sign up") } }
    }
    if (pending != null) MetalDialog(onDismissRequest = { pending = null }, title = { Text("Leave this form?") },
        text = { Text("Your unsaved details will be cleared.") },
        confirmButton = { TextButton(onClick = {
            val target = pending; pending = null; password = ""; confirmation = ""; autofill?.cancel(); focus.clearFocus(); target?.let(controller::open)
        }) { Text("Leave form") } },
        dismissButton = { TextButton(onClick = { pending = null }) { Text("Keep editing") } })
}
