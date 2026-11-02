package com.finsync.ui.components

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.finsync.R
import com.finsync.ui.components.liquidGlass
import com.finsync.ui.components.liquidLens
import com.finsync.ui.components.liquidSelected
import com.finsync.ui.theme.DockTokens
import com.finsync.ui.theme.LiquidTokens
import com.finsync.ui.theme.Space

internal data class ChromeTab(val key: String, @param:StringRes val label: Int, @param:DrawableRes val icon: Int)

internal object ChromeNavigationTokens {
    val icon = 20.dp
    val indicatorWidth = 64.dp
    val indicatorHeight = 26.dp
    val minimumTarget = 56.dp
    val gap = 2.dp
    val dockShape = RoundedCornerShape(20.dp)
    val tileShape = RoundedCornerShape(14.dp)
}

/** Equal slots and a common vertical center; large labels retain the full four-tab row. */
@Composable
internal fun ChromeNavigation(tabs: List<ChromeTab>, selectedKey: String, onAdd: (() -> Unit)? = null, addLabel: String = "Add expense", overviewFinish: Boolean = false, onSelect: (String) -> Unit) {
    val largeText = LocalDensity.current.fontScale > LiquidTokens.largeFont
    val action: @Composable () -> Unit = {
        if(onAdd != null) FilledIconButton(onClick = onAdd,
            modifier = Modifier.size(DockTokens.actionHeight)
                .then(if(overviewFinish) Modifier.liquidLens(ChromeNavigationTokens.tileShape)
                    else Modifier.chromeNavigationSurface(ChromeNavigationTokens.tileShape))
                .semantics { contentDescription = addLabel },
            shape=ChromeNavigationTokens.tileShape,
            colors=IconButtonDefaults.filledIconButtonColors(containerColor=Color.Transparent,
                contentColor=if(overviewFinish) DockTokens.onAction else DockTokens.selectedText)) {
            Icon(painterResource(R.drawable.ic_add), null, Modifier.size(DockTokens.medallion))
        }
    }
    Column(Modifier.fillMaxWidth().padding(horizontal=Space.small,vertical=Space.small).navigationBarsPadding(),
        horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(Space.small)) {
        if(largeText && onAdd != null) action()
        Row(Modifier.fillMaxWidth()
            .then(if(overviewFinish) Modifier.liquidGlass(ChromeNavigationTokens.dockShape)
                else Modifier.chromeNavigationSurface(ChromeNavigationTokens.dockShape))
            .padding(6.dp).selectableGroup(),verticalAlignment=Alignment.CenterVertically) {
            tabs.forEachIndexed { index, tab ->
                if(index==tabs.size/2 && onAdd!=null && !largeText)
                    Box(Modifier.weight(1f),contentAlignment=Alignment.Center) { action() }
                val active=tab.key==selectedKey
                Box(Modifier.weight(1f),contentAlignment=Alignment.Center) {
                    Column(Modifier.fillMaxWidth().heightIn(min=ChromeNavigationTokens.minimumTarget)
                        .testTag("chrome-tab-${tab.key}").clip(ChromeNavigationTokens.tileShape)
                        .then(if(active && overviewFinish) Modifier.liquidSelected()
                            else if(active) Modifier.chromeSelectedFinish() else Modifier)
                        .selectable(selected=active,role=Role.Tab,onClick={onSelect(tab.key)})
                        .padding(vertical=ChromeNavigationTokens.gap),
                        horizontalAlignment=Alignment.CenterHorizontally,
                        verticalArrangement=Arrangement.spacedBy(ChromeNavigationTokens.gap,Alignment.CenterVertically)) {
                        Box(Modifier.height(ChromeNavigationTokens.indicatorHeight).fillMaxWidth()
                            .testTag("chrome-icon-${tab.key}"),contentAlignment=Alignment.Center) {
                            Icon(painterResource(tab.icon),null,Modifier.size(ChromeNavigationTokens.icon),
                                tint=if(active) DockTokens.onAction else DockTokens.text)
                        }
                        Text(stringResource(tab.label),Modifier.fillMaxWidth().testTag("chrome-label-${tab.key}"),
                            style=MaterialTheme.typography.labelSmall.copy(fontSize=10.sp),
                            fontWeight=FontWeight.SemiBold,textAlign=TextAlign.Center,maxLines=1,softWrap=false,
                            color=if(active) DockTokens.onAction else DockTokens.text)
                    }
                }
            }
        }
    }
}

