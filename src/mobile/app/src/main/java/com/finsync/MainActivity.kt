package com.finsync

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.finsync.auth.AuthHost
import com.finsync.auth.AuthViewModel
import com.finsync.ui.FinSyncApp
import com.finsync.ui.FinanceViewModel
import com.finsync.ui.dashboard.DashboardViewModel
import com.finsync.ui.theme.FinSyncTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import com.finsync.data.FileGroupLedgerStore
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.DisposableEffect
import com.finsync.social.*
import com.finsync.bank.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val bankRequests = MutableStateFlow(0)
    private val bankCandidate = MutableStateFlow<String?>(null)
    private fun receiveBankRequest(intent: android.content.Intent?) {
        if (intent?.action != BankReminder.ACTION) return
        bankCandidate.value = intent.data?.takeIf { it.scheme == "finsync" && it.host == "bank-alert" }
            ?.lastPathSegment?.takeIf { it.matches(Regex("[a-f0-9]{64}")) }
        bankRequests.value++
    }
    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent); setIntent(intent)
        receiveBankRequest(intent)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        receiveBankRequest(intent)
        enableEdgeToEdge()
        val viewModel = ViewModelProvider(this)[DashboardViewModel::class.java]
        val finance = ViewModelProvider(this)[FinanceViewModel::class.java]
        val auth = ViewModelProvider(this)[AuthViewModel::class.java]
        setContent {
            val account by auth.controller.state.collectAsStateWithLifecycle()
            val workspace = account.user?.id?.let { "account:$it" } ?: "device"
            LaunchedEffect(workspace) {
                finance.groupStorage.bind(workspace, FileGroupLedgerStore(applicationContext, workspace))
            }
            FinSyncTheme {
                key(workspace) {
                val bankScope = rememberCoroutineScope()
                val bankStore = remember { FileBankStore(applicationContext, workspace) }
                val bank = remember { BankController(workspace, bankStore, finance, bankScope) }
                val bankState by bank.state.collectAsStateWithLifecycle()
                val bankRequest by bankRequests.collectAsStateWithLifecycle()
                val bankCandidateId by bankCandidate.collectAsStateWithLifecycle()
                LaunchedEffect(bank) {
                    BankCaptureSettings(applicationContext).pauseForDifferentWorkspace(workspace)
                    bank.open()
                    bankStore.updates.collect { bank.observeStore(it) }
                }
                DisposableEffect(bank) { onDispose { bank.close() } }
                val people = remember { PeopleController(DevicePeopleRepository(applicationContext, workspace, account.user?.email.orEmpty())) }
                LaunchedEffect(people) { people.reload() }
                val serverGroups = remember(account.authenticated, auth.repository) {
                    (auth.repository as? com.finsync.auth.HttpAuthRepository)?.takeIf { account.authenticated }
                        ?.let { com.finsync.groups.ServerGroups(it) }
                }
                CompositionLocalProvider(LocalPeopleController provides people, LocalBankController provides bank,
                    com.finsync.groups.LocalServerGroups provides serverGroups) {
                AuthHost(auth.controller, bankRequest, bankCandidateId) { onAccount ->
                if (bankState.loading || bankState.loadFailed) com.finsync.ui.components.FeaturePage("Saved wallet data", headerAction = {
                    androidx.compose.material3.TextButton(onClick = onAccount) { androidx.compose.material3.Text("Account") }
                }) {
                    item { if (bankState.loading) androidx.compose.material3.LinearProgressIndicator()
                        else { com.finsync.ui.components.ErrorText(bankState.error)
                            androidx.compose.material3.TextButton(onClick = { bankScope.launch { bank.open() } }) { androidx.compose.material3.Text("Retry saved data") } }
                    }
                } else
                FinSyncApp(
                    dashboardState = viewModel.uiState.collectAsStateWithLifecycle().value,
                    onRetry = viewModel::reload,
                    finance = finance,
                    onAccount = onAccount,
                    bankOpenRequest = bankRequest,
                    bankCandidateId = bankCandidateId,
                )
                }
                }
                }
            }
        }
    }
}
