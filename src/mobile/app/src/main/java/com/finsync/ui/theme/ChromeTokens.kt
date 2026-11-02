package com.finsync.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/** Shared bright chrome palette and geometry for all FinSync surfaces. */
internal object LiquidTokens {
    val night = Color(0xFFECEBFD)
    val nightPanel = Color(0xFFFFFFFF)
    val day = Color(0xFFECEBFD)
    val dayPanel = Color(0xFFFFFFFF)
    val ink = Color(0xFF100D30)
    val white = Color(0xFFFAF9FF)
    val chromeShadow = Color(0xFF363C58)
    val silver = Color(0xFFBCC6DB)
    val ice = Color(0xFFEFFBFF)
    val violet = Color(0xFF6255CD)
    val lavender = Color(0xFFCFBDFF)
    val cyan = Color(0xFF77DFFC)
    val chartBlue = Color(0xFF6D83E9)
    val chartRose = Color(0xFFF17E92)
    val chartAqua = Color(0xFF54BCA9)
    val pink = Color(0xFFF4C9EF)
    val incomeDark = Color(0xFFA4EAD9)
    val incomeLight = Color(0xFF146653)
    val expenseDark = Color(0xFFF4C0DB)
    val expenseLight = Color(0xFF983861)
    val mutedDark = Color(0xFFB9B4CC)
    val mutedLight = Color(0xFF625D75)
    val lineDark = Color(0xFF484258)
    val lineLight = Color(0xFFDDD7EA)
    val pearl = Color(0xFFFAFCFF)
    val mistBlue = Color(0xFFEEEDFC)
    val mistLilac = Color(0xFFF0EDFC)
    val pearlSilver = Color(0xFFC4CFDF)
    val pearlShade = Color(0xFFAAB9CE)
    val pearlRose = Color(0xFFF5E7F3)
    val heroViolet = Color(0xFF6331C5)
    val heroDeep = Color(0xFF382073)
    val cobalt = Color(0xFF334CCA)
    val lilacTile = Color(0xFFECE6FC)
    val pinkTile = Color(0xFFF8E8EE)
    val cyanTile = Color(0xFFE0F1F4)
    val chromeSize = 80.dp
    val compactChromeSize = 56.dp
    val ringSize = 88.dp
    val tileHeight = 80.dp
    val heroShape = RoundedCornerShape(26.dp)
    val panelShape = RoundedCornerShape(24.dp)
    val rowShape = RoundedCornerShape(20.dp)
    val iconSize = 44.dp
    val minTarget = 48.dp
    val progressHeight = 10.dp
    val compact = 360.dp
    val transactionCompact = 256.dp
    const val largeFont = 1.2f
}

/** Shared chrome dock and primary-action tokens. */
internal object DockTokens {
    val mist = Color(0xFFECE8F5)
    val mistBase = Color(0xFFE8E5F8)
    val silver = Color(0xFFD0D5E5)
    val pearl = Color(0xFFF2F0F9)
    val reflection = Color(0xFFFBF9FF)
    val lilac = Color(0xFFE3D7F8)
    val violet = Color(0xFF7960CE)
    val violetLight = Color(0xFF795FD0)
    val cobalt = Color(0xFF49349A)
    val text = Color(0xFF514B64)
    val selectedText = Color(0xFF4D2F99)
    val onAction = Color(0xFFFCFAFF)
    val shadow = Color(0xFF5B4A87)
    val shape = RoundedCornerShape(30.dp)
    val edge = 1.dp
    val elevation = 4.dp
    val quickActionHeight = 48.dp
    val quickActionIcon = 24.dp
    val actionHeight = 56.dp
    val medallion = 32.dp
    val iconInset = 4.dp
}

/** Geometry shared by the Soft Liquid Finance refinement. */
internal object SoftFinanceTokens {
    val avatarSize = 32.dp
    val selectionStroke = 2.dp
    val overviewHeroHeight = 166.dp
    val walletHeroHeight = 120.dp
    val reportHeroHeight = 154.dp
    val reportDonutHeight = 148.dp
    val conceptBudgetRing = 64.dp
    val overviewSparseHeight = 72.dp
    val overviewChartHeight = 144.dp
    val chartStroke = 3.dp
    val chartPoint = 3.dp
    val selectedChartPoint = 6.dp
}

