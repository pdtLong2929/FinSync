package com.finsync.ui

import androidx.activity.compose.BackHandler
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import com.finsync.ui.components.metalFinish
import com.finsync.ui.theme.MetalTokens
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.IconButton
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.runtime.DisposableEffect
import androidx.core.view.WindowCompat
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.role
import androidx.compose.ui.tooling.preview.Preview
import com.finsync.R
import com.finsync.ui.components.ChromeNavigation
import com.finsync.ui.components.ChromeTab
import com.finsync.ui.dashboard.DashboardScreen
import com.finsync.ui.dashboard.DashboardUiState
import com.finsync.ui.dashboard.DemoDashboard
import com.finsync.ui.components.chromePalette
import com.finsync.ui.theme.LiquidTokens
import com.finsync.ui.theme.DockTokens
import com.finsync.ui.components.ChromeExpenseAction
import com.finsync.ui.components.chromeDockBackdrop
import com.finsync.ui.components.chromeNavigationSurface
import com.finsync.ui.components.chromeSelectedFinish
import com.finsync.ui.theme.FinSyncTheme
import com.finsync.ui.theme.LayoutTokens
import com.finsync.ui.theme.Space
import kotlinx.coroutines.launch
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.key
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import com.finsync.ui.wallets.*
import com.finsync.ui.transactions.*
import com.finsync.ui.dashboard.toDashboardData
import com.finsync.ui.components.liquidAtmosphere
import com.finsync.ui.components.liquidLens
import com.finsync.ui.groups.GroupFlow
import com.finsync.ui.reports.ReportFlow

