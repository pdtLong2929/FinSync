package com.finsync.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.finsync.R
import com.finsync.ui.theme.LiquidTokens

internal object MaterialIconTokens {
    val objectSize = 32.dp
    val utilitySize = 24.dp
    val frameInset = 4.dp
}

/** Native multicolor vector art for objects; small utility/selected glyphs stay crisp. */
@Composable
internal fun SculptedIcon(@DrawableRes icon: Int, modifier: Modifier = Modifier,
    description: String? = null, monochrome: Boolean = false, tint: Color = LiquidTokens.violet) {
    val art = when(icon) {
        R.drawable.ic_wallet -> R.drawable.art_wallet
        R.drawable.ic_overview -> R.drawable.art_overview
        R.drawable.ic_groups -> R.drawable.art_people
        R.drawable.ic_reports -> R.drawable.art_chart
        R.drawable.ic_category_gift -> R.drawable.ic_category_gift
        R.drawable.ic_category_education -> R.drawable.ic_category_education
        else -> null
    }
    if(art != null && !monochrome) Image(painterResource(art), description, modifier.size(MaterialIconTokens.utilitySize))
    else Icon(painterResource(icon), description, modifier.size(MaterialIconTokens.utilitySize), tint = tint)
}
