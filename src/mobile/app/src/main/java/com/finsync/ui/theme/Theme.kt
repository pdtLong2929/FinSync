package com.finsync.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

data class FinanceSemanticColors(val warning: Color, val income: Color = LiquidTokens.incomeLight, val expense: Color = LiquidTokens.expenseLight)
val LocalFinanceColors = staticCompositionLocalOf { FinanceSemanticColors(Color(0xFF92400E)) }

private val LightColors = lightColorScheme(
    primary = LiquidTokens.violet, onPrimary = DockTokens.onAction,
    primaryContainer = DockTokens.lilac, onPrimaryContainer = LiquidTokens.ink,
    secondary = LiquidTokens.incomeLight, onSecondary = DockTokens.onAction,
    secondaryContainer = LiquidTokens.cyanTile, onSecondaryContainer = LiquidTokens.ink,
    tertiary = LiquidTokens.expenseLight, onTertiary = DockTokens.onAction,
    tertiaryContainer = LiquidTokens.pinkTile, onTertiaryContainer = LiquidTokens.ink,
    background = LiquidTokens.day, onBackground = LiquidTokens.ink,
    surface = LiquidTokens.day, onSurface = LiquidTokens.ink,
    surfaceVariant = DockTokens.mistBase, onSurfaceVariant = DockTokens.text,
    surfaceContainerLowest = LiquidTokens.pearl, surfaceContainerLow = Color(0xFFF5F3FB),
    surfaceContainer = DockTokens.mist, surfaceContainerHigh = DockTokens.lilac,
    surfaceContainerHighest = DockTokens.silver,
    surfaceDim = DockTokens.mistBase, surfaceBright = LiquidTokens.pearl,
    outline = Color(0xFF756D86), outlineVariant = Color(0xFFCBC4D9),
    error = Color(0xFFAD2943), onError = DockTokens.onAction,
    errorContainer = Color(0xFFF8DDE3), onErrorContainer = Color(0xFF6A1730),
    inverseSurface = DockTokens.mistBase, inverseOnSurface = LiquidTokens.ink,
    inversePrimary = DockTokens.selectedText, surfaceTint = LiquidTokens.violet,
)

@Composable
fun FinSyncTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    // Static brand schemes keep fictional previews reproducible on every device.
    CompositionLocalProvider(LocalFinanceColors provides FinanceSemanticColors(
        Color(0xFF8C4C08))) {
    MaterialTheme(
        // Bright brand appearance under both Android settings, as requested.
        colorScheme = LightColors,
        typography = FinSyncTypography,
        shapes = Shapes(
            small = RoundedCornerShape(MetalTokens.fieldRadius),
            medium = RoundedCornerShape(MetalTokens.fieldRadius),
            large = RoundedCornerShape(MetalTokens.radius),
            extraLarge = RoundedCornerShape(Space.extraLarge)),
        content = content,
    )
    }
}
