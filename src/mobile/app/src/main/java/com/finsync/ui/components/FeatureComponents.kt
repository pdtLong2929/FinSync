package com.finsync.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.ui.draw.clip
import com.finsync.ui.theme.LiquidTokens
import com.finsync.ui.theme.MetalTokens
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.text.input.KeyboardType
import com.finsync.ui.dashboard.formatVnd
import com.finsync.ui.theme.Space
import com.finsync.ui.theme.LayoutTokens
import androidx.compose.ui.Alignment
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.Instant

enum class LoadState { Ready, Loading, Empty, Error, Offline }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeaturePage(title: String, onBack: (() -> Unit)? = null, dirty: Boolean = false,
                primaryAction: (@Composable () -> Unit)? = null,
                listState: androidx.compose.foundation.lazy.LazyListState = androidx.compose.foundation.lazy.rememberLazyListState(),
                compact: Boolean = false,
                headerAction: (@Composable () -> Unit)? = null,
                content: LazyListScope.() -> Unit) {
    var discard by rememberSaveable { mutableStateOf(false) }
    val back = { if (dirty) discard = true else onBack?.invoke(); Unit }
    BackHandler(enabled = onBack != null, onBack = back)
    Scaffold(
        modifier = Modifier.liquidAtmosphere(),
        containerColor = androidx.compose.ui.graphics.Color.Transparent,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (primaryAction != null) Surface(modifier = Modifier.liquidGlass(MaterialTheme.shapes.large), color = androidx.compose.ui.graphics.Color.Transparent) {
                Box(Modifier.fillMaxWidth().imePadding(), contentAlignment = Alignment.Center) {
                    Column(Modifier.widthIn(max = LayoutTokens.maxContentWidth).fillMaxWidth().padding(Space.medium),
                        verticalArrangement = Arrangement.spacedBy(Space.small)) { primaryAction() }
                }
            }
        },
        topBar = {
            Surface(color = androidx.compose.ui.graphics.Color.Transparent) {
                Row(Modifier.fillMaxWidth().padding(horizontal = Space.medium, vertical = Space.small),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Space.small)) {
                    if (onBack != null) TextButton(onClick = back,
                        modifier = Modifier.heightIn(min = LiquidTokens.minTarget),
                        colors = ButtonDefaults.textButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) { Text("Back") }
                    Text(title, Modifier.weight(1f).semantics { heading() }, style = MaterialTheme.typography.titleLarge)
                    headerAction?.invoke()
                }
            }
        },
    ) { padding ->
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        LazyColumn(Modifier.widthIn(max = LayoutTokens.maxContentWidth).fillMaxSize().padding(padding).consumeWindowInsets(padding).imePadding(),
            contentPadding = PaddingValues(start = Space.medium, end = Space.medium, bottom = Space.fabClearance),
            verticalArrangement = Arrangement.spacedBy(if(compact) Space.small else Space.medium), state = listState, content = content)
        }
    }
    if (discard) MetalDialog(onDismissRequest = { discard = false },
        title = { Text("Discard unfinished changes?") },
        text = { Text("Your unsaved changes will be lost.") },
        confirmButton = { TextButton(onClick = { discard = false; onBack?.invoke() }) { Text("Discard") } },
        dismissButton = { TextButton(onClick = { discard = false }) { Text("Keep editing") } })
}

@Composable
fun DemoNotice() {
    Text("Sample workspace",
        style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
}

@Composable
fun StateNotice(mode: LoadState, empty: Boolean = false, emptyText: String, onRetry: () -> Unit = {},
                content: @Composable () -> Unit) {
    when {
        mode == LoadState.Loading -> LoadingState()
        mode == LoadState.Error -> Column(Modifier.semantics { liveRegion = LiveRegionMode.Polite }) {
            Text("Unable to load this view. Your entries are unchanged.")
            TextButton(onClick = onRetry) { Text("Try again") }
        }
        mode == LoadState.Empty || empty -> EmptyState(emptyText)
        else -> {
            if (mode == LoadState.Offline) Text("Offline - Showing this workspace")
            content()
        }
    }
}

@Composable
fun SectionHeading(text: String) {
    Column(Modifier.padding(top = Space.small), verticalArrangement = Arrangement.spacedBy(Space.small)) {
        Text(text, Modifier.semantics { heading() }, style = MaterialTheme.typography.titleLarge)
    }
}

@Composable
fun AmountField(value: String, onValue: (String) -> Unit, label: String = "Amount (VND)", error: Boolean = false, percentage: Boolean = false,
                errorMessage: String? = null) {
    ChromeTextField(value, onValue, Modifier.fillMaxWidth(), label = { Text(label) }, singleLine = true,
        textStyle = MaterialTheme.typography.headlineLarge,
        shape = MaterialTheme.shapes.large,
        colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        keyboardOptions = KeyboardOptions(keyboardType = if (percentage) KeyboardType.Decimal else KeyboardType.Number), isError = error || errorMessage != null,
        supportingText = { if (errorMessage != null) ErrorText(errorMessage) else Text(if (percentage) "0–100% · Up to 2 decimal places" else value.toLongOrNull()?.let { formatVnd(it) } ?: "Whole VND, no decimal places") })
}

@Composable
fun ErrorText(error: String?) { if (error != null) Text(error, Modifier.semantics { liveRegion = LiveRegionMode.Polite }, color = MaterialTheme.colorScheme.error) }

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChoiceChips(options: List<String>, selected: String, onSelect: (String) -> Unit) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(Space.small), verticalArrangement = Arrangement.spacedBy(Space.small)) {
        options.forEach { label -> FilterChip(selected == label, { onSelect(label) }, label = { Text(label) },
            modifier = Modifier.heightIn(min=LiquidTokens.minTarget).clip(MaterialTheme.shapes.medium)
                .then(if(selected == label) Modifier.liquidSelected(action=true) else Modifier.liquidGlass(MaterialTheme.shapes.medium, androidx.compose.ui.unit.Dp(0f))),
            shape = MaterialTheme.shapes.medium,
            border = null,
            colors = FilterChipDefaults.filterChipColors(
                containerColor = androidx.compose.ui.graphics.Color.Transparent,
                selectedContainerColor = androidx.compose.ui.graphics.Color.Transparent,
                selectedLabelColor = com.finsync.ui.theme.DockTokens.onAction)) }
    }
}

