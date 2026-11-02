package com.finsync.ui.dashboard

import androidx.annotation.StringRes
import com.finsync.R
import java.text.NumberFormat
import java.util.Locale

/** Whole VND amounts; never mix shared receivables with personal wallet balances. */
data class PersonalSummary(val balance: Long, val income: Long, val expenses: Long)
data class GroupSummary(
    @param:StringRes val name: Int,
    val receivable: Long,
    val payable: Long,
    val displayName: String? = null,
)
data class PersonalTransaction(
    val id: String,
    @param:StringRes val title: Int,
    @param:StringRes val detail: Int,
    val signedAmount: Long,
    val displayTitle: String? = null,
    val displayDetail: String? = null,
    val category: String? = null,
)
data class DashboardData(
    val personal: PersonalSummary,
    val group: GroupSummary,
    val transactions: List<PersonalTransaction>,
    val diningSpent: Long,
    val diningLimit: Long,
    val spendingTrend: List<Pair<String, Long>> = emptyList(),
)

sealed interface DashboardUiState {
    data object Loading : DashboardUiState
    data object Empty : DashboardUiState
    data object Error : DashboardUiState
    data class Content(val data: DashboardData, val isOffline: Boolean = false) : DashboardUiState
}

/** No repository or network source: this fixture is deliberately fictional. */
object DemoDashboard {
    val data = DashboardData(
        personal = PersonalSummary(balance = 12_450_000, income = 8_000_000, expenses = 3_550_000),
        group = GroupSummary(R.string.sample_group, receivable = 350_000, payable = 120_000),
        transactions = listOf(
            PersonalTransaction("lunch", R.string.sample_lunch, R.string.sample_lunch_detail, -65_000, category = "Food"),
            PersonalTransaction("allowance", R.string.sample_salary, R.string.sample_salary_detail, 8_000_000),
            PersonalTransaction("bus", R.string.sample_transport, R.string.sample_transport_detail, -100_000, category = "Transport"),
        ),
        diningSpent = 1_200_000,
        diningLimit = 1_500_000,
    )
}

fun formatVnd(amount: Long): String =
    "${NumberFormat.getIntegerInstance(Locale.forLanguageTag("vi-VN")).format(amount)} ₫"

fun budgetProgress(spent: Long, limit: Long): Float =
    if (limit <= 0) 0f else (spent.toDouble() / limit).toFloat().coerceIn(0f, 1f)

/** Signed remainder: negative means overspent. Does not change the budget or wallet. */
fun budgetRemaining(spent: Long, limit: Long): Long = limit - spent
