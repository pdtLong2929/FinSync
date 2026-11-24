package com.finsync.auth

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.finsync.BuildConfig
import java.net.URI

/** Public HTTPS deployments do not need broad local-network access. */
internal fun requiresLocalBackendPermission(sdk: Int, debug: Boolean, baseUrl: String): Boolean =
    sdk >= 37 && debug && runCatching { URI(baseUrl).host == "10.0.2.2" }.getOrDefault(false)

internal class LocalBackendAccess(
    val submit: () -> Unit,
    val denied: Boolean,
    val openSettings: () -> Unit,
)

/** Ask only after a user submits a local demo form; keep the form and passwords in memory. */
@Composable
internal fun rememberLocalBackendAccess(onSubmit: () -> Unit): LocalBackendAccess {
    val context = LocalContext.current
    val currentSubmit by rememberUpdatedState(onSubmit)
    var denied by remember { mutableStateOf(false) }
    var requesting by remember { mutableStateOf(false) }
    val required = requiresLocalBackendPermission(Build.VERSION.SDK_INT, BuildConfig.DEBUG, BuildConfig.AUTH_BASE_URL)
    val permission = "android.permission.ACCESS_LOCAL_NETWORK"
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        requesting = false
        denied = !granted
        if (granted) currentSubmit()
    }
    return LocalBackendAccess(
        submit = {
            if (!requesting) {
                if (!required || ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
                    denied = false
                    currentSubmit()
                } else {
                    requesting = true
                    launcher.launch(permission)
                }
            }
        },
        denied = denied,
        openSettings = {
            context.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:${context.packageName}")))
        },
    )
}
