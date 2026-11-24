package com.finsync.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.finsync.R

/** Bundled OFL Space Grotesk. API 26 minimum supports its variable weight axis. */
@OptIn(ExperimentalTextApi::class)
internal val FinSyncDisplayFamily = FontFamily(
    *listOf(300, 400, 500, 600, 700).map { weight ->
        Font(R.font.space_grotesk_variable, FontWeight(weight),
            variationSettings = FontVariation.Settings(FontVariation.weight(weight)))
    }.toTypedArray()
)

// A single angular family keeps headings, forms and figures visually consistent.
internal val FinSyncReadingFamily = FinSyncDisplayFamily

internal val FinSyncTypography = Typography(
    displayLarge = TextStyle(fontFamily = FinSyncDisplayFamily, fontWeight = FontWeight.Medium, fontSize = 57.sp, lineHeight = 64.sp, letterSpacing = (-.2).sp, fontFeatureSettings = "tnum"),
    displayMedium = TextStyle(fontFamily = FinSyncDisplayFamily, fontWeight = FontWeight.Medium, fontSize = 45.sp, lineHeight = 52.sp, letterSpacing = (-.2).sp, fontFeatureSettings = "tnum"),
    displaySmall = TextStyle(fontFamily = FinSyncDisplayFamily, fontWeight = FontWeight.Normal, fontSize = 40.sp, lineHeight = 48.sp, letterSpacing = (-.2).sp, fontFeatureSettings = "tnum"),
    headlineLarge = TextStyle(fontFamily = FinSyncDisplayFamily, fontWeight = FontWeight.Medium, fontSize = 34.sp, lineHeight = 42.sp, letterSpacing = (-.2).sp, fontFeatureSettings = "tnum"),
    headlineMedium = TextStyle(fontFamily = FinSyncDisplayFamily, fontWeight = FontWeight.Medium, fontSize = 30.sp, lineHeight = 38.sp, letterSpacing = (-.1).sp, fontFeatureSettings = "tnum"),
    headlineSmall = TextStyle(fontFamily = FinSyncDisplayFamily, fontWeight = FontWeight.Medium, fontSize = 26.sp, lineHeight = 34.sp, letterSpacing = 0.sp, fontFeatureSettings = "tnum"),
    titleLarge = TextStyle(fontFamily = FinSyncDisplayFamily, fontWeight = FontWeight.Medium, fontSize = 22.sp, lineHeight = 28.sp, letterSpacing = 0.sp),
    titleMedium = TextStyle(fontFamily = FinSyncDisplayFamily, fontWeight = FontWeight.Medium, fontSize = 16.sp, lineHeight = 24.sp, fontFeatureSettings = "tnum"),
    titleSmall = TextStyle(fontFamily = FinSyncReadingFamily, fontWeight = FontWeight.Medium, fontSize = 14.sp, lineHeight = 20.sp),
    bodyLarge = TextStyle(fontFamily = FinSyncReadingFamily, fontSize = 16.sp, lineHeight = 24.sp),
    bodyMedium = TextStyle(fontFamily = FinSyncReadingFamily, fontSize = 14.sp, lineHeight = 22.sp),
    bodySmall = TextStyle(fontFamily = FinSyncReadingFamily, fontSize = 12.sp, lineHeight = 18.sp),
    labelLarge = TextStyle(fontFamily = FinSyncReadingFamily, fontWeight = FontWeight.Medium, fontSize = 14.sp, lineHeight = 20.sp),
    labelMedium = TextStyle(fontFamily = FinSyncReadingFamily, fontWeight = FontWeight.Medium, fontSize = 12.sp, lineHeight = 16.sp),
    labelSmall = TextStyle(fontFamily = FinSyncReadingFamily, fontWeight = FontWeight.Medium, fontSize = 11.sp, lineHeight = 16.sp),
)
