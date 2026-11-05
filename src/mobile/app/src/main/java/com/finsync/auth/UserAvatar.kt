package com.finsync.auth

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.finsync.R
import com.finsync.ui.components.liquidLens
import com.finsync.ui.theme.DockTokens
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

val LocalAccountProfile = staticCompositionLocalOf<LocalProfile?> { null }

@Composable
fun UserAvatar(name: String?, avatarPath: String?, modifier: Modifier = Modifier, compact: Boolean = false) {
    val photo by produceState<ImageBitmap?>(null, avatarPath) {
        value = null
        if (avatarPath != null) value = withContext(Dispatchers.IO) {
            runCatching { BitmapFactory.decodeFile(avatarPath)?.asImageBitmap() }.getOrNull()
        }
    }
    Box(modifier.clip(CircleShape).liquidLens(), contentAlignment = Alignment.Center) {
        when {
            photo != null -> Image(photo!!, null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            !name.isNullOrBlank() -> Text(profileInitials(name), style = if (compact) MaterialTheme.typography.labelLarge else MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary)
            else -> Icon(painterResource(R.drawable.ic_person), null, Modifier.size(DockTokens.quickActionIcon),
                tint = MaterialTheme.colorScheme.primary)
        }
    }
}
