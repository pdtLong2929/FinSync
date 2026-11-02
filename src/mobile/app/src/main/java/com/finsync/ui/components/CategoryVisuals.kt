package com.finsync.ui.components

import androidx.compose.runtime.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import com.finsync.R
import com.finsync.domain.*
import com.finsync.ui.theme.*

val LocalCategories = staticCompositionLocalOf { defaultCategories }
fun categoryIconResource(icon: CategoryIcon): Int = when(icon) {
    CategoryIcon.Food -> R.drawable.ic_category_food
    CategoryIcon.Shopping -> R.drawable.ic_category_shopping
    CategoryIcon.Transport -> R.drawable.ic_category_transport
    CategoryIcon.Housing -> R.drawable.ic_category_housing
    CategoryIcon.Utilities -> R.drawable.ic_category_utilities
    CategoryIcon.Entertainment -> R.drawable.ic_category_entertainment
    CategoryIcon.Health -> R.drawable.ic_category_health
    CategoryIcon.Education -> R.drawable.ic_category_education
    CategoryIcon.Travel -> R.drawable.ic_category_travel
    CategoryIcon.Salary -> R.drawable.ic_category_salary
    CategoryIcon.Gift -> R.drawable.ic_category_gift
    CategoryIcon.Other -> R.drawable.ic_category_other
}
@Composable fun CategoryBadge(name: String, categoryId: String? = null) {
    val categories = LocalCategories.current
    val category = categories.find { it.id == categoryId } ?: categories.find { it.name == name }
        ?: categories.find { name == "Dining" && it.icon == CategoryIcon.Food }
    val color = when(category?.color) {
        CategoryColor.Cyan -> LiquidTokens.cyanTile
        CategoryColor.Rose -> LiquidTokens.pinkTile
        else -> LiquidTokens.lilacTile
    }
    Box(Modifier.size(MetalTokens.badge).liquidGlass(MaterialTheme.shapes.small,androidx.compose.ui.unit.Dp(1f),color), contentAlignment = Alignment.Center) {
        Icon(painterResource(categoryIconResource(category?.icon ?: CategoryIcon.Other)),
            contentDescription = "$name category", tint = LiquidTokens.ink,
            modifier = Modifier.semantics { stateDescription = "${category?.icon ?: CategoryIcon.Other} · ${category?.color ?: CategoryColor.Lavender}" })
    }
}
