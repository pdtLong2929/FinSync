package com.finsync.ui.dashboard

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class DashboardViewModel : ViewModel() {
    private val state = MutableStateFlow<DashboardUiState>(DashboardUiState.Content(DemoDashboard.data))
    val uiState = state.asStateFlow()

    fun reload() {
        state.value = DashboardUiState.Content(DemoDashboard.data)
    }
}
