package com.finsync.ui.transactions

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.finsync.domain.*
import com.finsync.ui.components.*
import com.finsync.ui.theme.Space
import java.time.DayOfWeek
import java.time.LocalDate

data class TransactionFilterState(
    val type: String = "All",
    val period: String = "All dates",
    val category: String = "All categories",
    val walletId: String = "",
    val minimumAmount: String = "",
    val maximumAmount: String = "",
) {
    val activeCount: Int get() = listOf(
        type != "All", period != "All dates", category != "All categories", walletId.isNotBlank(),
        minimumAmount.isNotBlank(), maximumAmount.isNotBlank(),
    ).count { it }

    companion object {
        val Saver: Saver<TransactionFilterState, Any> = listSaver(
            save = { listOf(it.type, it.period, it.category, it.walletId, it.minimumAmount, it.maximumAmount) },
            restore = { values -> TransactionFilterState(
                type = values[0] as String, period = values[1] as String, category = values[2] as String,
                walletId = values[3] as String, minimumAmount = values[4] as String, maximumAmount = values[5] as String,
            ) },
        )
    }
}

fun filterTransactions(
    entries: List<LedgerEntry>,
    wallets: List<Wallet>,
    query: String,
    filters: TransactionFilterState,
    today: LocalDate,
): List<LedgerEntry> {
    val minimum = filters.minimumAmount.takeIf(String::isNotBlank)?.toLongOrNull()
    val maximum = filters.maximumAmount.takeIf(String::isNotBlank)?.toLongOrNull()
    val normalizedQuery = query.trim()
    val weekStart = today.with(DayOfWeek.MONDAY)
    val lastThirtyDaysStart = today.minusDays(29)

    return entries.asSequence().filter { entry ->
        if (!entry.type.isTransaction) return@filter false
        val wallet = wallets.find { it.id == entry.walletId }
        val matchesType = filters.type == "All" || entry.type.name.equals(filters.type, ignoreCase = true)
        val matchesWallet = filters.walletId.isBlank() || entry.walletId == filters.walletId
        val matchesCategory = filters.category == "All categories" || entry.category == filters.category
        val matchesAmount = (minimum == null || entry.amount >= minimum) && (maximum == null || entry.amount <= maximum)
        val matchesPeriod = when (filters.period) {
            "This week" -> entry.date >= weekStart && entry.date <= today
            "This month" -> entry.date.year == today.year && entry.date.month == today.month
            "Last 30 days" -> entry.date >= lastThirtyDaysStart && entry.date <= today
            "This year" -> entry.date.year == today.year
            else -> true
        }
        val matchesQuery = normalizedQuery.isBlank() || listOf(entry.note, entry.category, wallet?.name.orEmpty())
            .any { it.contains(normalizedQuery, ignoreCase = true) }
        matchesType && matchesWallet && matchesCategory && matchesAmount && matchesPeriod && matchesQuery
    }.sortedWith(compareByDescending<LedgerEntry> { it.date }.thenBy { it.category.lowercase() }).toList()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionFilterSheet(
    filters: TransactionFilterState,
    categories: List<Category>,
    wallets: List<Wallet>,
    onApply: (TransactionFilterState) -> Unit,
    onDismiss: () -> Unit,
) {
    var type by rememberSaveable(filters.type) { mutableStateOf(filters.type) }
    var period by rememberSaveable(filters.period) { mutableStateOf(filters.period) }
    var category by rememberSaveable(filters.category) { mutableStateOf(filters.category) }
    var walletId by rememberSaveable(filters.walletId) { mutableStateOf(filters.walletId) }
    var minimum by rememberSaveable(filters.minimumAmount) { mutableStateOf(filters.minimumAmount) }
    var maximum by rememberSaveable(filters.maximumAmount) { mutableStateOf(filters.maximumAmount) }
    val minimumValue = minimum.takeIf(String::isNotBlank)?.toLongOrNull()
    val maximumValue = maximum.takeIf(String::isNotBlank)?.toLongOrNull()
    val rangeError = when {
        minimum.isNotBlank() && minimumValue == null -> "Enter a valid minimum whole VND amount."
        maximum.isNotBlank() && maximumValue == null -> "Enter a valid maximum whole VND amount."
        minimumValue != null && maximumValue != null && minimumValue > maximumValue -> "Minimum must not exceed maximum."
        else -> null
    }
    val walletOptions = wallets.map { wallet ->
        wallet.id to (wallet.name + if (wallet.archived) " · Archived" else "")
    }
    val selectedWallet = walletOptions.find { it.first == walletId }?.second ?: "All wallets"
    val filterListMaxHeight = (LocalConfiguration.current.screenHeightDp * 0.28f).dp

    ModalBottomSheet(onDismissRequest = onDismiss,containerColor=LiquidSurfaceTokens.frost) {
        Column(Modifier.fillMaxWidth().imePadding().navigationBarsPadding().padding(horizontal = Space.medium),
            verticalArrangement = Arrangement.spacedBy(Space.medium)) {
                Text("Filter transactions", style = MaterialTheme.typography.headlineSmall)
                Text("Choose any combination, then apply it to the list.", style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().heightIn(max = filterListMaxHeight),
                    contentPadding = PaddingValues(bottom = Space.small),
                    verticalArrangement = Arrangement.spacedBy(Space.medium),
                ) {
                    item {
                        Text("Type", style = MaterialTheme.typography.labelLarge)
                        ChoiceChips(listOf("All", "Expense", "Income"), type) { type = it }
                    }
                    item {
                        Text("Time", style = MaterialTheme.typography.labelLarge)
                        ChoiceChips(listOf("All dates", "This week", "This month", "Last 30 days", "This year"), period) { period = it }
                    }
                    item {
                        OptionPicker("Category", category, listOf("All categories") + categories.map { it.name }.distinct()) { category = it }
                    }
                    item {
                        OptionPicker("Wallet", selectedWallet, listOf("All wallets") + walletOptions.map { it.second }) { name ->
                            walletId = walletOptions.find { it.second == name }?.first.orEmpty()
                        }
                    }
                    item {
                        Text("Amount range · VND", style = MaterialTheme.typography.labelLarge)
                        Row(horizontalArrangement = Arrangement.spacedBy(Space.small)) {
                            ChromeTextField(minimum, { minimum = it.filter { char -> char in '0'..'9' }.take(12) },
                                Modifier.weight(1f), label = { Text("Minimum") }, singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                isError = rangeError?.startsWith("Enter a valid minimum") == true)
                            ChromeTextField(maximum, { maximum = it.filter { char -> char in '0'..'9' }.take(12) },
                                Modifier.weight(1f), label = { Text("Maximum") }, singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                isError = rangeError?.startsWith("Enter a valid maximum") == true)
                        }
                        ErrorText(rangeError)
                    }
                }
                Row(Modifier.fillMaxWidth().padding(bottom = Space.medium), horizontalArrangement = Arrangement.spacedBy(Space.small)) {
                    TextButton(onClick = {
                        type = "All"; period = "All dates"; category = "All categories"; walletId = ""; minimum = ""; maximum = ""
                        onApply(TransactionFilterState())
                    }, modifier = Modifier.weight(1f).heightIn(min = 48.dp)) { Text("Reset") }
                    MetalButton(onClick = {
                        onApply(TransactionFilterState(type, period, category, walletId, minimum, maximum))
                        onDismiss()
                    }, modifier = Modifier.weight(1f), enabled = rangeError == null) { Text("Apply") }
                }
        }
    }
}