private enum class Destination(@param:StringRes val label: Int, @param:DrawableRes val icon: Int) {
    Overview(R.string.overview, R.drawable.ic_overview),
    Wallets(R.string.wallets, R.drawable.ic_wallet),
    Groups(R.string.groups, R.drawable.ic_groups),
    Reports(R.string.reports, R.drawable.ic_reports),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinSyncApp(dashboardState: DashboardUiState, onRetry: () -> Unit = {}, finance: FinanceViewModel = remember { FinanceViewModel() }, onAccount: (() -> Unit)? = null) {
    val accountProfile = com.finsync.auth.LocalAccountProfile.current
    val financeState by finance.uiState.collectAsStateWithLifecycle()
    var route by rememberSaveable { mutableStateOf("") }
    var transactionOrigin by rememberSaveable { mutableStateOf("") }
    var destinationName by rememberSaveable { mutableStateOf(Destination.Overview.name) }
    val destination = Destination.valueOf(destinationName)
    val liquidDashboard = destination == Destination.Overview && route.isEmpty()
    val dashboardColors = chromePalette()
    val dashboardLargeText = LocalDensity.current.fontScale > LiquidTokens.largeFont
    val hostView = LocalView.current
    // All destinations use the bright pearl theme; preserve host flags on exit.
    DisposableEffect(liquidDashboard, hostView) {
        val window = (hostView.context as? android.app.Activity)?.window
        val controller = window?.let { WindowCompat.getInsetsController(it, hostView) }
        val previousStatus = controller?.isAppearanceLightStatusBars
        val previousNavigation = controller?.isAppearanceLightNavigationBars
        run {
            controller?.isAppearanceLightStatusBars = true
            controller?.isAppearanceLightNavigationBars = true
        }
        onDispose {
            run {
                previousStatus?.let { controller?.isAppearanceLightStatusBars = it }
                previousNavigation?.let { controller?.isAppearanceLightNavigationBars = it }
            }
        }
    }
    val focusManager = LocalFocusManager.current
    val keyboard = LocalSoftwareKeyboardController.current
    LaunchedEffect(route, destinationName) {
        focusManager.clearFocus()
        keyboard?.hide()
    }
    var amountsHidden by rememberSaveable { mutableStateOf(false) }
    var showLedgerPicker by rememberSaveable { mutableStateOf(false) }
    val snackbarHost = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val savedScreens = rememberSaveableStateHolder()
    val quickExpense = {
        if (destination == Destination.Groups) {
            val groups = financeState.groups.filter { it.joined }
            if (groups.size == 1) route = "group/${groups.first().id}/expense" else showLedgerPicker = true
        } else { transactionOrigin = ""; route = "transaction/new" }
    }
    val notifySaved: (String) -> Unit = { message -> scope.launch {
        snackbarHost.currentSnackbarData?.dismiss()
        snackbarHost.showSnackbar(message)
    } }
    val navigateTo: (String) -> Unit = { next ->
        snackbarHost.currentSnackbarData?.dismiss()
        route = next
    }
    BackHandler(enabled = destination != Destination.Overview && !showLedgerPicker && route.isEmpty()) {
        destinationName = Destination.Overview.name
    }

    BoxWithConstraints {
        val useRail = maxWidth >= LayoutTokens.navigationRailBreakpoint
        Row(Modifier.fillMaxSize()) {
            if (useRail && route.isEmpty()) NavigationRail(
                containerColor = if (liquidDashboard) dashboardColors.background else MaterialTheme.colorScheme.surface,
            ) {
                Destination.entries.forEach { item ->
                    NavigationRailItem(
                        selected = destination == item,
                        colors = if (liquidDashboard) NavigationRailItemDefaults.colors(
                            selectedIconColor = if (dashboardColors.dark) LiquidTokens.ink else LiquidTokens.white,
                            selectedTextColor = dashboardColors.accent, indicatorColor = dashboardColors.accent,
                            unselectedIconColor = dashboardColors.muted, unselectedTextColor = dashboardColors.muted,
                        ) else NavigationRailItemDefaults.colors(),
                        onClick = { destinationName = item.name },
                        icon = { Icon(painterResource(item.icon), contentDescription = null) },
                        label = { Text(stringResource(item.label)) },
                    )
                }
            }
            Scaffold(
                modifier = Modifier.weight(1f).then(if(route.isEmpty()) Modifier.liquidAtmosphere() else Modifier),
                containerColor = if(route.isEmpty()) androidx.compose.ui.graphics.Color.Transparent else MaterialTheme.colorScheme.background,
                topBar = {
                    if (route.isEmpty() && destination == Destination.Overview)
                    TopAppBar(colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = androidx.compose.ui.graphics.Color.Transparent, titleContentColor = dashboardColors.text,
                    ), title = {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Space.small)) {
                            Box(Modifier.size(DockTokens.medallion).liquidLens(), contentAlignment = Alignment.Center) {
                                Icon(painterResource(R.drawable.ic_brand_monogram), null, Modifier.size(DockTokens.quickActionIcon), tint = LiquidTokens.violet)
                            }
                            Text(stringResource(R.string.app_name), style = MaterialTheme.typography.titleMedium)
                        }
                    }, actions = {
                        if (onAccount != null) IconButton(onClick = onAccount,
                            modifier = Modifier.size(LiquidTokens.minTarget).semantics { contentDescription = "Account" }) {
                            com.finsync.auth.UserAvatar(accountProfile?.displayName, accountProfile?.avatarPath,
                                Modifier.size(DockTokens.medallion), compact = true)
                        }
                    })
                },
                bottomBar = {
                    // Reserve space for feedback instead of overlaying form actions or list rows.
                    Column(Modifier) {
                    SnackbarHost(snackbarHost, modifier = if ((useRail || route.isNotEmpty()) &&
                        snackbarHost.currentSnackbarData != null) Modifier.navigationBarsPadding() else Modifier)
                    if (useRail && route.isEmpty()) ChromeExpenseAction(quickExpense, if (destination == Destination.Groups) "Add group expense" else "Add expense")
                    if (!useRail && route.isEmpty()) ChromeNavigation(
                        Destination.entries.map { ChromeTab(it.name, it.label, it.icon) },
                        destination.name, onSelect = { destinationName = it }, onAdd = quickExpense,
                        overviewFinish = true,
                        addLabel = if(destination == Destination.Groups) "Add group expense" else "Add expense",
                    )
                    }
                },
            ) { padding ->
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.TopCenter) {
                    val contentModifier = Modifier.widthIn(max = LayoutTokens.maxContentWidth).fillMaxWidth()
                    val screen: @Composable () -> Unit = {
                    if (route == "transactions" || route.startsWith("transaction/")) key(route) {
                        val entry = financeState.entries.find { it.id == route.substringAfterLast('/') }
                        when {
                            route == "transaction/new" || route.startsWith("transaction/edit/") -> TransactionFormScreen(financeState, entry,
                                { route = if (entry == null) transactionOrigin else "transaction/${entry.id}" },
                                { amount, walletId, type, category, date, note ->
                                    val error = finance.saveTransaction(entry?.id, amount, walletId, type, category, date, note)
                                    if (error == null) { route = if (entry == null) "transactions" else "transaction/${entry.id}"; scope.launch { snackbarHost.showSnackbar("Transaction saved") } }
                                    error
                                }, finance::addCategory, finance::setCategoryVisual)
                            entry != null -> TransactionDetailsScreen(entry, financeState.wallets.find { it.id == entry.walletId }, amountsHidden,
                                { route = transactionOrigin }, { route = "transaction/edit/${entry.id}" }, {
                                    finance.deleteTransaction(entry.id)
                                    route = transactionOrigin.ifBlank {
                                        if (destination == Destination.Overview) "transactions" else ""
                                    }
                                    scope.launch {
                                        snackbarHost.currentSnackbarData?.dismiss()
                                        if (snackbarHost.showSnackbar("Transaction deleted", actionLabel = "Undo") == SnackbarResult.ActionPerformed)
                                            finance.restoreTransaction(entry)
                                    }
                                })
                            else -> TransactionHistoryScreen(financeState, amountsHidden, { amountsHidden = !amountsHidden }, { route = "" },
                                { transactionOrigin = "transactions"; route = "transaction/$it" }, { transactionOrigin = "transactions"; route = "transaction/new" })
                        }
                    } else if (destination == Destination.Wallets) key(route) {
                        val id = route.substringAfterLast('/')
                        val wallet = financeState.wallets.find { it.id == id }
                        when {
                            route == "transfer/new" -> TransferScreen(financeState, { navigateTo("") }, { navigateTo("transfer/history") }, finance::transferWallets)
                            route == "transfer/history" -> TransferHistoryScreen(financeState, amountsHidden, { navigateTo("") })
                            route == "wallet/new" || route.startsWith("wallet/edit/") -> WalletFormScreen(wallet, { navigateTo(if (wallet == null) "" else "wallet/${wallet.id}") }) { name, type, opening ->
                                val error = finance.saveWallet(wallet?.id, name, type, opening)
                                if (error == null) { navigateTo(""); notifySaved("Wallet saved") }
                                error
                            }
                            route.startsWith("wallet/history/") && wallet != null -> WalletHistoryScreen(wallet, financeState.entries, amountsHidden, { navigateTo("wallet/${wallet.id}") }, { transactionOrigin = route; navigateTo("transaction/$it") })
                            wallet != null -> WalletDetailsScreen(wallet, financeState.entries, amountsHidden, { amountsHidden = !amountsHidden },
                                { navigateTo("") }, { navigateTo("wallet/edit/${wallet.id}") }, { navigateTo("wallet/history/${wallet.id}") }, { finance.archiveWallet(wallet.id) })
                            else -> WalletListScreen(financeState, amountsHidden, { amountsHidden = !amountsHidden }, { navigateTo("wallet/$it") }, { navigateTo("wallet/new") }, onHistory = { navigateTo("transactions") }, onTransfer = { navigateTo("transfer/new") }, onTransferHistory = { navigateTo("transfer/history") })
                        }
                    } else if (destination == Destination.Groups) GroupFlow(financeState, finance, route,
                        navigateTo, notifySaved)
                    else if (destination == Destination.Reports) ReportFlow(financeState, finance, route,
                        navigateTo, notifySaved, onTransaction = { transactionOrigin = route; navigateTo("transaction/$it") })
                    else if (destination == Destination.Overview) DashboardScreen(
                        state = if (dashboardState is DashboardUiState.Content) dashboardState.copy(data = financeState.toDashboardData()) else dashboardState,
                        amountsHidden = amountsHidden,
                        onToggleAmounts = { amountsHidden = !amountsHidden },
                        onGroups = { destinationName = Destination.Groups.name },
                        onCreateTransaction = quickExpense,
                        onRetry = onRetry,
                        modifier = contentModifier,
                        onTransaction = { transactionOrigin = ""; route = "transaction/$it" },
                        onHistory = { route = "transactions" },
                        onBudgets = { destinationName = Destination.Reports.name; route = "budgets" },
                    ) else PlaceholderScreen(destination, { destinationName = Destination.Overview.name }, contentModifier)
                    }
                    // Retain browsing state, never retain submitted or discarded form drafts.
                    val browsing = route.isEmpty() || route == "transactions" || route.startsWith("wallet/history/") ||
                        route.substringBefore('?') in listOf("budgets", "goals", "charts", "charts/trend", "analytics", "categories", "insights") ||
                        (route.startsWith("group/") && !route.endsWith("/expense") && !route.endsWith("/members") && !route.contains("/settle/"))
                    androidx.compose.runtime.CompositionLocalProvider(com.finsync.ui.components.LocalCategories provides financeState.categories) {
                        if (browsing) savedScreens.SaveableStateProvider("$destinationName/$route", screen) else screen()
                    }
                }
            }
        }
    }
    if (showLedgerPicker) ModalBottomSheet(onDismissRequest = { showLedgerPicker = false },containerColor=com.finsync.ui.components.LiquidSurfaceTokens.frost) {
        Text("Choose a group", Modifier.padding(Space.large), style = MaterialTheme.typography.headlineSmall)
        val groups = financeState.groups.filter { it.joined }
        if (groups.isEmpty()) Text("Create or join a group first.", Modifier.padding(Space.large))
        LazyColumn(Modifier.fillMaxWidth()) {
            items(groups, key = { it.id }) { group ->
                ListItem(
                    headlineContent = { Text(group.name) },
                    supportingContent = { Text("Shared ledger · ${group.members.count { it.active }} members") },
                    modifier = Modifier.clickable(role = Role.Button) {
                        showLedgerPicker = false
                        route = "group/${group.id}/expense"
                    }.padding(bottom = Space.small),
                )
            }
        }
    }
}

@Composable
private fun PlaceholderScreen(destination: Destination, onOverview: () -> Unit, modifier: Modifier) {
    val description = when (destination) {
        Destination.Wallets -> R.string.wallets_placeholder
        Destination.Groups -> R.string.groups_placeholder
        else -> R.string.reports_placeholder
    }
    Column(modifier.padding(Space.large), verticalArrangement = Arrangement.spacedBy(Space.medium)) {
        Text(stringResource(R.string.placeholder, stringResource(destination.label)), style = MaterialTheme.typography.headlineSmall)
        Text(stringResource(description), style = MaterialTheme.typography.bodyLarge)
        TextButton(onClick = onOverview) { Text(stringResource(R.string.back_overview)) }
    }
}

@Preview(name = "App phone", widthDp = 393, heightDp = 852)
@Preview(name = "App tablet", widthDp = 1000, heightDp = 900)
@Composable
private fun FinSyncAppPreview() {
    FinSyncTheme { FinSyncApp(DashboardUiState.Content(DemoDashboard.data)) }
}