@Composable
fun LedgerRow(title: String, subtitle: String, value: String, onClick: (() -> Unit)? = null, icon: String? = null,
    tone: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.primary, category: String? = null, categoryId: String? = null, compact: Boolean = false) {
    val modifier = Modifier.fillMaxWidth().liquidGlass(MaterialTheme.shapes.medium, androidx.compose.ui.unit.Dp(2f))
        .then(if (onClick != null) Modifier.clickable(role = Role.Button, onClick = onClick) else Modifier)
    Row(modifier.padding(Space.medium), horizontalArrangement = Arrangement.spacedBy(Space.medium), verticalAlignment = Alignment.CenterVertically) {
        val inline = compact && androidx.compose.ui.platform.LocalDensity.current.fontScale <= LiquidTokens.largeFont
        if (category != null) CategoryBadge(category, categoryId) else Box(Modifier.size(MetalTokens.badge).clip(MaterialTheme.shapes.small).metalFinish(hero = true), contentAlignment = Alignment.Center) {
            if (icon != null) Text(icon, color = LiquidTokens.ink, style = MaterialTheme.typography.titleMedium)
            else CategoryBadge(title)
        }
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(Space.small)) {
            Text(title, style = if(compact) MaterialTheme.typography.titleSmall else MaterialTheme.typography.titleMedium)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            if(!inline) Text(value, style = MaterialTheme.typography.titleMedium.copy(fontFeatureSettings = "tnum"), color = tone)
        }
        if(inline) Text(value, style = MaterialTheme.typography.labelLarge.copy(fontFeatureSettings = "tnum"), color = tone)
    }
}

@Composable
fun PrivacyButton(hidden: Boolean, onToggle: () -> Unit) {
    TextButton(onClick = onToggle, colors = ButtonDefaults.textButtonColors(contentColor = LocalContentColor.current)) {
        Text(if (hidden) "Show amounts" else "Hide amounts")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateField(value: String, onValue: (String) -> Unit, label: String = "Date (YYYY-MM-DD)", errorMessage: String? = null) {
    var picker by remember { mutableStateOf(false) }
    ChromeTextField(value, onValue, Modifier.fillMaxWidth(), label = { Text(label) }, singleLine = true,
        isError = errorMessage != null, supportingText = errorMessage?.let { message -> { ErrorText(message) } },
        trailingIcon = { TextButton(onClick = { picker = true }) { Text("Pick") } })
    if (picker) {
        val initial = runCatching { LocalDate.parse(value).atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli() }.getOrNull()
        val state = rememberDatePickerState(initialSelectedDateMillis = initial)
        DatePickerDialog(onDismissRequest = { picker = false }, modifier=Modifier.liquidGlass(MaterialTheme.shapes.extraLarge),
            colors=DatePickerDefaults.colors(containerColor=LiquidSurfaceTokens.frost),
            confirmButton = { TextButton(onClick = {
                state.selectedDateMillis?.let { onValue(Instant.ofEpochMilli(it).atZone(ZoneOffset.UTC).toLocalDate().toString()) }
                picker = false
            }) { Text("Choose date") } }, dismissButton = { TextButton(onClick = { picker = false }) { Text("Cancel") } }) {
            DatePicker(state)
        }
    }
}

@Composable
fun OptionPicker(label: String, selected: String, options: List<String>, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Column {
        Text(label, style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(bottom = Space.small))
        MetalOutlineButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) { Text(selected.ifBlank { "Choose $label" }) }
        DropdownMenu(expanded, { expanded = false }) {
            options.forEach { option -> DropdownMenuItem(text = { Text(option) }, onClick = { onSelect(option); expanded = false }) }
        }
    }
}


/** One field surface/shape across search, forms and pickers; native input behavior retained. */
@Composable
fun ChromeTextField(
    value: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier,
    label: @Composable (() -> Unit)? = null, singleLine: Boolean = false,
    minLines: Int = 1, supportingText: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null, isError: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    enabled: Boolean = true,
    visualTransformation: androidx.compose.ui.text.input.VisualTransformation = androidx.compose.ui.text.input.VisualTransformation.None,
    keyboardActions: androidx.compose.foundation.text.KeyboardActions = androidx.compose.foundation.text.KeyboardActions.Default,
    textStyle: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.bodyLarge,
    shape: androidx.compose.ui.graphics.Shape = MaterialTheme.shapes.medium,
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors(
        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
) {
    OutlinedTextField(value, onValueChange, modifier, label = label, singleLine = singleLine,
        minLines = minLines, supportingText = supportingText, trailingIcon = trailingIcon,
        isError = isError, keyboardOptions = keyboardOptions, textStyle = textStyle,
        enabled = enabled, visualTransformation = visualTransformation, keyboardActions = keyboardActions,
        shape = shape, colors = colors)
}
