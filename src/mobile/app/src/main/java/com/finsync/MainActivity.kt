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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val viewModel = ViewModelProvider(this)[DashboardViewModel::class.java]
        val finance = ViewModelProvider(this)[FinanceViewModel::class.java]
        val auth = ViewModelProvider(this)[AuthViewModel::class.java]
        setContent {
            FinSyncTheme {
                AuthHost(auth.controller) { onAccount ->
                FinSyncApp(
                    dashboardState = viewModel.uiState.collectAsStateWithLifecycle().value,
                    onRetry = viewModel::reload,
                    finance = finance,
                    onAccount = onAccount,
                )
                }
            }
        }
    }
}
