package com.finsync.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.finsync.ui.FinanceState
import com.finsync.ui.groups.GroupListScreen
import com.finsync.ui.groups.GroupExpenseForm
import com.finsync.ui.reports.GoalsScreen
import com.finsync.ui.reports.ReportsScreen
import com.finsync.ui.theme.FinSyncTheme

@Preview(name = "Groups loading", showBackground = true)
@Composable private fun GroupsLoadingPreview() { FinSyncTheme { GroupListScreen(FinanceState(), {}, {}, { null }, LoadState.Loading) } }
@Preview(name = "Groups error", showBackground = true)
@Composable private fun GroupsErrorPreview() { FinSyncTheme { GroupListScreen(FinanceState(), {}, {}, { null }, LoadState.Error) } }
@Preview(name = "Groups empty", showBackground = true)
@Composable private fun GroupsEmptyPreview() { FinSyncTheme { GroupListScreen(FinanceState(groups = emptyList()), {}, {}, { null }) } }
@Preview(name = "Shared expense form", showBackground = true)
@Composable private fun ExpensePreview() { FinSyncTheme { GroupExpenseForm(FinanceState().groups.first(), {}) { _, _, _, _, _, _, _, _ -> null } } }
@Preview(name = "Reports offline", showBackground = true)
@Composable private fun ReportsOfflinePreview() { FinSyncTheme { ReportsScreen(FinanceState(), "", {}, LoadState.Offline) } }
@Preview(name = "Reports loading", showBackground = true)
@Composable private fun ReportsLoadingPreview() { FinSyncTheme { ReportsScreen(FinanceState(), "", {}, LoadState.Loading) } }
@Preview(name = "Savings goals", showBackground = true)
@Composable private fun GoalsPreview() { FinSyncTheme { GoalsScreen(FinanceState().goals, {}, {}) } }
